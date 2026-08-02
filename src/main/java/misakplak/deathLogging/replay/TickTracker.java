package misakplak.deathLogging.replay;

import misakplak.deathLogging.DeathLogging;
import org.bukkit.Bukkit;

public class TickTracker {

    private static long tick;


    public static void start(){
        Bukkit.getScheduler().runTaskTimer(DeathLogging.getInstance(), () -> tick++, 1L, 1L);
    }


    public static long getTick(){
        return  tick;
    }

    public static void nextTick() {
        tick++;
    }
}
