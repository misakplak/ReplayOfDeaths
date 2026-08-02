package misakplak.deathLogging.recordables;

import org.bukkit.entity.EntityType;

import java.util.UUID;

public record EntitySpawnRecord(
        long tick,
        UUID entityId,
        EntityType entityType,
        Position position
)implements Recordable {
}
