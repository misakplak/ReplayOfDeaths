package misakplak.deathLogging.replay.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class PlotCopier {

    private final int radius;

    public PlotCopier(int radius) {
        this.radius = radius;
    }

    public void copy(Location sourceCenter, Location plotOrigin) {

        World source = sourceCenter.getWorld();
        World plot = plotOrigin.getWorld();

        ReplayOffset offset = ReplayOffset.between(sourceCenter, plotOrigin);

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = source.getMinHeight(); y < source.getMaxHeight(); y++) {
                    Block sourceBlock = source.getBlockAt(
                            sourceCenter.getBlockX() + x, y, sourceCenter.getBlockZ() + z);

                    if (sourceBlock.isEmpty()) continue;

                    int targetY = y + offset.y();
                    if (targetY < plot.getMinHeight() || targetY >= plot.getMaxHeight()) continue;

                    Block targetBlock = plot.getBlockAt(
                            plotOrigin.getBlockX() + x, targetY, plotOrigin.getBlockZ() + z);

                    targetBlock.setBlockData(sourceBlock.getBlockData(), false);
                }
            }
        }
    }
}