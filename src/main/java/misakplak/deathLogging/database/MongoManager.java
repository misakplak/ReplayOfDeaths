package misakplak.deathLogging.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bukkit.Bukkit;

public class MongoManager {

    private MongoClient client;
    private MongoDatabase database;

    public void connect() {
        client = MongoClients.create("mongodb://localhost:27017");

        database = client.getDatabase("deathloggingMC");

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