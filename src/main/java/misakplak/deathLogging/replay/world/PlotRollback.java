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

        World plot = plotOrigin.getWorld();
        Location deathLocation = replay.getDeathlocation();

        ReplayOffset offset = ReplayOffset.between(deathLocation, plotOrigin);

        List<Recordable> records = replay.getRecords();

        for (int i = records.size() - 1; i >= 0; i--) {
            Recordable record = records.get(i);

            if (record instanceof BlockBreakRecord broken) {
                setBlock(plot, broken.position(), offset, broken.material());
            }

            if (record instanceof BlockPlaceRecord placed) {
                setBlock(plot, placed.position(), offset, Material.AIR);
            }
        }
    }

    private void setBlock(World plot, Position position, ReplayOffset offset, Material material) {
        Location target = offset.apply(Position.toLocation(position, plot));
        target.getBlock().setType(material, false);
    }


}
