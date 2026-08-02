package misakplak.deathLogging.replay.tasks;

import misakplak.deathLogging.replay.LocationRecorder;
import misakplak.deathLogging.replay.ReplayBuffer;
import misakplak.deathLogging.replay.ReplayManager;
import misakplak.deathLogging.replay.TickTracker;
import misakplak.deathLogging.replay.world.EntityRecorder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ReplayTask extends BukkitRunnable {

    private final ReplayManager manager;
    private final LocationRecorder locationRecorder = new LocationRecorder();

    public ReplayTask(ReplayManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            ReplayBuffer buffer = manager.get(player);

            if (buffer == null)
                continue;

            buffer.add(locationRecorder.record(player));

            for (Entity entity : player.getNearbyEntities(50, 50, 50)) {

                if (entity == player)
                    continue;


                if (entity.getType() == EntityType.ARMOR_STAND)
                    continue;

                EntityRecorder recorder = new EntityRecorder();
                recorder.record(player,buffer);
            }
        }

        TickTracker.nextTick();
    }
}
