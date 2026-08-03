package misakplak.deathLogging.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import misakplak.deathLogging.DeathLogging;
import misakplak.deathLogging.replay.Replay;
import misakplak.deathLogging.replay.ReplayManager;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;


public class MongoManager {

    private MongoClient client;
    private MongoDatabase database;

    private final DeathLogging plugin;

    public MongoManager(DeathLogging plugin) {
        this.plugin = plugin;
    }

    public void connect() {

        String databaseName = plugin.getConfig().getString("database.mongodb.database");
        String accessUri = plugin.getConfig().getString("database.mongodb.uri");


        try {
            client = MongoClients.create(accessUri);
            database = (client.getDatabase(databaseName));

            database.listCollectionNames().first();

            plugin.getLogger().info("Connected to database " + databaseName);

        }catch (Exception e){
            plugin.getLogger().warning("Could not connect to database " + databaseName);
            e.printStackTrace();
        }

        Bukkit.getLogger().info("Connected to database.");
    }

    public void disconnect() {
        if (client != null) {
            client.close();
        }
    }

    public MongoCollection<Document> getReplayCollection() {
        return database.getCollection("replays");
    }


    public MongoDatabase getDatabase() {
        return database;
    }
}