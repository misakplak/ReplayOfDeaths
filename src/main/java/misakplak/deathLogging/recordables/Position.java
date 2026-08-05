package misakplak.deathLogging.recordables;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public record Position(
            double x,
            double y,
            double z,
            float yaw,
            float pitch,
            String world
    ) {

    public static Location toLocation(Position position, World world) {

        Location location = new Location(world,
                position.x(),
                position.y(),
                position.z());

        location.setYaw(position.yaw());
        location.setPitch(position.pitch());

        return location;
    }

    public static Location toLocation(Position position) {
        World world = Bukkit.getWorld(position.world());

        Location location = new Location(world, position.x(), position.y(), position.z());
        location.setYaw(position.yaw());
        location.setPitch(position.pitch());
        return location;
    }

}
