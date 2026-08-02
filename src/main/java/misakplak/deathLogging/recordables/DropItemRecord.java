package misakplak.deathLogging.recordables;

import org.bukkit.Material;

public record DropItemRecord(
        long tick,
        Position position,
        Material material
) implements Recordable{}

