package misakplak.deathLogging.replay.world;

import misakplak.deathLogging.recordables.BlockBreakRecord;
import misakplak.deathLogging.recordables.BlockPlaceRecord;
import misakplak.deathLogging.recordables.Position;
import misakplak.deathLogging.recordables.Recordable;
import misakplak.deathLogging.replay.Replay;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.List;


public class PlotRollback {

    public void rollback(Replay replay, Location plotOrigin) {

        World plot =  plotOrigin.getWorld();
        Location deathLocation = replay.getDeathlocation();

        int offsetX = plotOrigin.getBlockX() - deathLocation.getBlockX();
        int offsetY = plotOrigin.getBlockY() - deathLocation.getBlockY();
        int offsetZ = plotOrigin.getBlockZ() - deathLocation.getBlockZ();

        List<Recordable> records = replay.getRecords();

        for (int i = records.size() - 1; i >= 0; i--) {
            Recordable record = records.get(i);

            if (record instanceof BlockBreakRecord broken) {
                setBlock(plot, broken.position(), offsetX, offsetY, offsetZ, broken.material());
            }

            if (record instanceof BlockPlaceRecord placed) {
                setBlock(plot, placed.position(), offsetX, offsetY, offsetZ, Material.AIR);
            }
        }

    }

    private void setBlock(World plot, Position position, int offsetX, int offsetY, int offsetZ, Material material) {
        Location location = Position.toLocation(position, plot);
        location.add(offsetX, offsetY, offsetZ);
        location.getBlock().setType(material, false);
    }


}
