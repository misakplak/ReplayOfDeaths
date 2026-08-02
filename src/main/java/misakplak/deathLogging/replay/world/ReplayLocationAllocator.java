package misakplak.deathLogging.replay.world;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class ReplayLocationAllocator {

    private static final int SPACING = 200;

    public static Location getReplayLocation(UUID id) {
        World world = Bukkit.getWorld("replay_world");

        long hash = id.getMostSignificantBits() ^ id.getLeastSignificantBits();

        int index = Math.abs((int) hash);

        int x = (index % 50) * SPACING;
        int z = (index / 50) * SPACING;


        return new Location(world, x, 100, z);
    }
}
