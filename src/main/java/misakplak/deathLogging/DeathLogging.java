package misakplak.deathLogging;

import com.github.retrooper.packetevents.PacketEvents;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import misakplak.deathLogging.commands.ReplayCommand;
import misakplak.deathLogging.database.MongoManager;
import misakplak.deathLogging.guis.ReplayGui;
import misakplak.deathLogging.listeners.EventListeners;
import misakplak.deathLogging.replay.ReplayManager;
import misakplak.deathLogging.database.ReplayStorage;
import misakplak.deathLogging.replay.loading.ReplayManaging;
import misakplak.deathLogging.replay.world.ReplayWorldManager;
import misakplak.deathLogging.replay.TickTracker;
import misakplak.deathLogging.replay.tasks.ReplayTask;
import org.bukkit.plugin.java.JavaPlugin;

public final class DeathLogging extends JavaPlugin {

    private static DeathLogging instance;
    private ReplayStorage replayStorage;

    public static DeathLogging getInstance() {
        return instance;
    }


    private final MongoManager mongoManager = new MongoManager();
    private ReplayWorldManager replayWorldManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        mongoManager.connect();
        replayWorldManager = new ReplayWorldManager();

        replayWorldManager.load();


        replayStorage = new ReplayStorage(mongoManager.getDatabase());


        TickTracker.start();
        new ReplayTask(new ReplayManager()).runTaskTimer(this, 1L, 1L);
        getServer().getPluginManager().registerEvents(new EventListeners(), this);
        getServer().getPluginManager().registerEvents(new ReplayGui(), this);
        getCommand("replay").setExecutor(new ReplayCommand());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
        mongoManager.disconnect();
    }

    public ReplayStorage getReplayStorage() {
        return replayStorage;
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public ReplayWorldManager getReplayWorldManager() {
        return replayWorldManager;
    }
}
