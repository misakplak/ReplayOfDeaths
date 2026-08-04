package misakplak.deathLogging;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import misakplak.deathLogging.commands.ReplayCommand;
import misakplak.deathLogging.database.MongoManager;
import misakplak.deathLogging.database.MySqlReplayStorage;
import misakplak.deathLogging.database.ReplayStorage;
import misakplak.deathLogging.guis.PlayerReplayGui;
import misakplak.deathLogging.guis.ReplayGui;
import misakplak.deathLogging.listeners.EventListeners;
import misakplak.deathLogging.replay.ReplayManager;
import misakplak.deathLogging.database.MongoReplayStorage;
import misakplak.deathLogging.replay.loading.ReplayManaging;
import misakplak.deathLogging.replay.world.ReplayWorldManager;
import misakplak.deathLogging.replay.tasks.ReplayTask;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class DeathLogging extends JavaPlugin {

    private static DeathLogging instance;
    private ReplayStorage replayStorage;

    public static DeathLogging getInstance() {
        return instance;
    }


    private final Map<UUID, OfflinePlayer> replayTargets = new HashMap<>();

    public Map<UUID, OfflinePlayer> getReplayTargets() {
        return replayTargets;
    }


    private final MongoManager mongoManager = new MongoManager(this);
    private ReplayWorldManager replayWorldManager;

    private ReplayManaging replayManaging;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        replayWorldManager = new ReplayWorldManager();

        String type = getConfig().getString("database.type", "mongodb").toLowerCase();

        replayStorage = switch (type) {
            case "mysql" -> new MySqlReplayStorage(buildMySqlDataSource());
            case "mongodb" -> {
                mongoManager.connect();
                yield new MongoReplayStorage(mongoManager.getDatabase());
            }
            default -> {
                getLogger().warning("Unknown database.type '" + type + "' in config.yml — defaulting to mongodb.");
                mongoManager.connect();
                yield new MongoReplayStorage(mongoManager.getDatabase());
            }
        };

        replayManaging = new ReplayManaging(replayStorage, replayWorldManager);

        replayWorldManager.load();
        replayManager = new ReplayManager();

        new ReplayTask(replayManager).runTaskTimer(this, 1L, 1L);
        getServer().getPluginManager().registerEvents(new EventListeners(replayManager), this);
        getServer().getPluginManager().registerEvents(new ReplayGui(), this);
        getServer().getPluginManager().registerEvents(new PlayerReplayGui(), this);
        getCommand("replay").setExecutor(new ReplayCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        instance = null;
        mongoManager.disconnect();
    }

    private ReplayManager replayManager = new ReplayManager();


    public MongoManager getMongoManager() {
        return mongoManager;
    }

    public ReplayManager getReplayManager() {
        return replayManager;
    }

    public ReplayWorldManager getReplayWorldManager() {
        return replayWorldManager;
    }

    public ReplayManaging getReplayManaging() {
        return replayManaging;
    }

    private DataSource buildMySqlDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + getConfig().getString("database.mysql.host") + ":"
                + getConfig().getInt("database.mysql.port") + "/"
                + getConfig().getString("database.mysql.database"));
        config.setUsername(getConfig().getString("database.mysql.username"));
        config.setPassword(getConfig().getString("database.mysql.password"));
        config.setMaximumPoolSize(5);
        return new HikariDataSource(config);
    }

    public ReplayStorage getReplayStorage() {
        return replayStorage;
    }

}
