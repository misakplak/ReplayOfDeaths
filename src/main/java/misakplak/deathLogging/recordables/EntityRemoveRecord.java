package misakplak.deathLogging.recordables;

import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRemoveEvent;

import java.util.UUID;

public record  EntityRemoveRecord(
        long tick,
        UUID entityId
)implements Recordable {
}
