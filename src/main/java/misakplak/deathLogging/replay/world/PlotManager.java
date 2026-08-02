package misakplak.deathLogging.replay.world;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlotManager {
    private static final int PLOT_SIZE = 100;
    private static final int SPACING = 40;

    private final Map<UUID, Location> plots = new HashMap<>();

    public Location getPlot(World world, UUID replayId) {
        return plots.computeIfAbsent(replayId, id -> {

            int index = plots.size();

            int gridX = index % 100;
            int gridZ = index / 100;

            int x = gridX * (PLOT_SIZE + SPACING);
            int z = gridZ * (PLOT_SIZE + SPACING);

            return new Location(world, x, 64, z);
        });
    }
}
