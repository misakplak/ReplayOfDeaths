package misakplak.deathLogging.recordables;

import org.bukkit.Material;

public record BlockPlaceRecord(
        long tick,
        Position position,
        Material material
) implements Recordable{

}
