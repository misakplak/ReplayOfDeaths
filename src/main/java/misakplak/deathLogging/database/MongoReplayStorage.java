package misakplak.deathLogging.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import misakplak.deathLogging.misc.SwingHand;
import misakplak.deathLogging.recordables.*;
import misakplak.deathLogging.replay.Replay;
import org.bson.Document;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class MongoReplayStorage implements  ReplayStorage {

    private final MongoCollection<Document> collection;
    private final ReplayCodec codec = new ReplayCodec();

    public MongoReplayStorage(MongoDatabase database) {
        this.collection = database.getCollection("replays");
    }


public void save(Replay replay) {
    collection.insertOne(codec.toDocument(replay));
}

public Replay load(UUID replayId) {
    Document document = collection.find(Filters.eq("replayId", replayId.toString())).first();
    return document == null ? null : codec.fromDocument(replayId, document);
}


    public Replay loadLatest() {
        Document doc = collection.find().sort(new Document("createdAt", -1)).first();
        return doc == null ? null : codec.fromDocument(UUID.fromString(doc.getString("replayId")), doc);
    }

    @Override
    public List<ReplaySummary> list(UUID playerId, boolean asKiller, int page, int pageSize) {
        String field = asKiller ? "killerId" : "playerId";
        return collection.find(Filters.eq(field, playerId.toString()))
                .sort(Sorts.descending("createdAt"))
                .skip(page * pageSize)
                .limit(pageSize)
                .into(new ArrayList<Document>())
                .stream()
                .map(d -> new ReplaySummary(UUID.fromString(d.getString("replayId")), d.getLong("createdAt")))
                .toList();
    }

    @Override
    public int count(UUID playerId, boolean asKiller) {
        String field = asKiller ? "killerId" : "playerId";
        return (int) collection.countDocuments(Filters.eq(field, playerId.toString()));
    }
}
