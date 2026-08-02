package misakplak.deathLogging.replay;

import misakplak.deathLogging.recordables.Position;
import misakplak.deathLogging.recordables.Recordable;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

public record Replay(
        UUID replayId,
        UUID playerId,
        Position deathlocation,
        UUID killerId,
        long createdAt,
        List<Recordable> records
) {
    public List<Recordable> getRecords() {
        return records;
    }
    public UUID getReplayId() {
        return replayId;
    }
    public long getCreatedAt() {
        return createdAt;
    }

    public Location getDeathlocation() {
        return Position.toLocation(deathlocation, Bukkit.getWorld("world"));
    }
}
