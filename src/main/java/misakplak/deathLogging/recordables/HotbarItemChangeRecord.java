package misakplak.deathLogging.recordables;

import org.bukkit.Material;

public record HotbarItemChangeRecord(
        long tick,
        Material material
)implements Recordable {
}
