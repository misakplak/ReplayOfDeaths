package misakplak.deathLogging.replay.world;

import org.bukkit.Location;

public record ReplayOffset(int x, int y, int z) {

    public static ReplayOffset between(Location deathLocation, Location plotOrigin) {
        return new ReplayOffset(
                plotOrigin.getBlockX() - deathLocation.getBlockX(),
                plotOrigin.getBlockY() - deathLocation.getBlockY(),
                plotOrigin.getBlockZ() - deathLocation.getBlockZ()
        );
    }

    public Location apply(Location location) {
        return location.clone().add(x, y, z);
    }
}