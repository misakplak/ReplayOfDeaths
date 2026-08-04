package misakplak.deathLogging.database;

import misakplak.deathLogging.DeathLogging;
import misakplak.deathLogging.replay.Replay;
import org.bson.Document;

import javax.sql.DataSource;
import java.sql.*;
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

        String sql = "INSERT INTO replays (replayId, playerId, killerId,  created_at, data) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)){

            stmt.setString(1, replay.getReplayId().toString());
            stmt.setString(2, replay.playerId().toString());
            stmt.setString(3, replay.killerId() == null ? null : replay.killerId().toString());
            stmt.setLong(4, replay.createdAt());
            stmt.setString(5, doc.toJson());
            stmt.executeUpdate();

        } catch (SQLException e) {
            DeathLogging.getInstance().getLogger().warning("failed to save replay " + e.getMessage());
        }




    }

    @Override
    public Replay load(UUID replayId) {
        String sql = "SELECT data FROM replays WHERE replayId = ?";
        try (Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, replayId.toString());
            try(ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return codec.fromDocument(replayId, Document.parse(rs.getString("data")));
            }

        }catch (SQLException e){
            DeathLogging.getInstance().getLogger().warning("failed to load replay " + e.getMessage());
            return null;
        }
    }

    @Override
    public Replay loadLatest() {
        return null;
    }

    @Override
    public List<ReplaySummary> list(UUID playerId, boolean asKiller, int page, int pageSize) {
        return List.of();
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
}

