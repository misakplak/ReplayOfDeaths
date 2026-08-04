package misakplak.deathLogging.database;

import misakplak.deathLogging.DeathLogging;
import misakplak.deathLogging.replay.Replay;
import org.bson.Document;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MySqlReplayStorage implements ReplayStorage {

    private final DataSource dataSource;
    private final ReplayCodec codec = new ReplayCodec();


    public MySqlReplayStorage(DataSource dataSource) {
        this.dataSource = dataSource;
        createTable();
    }



    @Override
    public void save(Replay replay) {
        Document doc = codec.toDocument(replay);

        String sql = "INSERT INTO replays (replay_id, player_id, killer_id, created_at, data) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, replay.getReplayId().toString());
            stmt.setString(2, replay.playerId().toString());
            stmt.setString(3, replay.killerId() == null ? null : replay.killerId().toString());
            stmt.setLong(4, replay.createdAt());
            stmt.setString(5, doc.toJson());
            stmt.executeUpdate();

        } catch (SQLException e) {
            DeathLogging.getInstance().getLogger().warning("Failed to save replay: " + e.getMessage());
        }
    }

    @Override
    public Replay load(UUID replayId) {
        String sql = "SELECT data FROM replays WHERE replay_id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, replayId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return codec.fromDocument(replayId, Document.parse(rs.getString("data")));
            }

        } catch (SQLException e) {
            DeathLogging.getInstance().getLogger().warning("Failed to load replay: " + e.getMessage());
            return null;
        }
    }

    @Override
    public Replay loadLatest() {
        String sql = "SELECT replay_id, data FROM replays ORDER BY created_at DESC LIMIT 1";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (!rs.next()) return null;

            UUID replayId = UUID.fromString(rs.getString("replay_id"));
            return codec.fromDocument(replayId, Document.parse(rs.getString("data")));

        } catch (SQLException e) {
            DeathLogging.getInstance().getLogger().warning("Failed to load latest replay: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<ReplaySummary> list(UUID playerId, boolean asKiller, int page, int pageSize) {
        String column = asKiller ? "killer_id" : "player_id";
        String sql = "SELECT replay_id, created_at FROM replays WHERE " + column
                + " = ? ORDER BY created_at DESC LIMIT ? OFFSET ?";

        List<ReplaySummary> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerId.toString());
            stmt.setInt(2, pageSize);
            stmt.setInt(3, page * pageSize);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(new ReplaySummary(
                            UUID.fromString(rs.getString("replay_id")),
                            rs.getLong("created_at")
                    ));
                }
            }

        } catch (SQLException e) {
            DeathLogging.getInstance().getLogger().warning("Failed to list replays: " + e.getMessage());
        }

        return results;
    }

    private void createTable() {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS replays (
                    replay_id CHAR(36) PRIMARY KEY,
                    player_id CHAR(36) NOT NULL,
                    killer_id CHAR(36) NULL,
                    created_at BIGINT NOT NULL,
                    data LONGTEXT NOT NULL,
                    INDEX idx_player (player_id),
                    INDEX idx_killer (killer_id)
                )
                """);
        } catch (SQLException e) {
            DeathLogging.getInstance().getLogger().warning("Failed to create replays table: " + e.getMessage());
        }
    }

    @Override
    public int count(UUID playerId, boolean asKiller) {
        String column = asKiller ? "killer_id" : "player_id";
        String sql = "SELECT COUNT(*) FROM replays WHERE " + column + " = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, playerId.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }

        } catch (SQLException e) {
            DeathLogging.getInstance().getLogger().warning("Failed to count replays: " + e.getMessage());
            return 0;
        }
    }
}

