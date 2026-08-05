package misakplak.deathLogging.replay;

import misakplak.deathLogging.recordables.LocationRecord;
import misakplak.deathLogging.recordables.Position;
import org.bukkit.entity.Player;

public class LocationRecorder {

    public LocationRecord record(Player player) {

        return new LocationRecord(
                TickTracker.getTick(),
                new Position(
                        player.getX(),
                        player.getY(),
                        player.getZ(),
                        player.getYaw(),
                        player.getPitch(),
                        player.getWorld().getName()
                )
        );
    }
}