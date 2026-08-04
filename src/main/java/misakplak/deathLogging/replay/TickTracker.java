package misakplak.deathLogging.replay;

import misakplak.deathLogging.DeathLogging;
import org.bukkit.Bukkit;

public class TickTracker {

    private static long tick;


    public static long getTick(){
        return  tick;
    }

    public static void nextTick() {
        tick++;
    }
}
