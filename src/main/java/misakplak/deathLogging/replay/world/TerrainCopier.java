package misakplak.deathLogging.replay.world;

import org.bukkit.Location;

public class TerrainCopier {

    private static final int RADIUS = 50;
    private static final int MIN_Y = -64;

    public void copy(Location center, Location destination) {

        int maxY = center.getWorld().getMaxHeight();

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {

            }
        }

    }
}
