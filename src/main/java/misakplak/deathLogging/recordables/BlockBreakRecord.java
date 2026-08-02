package misakplak.deathLogging.recordables;

import org.bukkit.Material;

    public record BlockBreakRecord(
            long tick,
            Position position,
            Material material
    ) implements Recordable {}
