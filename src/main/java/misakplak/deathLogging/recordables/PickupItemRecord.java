package misakplak.deathLogging.recordables;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;


public record PickupItemRecord(
            long tick,
            Position position,
            Material material
    ) implements Recordable{}