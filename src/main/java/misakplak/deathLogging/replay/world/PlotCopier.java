package misakplak.deathLogging.replay.world;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class PlotCopier {

    private static final int RADIUS = 50;

    public void copy(Location sourceCenter, Location plotOrigin) {

        World source = sourceCenter.getWorld();
        World plot = plotOrigin.getWorld();

        for (int x = -RADIUS; x <= RADIUS; x++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {

                for (int y = source.getMinHeight(); y < source.getMaxHeight(); y++) {
                    Block sourceBlock = source.getBlockAt(
                            sourceCenter.getBlockX() + x,
                            y,
                            sourceCenter.getBlockZ() + z
                    );

                    if (sourceBlock.isEmpty()) {
                        continue;
                    }

                    Block targetBlock = plot.getBlockAt(
                            plotOrigin.getBlockX() + x,
                            y,
                            plotOrigin.getBlockZ() + z
                    );



                    targetBlock.setBlockData(sourceBlock.getBlockData(), false);
                }

            }
        }


    }
}
