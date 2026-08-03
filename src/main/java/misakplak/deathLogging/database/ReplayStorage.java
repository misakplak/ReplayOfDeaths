package misakplak.deathLogging.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import misakplak.deathLogging.misc.SwingHand;
import misakplak.deathLogging.recordables.*;
import misakplak.deathLogging.replay.Replay;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ReplayStorage {

    private final MongoCollection<org.bson.Document> collection;

    public ReplayStorage(MongoDatabase database) {
        this.collection = database.getCollection("replays");
    }

    public void save(Replay replay) {

        List<Document> records = new ArrayList<>();

        long firstTick = replay.records().getFirst().tick();

        for (Recordable record : replay.records()) {

            long tick = record.tick() - firstTick;

            if (record instanceof DamageRecord damage) {
                records.add(new Document()
                        .append("type", "DAMAGE")
                        .append("tick", tick)
                        .append("position", position(damage.position()))
                        .append("damage", damage.damage())
                        .append("cause", damage.cause().name()));
            }

            if (record instanceof BlockPlaceRecord blockPlace) {
                records.add(new Document()
                        .append("type", "BLOCK_PLACE")
                        .append("tick", tick)
                        .append("position", position(blockPlace.position()))
                        .append("material", blockPlace.material().name()));
            }

            if (record instanceof BlockBreakRecord blockBreak) {
                records.add(new Document()
                        .append("type", "BLOCK_BREAK")
                        .append("tick", tick)
                        .append("position", position(blockBreak.position()))
                        .append("material", blockBreak.material().name()));
            }

            if (record instanceof PickupItemRecord pickupItem) {
                records.add(new Document()
                        .append("type", "ITEM_PICKUP")
                        .append("tick", tick)
                        .append("position", position(pickupItem.position()))
                        .append("material", pickupItem.material().name()));
            }

            if (record instanceof DropItemRecord dropItem) {
                records.add(new Document()
                        .append("type", "ITEM_DROP")
                        .append("tick", tick)
                        .append("position", position(dropItem.position()))
                        .append("material", dropItem.material().name()));
            }

            if (record instanceof LocationRecord location) {
                records.add(new Document()
                        .append("type", "MOVE")
                        .append("tick", tick)
                        .append("position", position(location.position())));
            }

            if (record instanceof EntitySpawnRecord spawn) {
                records.add(new Document()
                        .append("type", "ENTITY_SPAWN")
                        .append("tick", tick)
                        .append("uuid", spawn.entityId().toString())
                        .append("entityType", spawn.entityType().name())
                        .append("position", position(spawn.position())));
            }

            if (record instanceof EntityMoveRecord move) {
                records.add(new Document()
                        .append("type", "ENTITY_MOVE")
                        .append("tick", tick)
                        .append("uuid", move.entityId().toString())
                        .append("position", position(move.position())));
            }

            if (record instanceof EntityRemoveRecord remove) {
                records.add(new Document()
                        .append("type", "ENTITY_REMOVE")
                        .append("tick", tick)
                        .append("uuid", remove.entityId().toString()));
            }

            if (record instanceof SwingArmRecord swingArmRecord) {
                records.add(new Document()
                        .append("type", "SWING")
                        .append("tick", tick)
                        .append("swing", swingArmRecord.hand().name()));
            }

        }

        Document replayDoc = new Document()
                .append("replayId", replay.replayId().toString())
                .append("playerId", replay.playerId().toString())
                .append("deathlocation", position(replay.deathlocation()))
                .append("killerId",
                        replay.killerId() == null ? null : replay.killerId().toString())
                .append("createdAt", replay.createdAt())
                .append("records", records);

        collection.insertOne(replayDoc);
        Bukkit.getLogger().info(replayDoc.toJson());

    }

    private Document position(Position p) {
        return new Document()
                .append("x", p.x())
                .append("y", p.y())
                .append("z", p.z())
                .append("yaw", p.yaw())
                .append("pitch", p.pitch());
    }

    public Replay load(UUID replayId){
        Document document = collection.find(Filters.eq("replayId", replayId.toString())).first();

        if (document == null){
            return null;
        }

        UUID playerId = UUID.fromString(document.getString("playerId"));

        String killerString = document.getString("killerId");
        UUID killerId = killerString == null ? null : UUID.fromString(killerString);
        Position deathPosition =
                position(document.get("deathlocation", Document.class));

        long createdAt = document.getLong("createdAt");

        List<Document> recordsDocs = document.getList("records", Document.class);
        List<Recordable> records = new ArrayList<>();

        for (Document record : recordsDocs) {

            String type = record.getString("type");

            switch (type) {


                case "SWING" -> {

                    records.add(new SwingArmRecord(
                            record.getLong("tick"),
                            SwingHand.valueOf(record.getString("swing"))
                    ));
                }

                case "DAMAGE" -> {
                    Position position = position(record.get("position", Document.class));

                    records.add(new DamageRecord(
                            record.getLong("tick"),
                            position,
                            record.getDouble("damage"),
                            EntityDamageEvent.DamageCause.valueOf(record.getString("cause"))
                    ));
                }

                case "BLOCK_PLACE" -> {
                    Position position = position(record.get("position", Document.class));
                    Material material = Material.getMaterial(record.getString("material"));

                    records.add(new BlockPlaceRecord(
                            record.getLong("tick"),
                            position,
                            material
                    ));
                }

                case "BLOCK_BREAK" -> {
                    Position position = position(record.get("position", Document.class));
                    Material material = Material.getMaterial(record.getString("material"));

                    records.add(new BlockBreakRecord(
                            record.getLong("tick"),
                            position,
                            material
                    ));
                }

                case "MOVE" -> {
                    Position position = position(record.get("position", Document.class));

                    records.add(new LocationRecord(
                            record.getLong("tick"),
                            position
                    ));
                }

                case "ITEM_DROP" -> {
                    Position position = position(record.get("position", Document.class));
                    Material material = Material.getMaterial(record.getString("material"));

                    records.add(new DropItemRecord(

                            record.getLong("tick"),
                            position,
                            material

                    ));
                }

                case "ITEM_PICKUP" -> {
                    Position position = position(record.get("position", Document.class));
                    Material material = Material.getMaterial(record.getString("material"));

                    records.add(new PickupItemRecord(

                            record.getLong("tick"),
                            position,
                            material

                    ));
                }

                case "ENTITY_SPAWN" -> {
                    Position position = position(record.get("position", Document.class));

                    records.add(new EntitySpawnRecord(
                            record.getLong("tick"),
                            UUID.fromString(record.getString("uuid")),
                            EntityType.valueOf(record.getString("entityType")),
                            position
                    ));
                }

                case "ENTITY_MOVE" -> {
                    Position position = position(record.get("position", Document.class));

                    records.add(new EntityMoveRecord(
                            record.getLong("tick"),
                            UUID.fromString(record.getString("uuid")),
                            position
                    ));
                }

                case "ENTITY_REMOVE" -> {
                    records.add(new EntityRemoveRecord(
                            record.getLong("tick"),
                            UUID.fromString(record.getString("uuid"))
                    ));
                }
            }
        }



        return new Replay(
                replayId,
                playerId,
                deathPosition,
                killerId,
                createdAt,
                records
        );
    }

    private Position position(Document document) {

        return new Position(
                document.getDouble("x"),
                document.getDouble("y"),
                document.getDouble("z"),
                document.getDouble("yaw").floatValue(),
                document.getDouble("pitch").floatValue()
        );
    }


    public Replay loadLatest() {

        Document document = collection.find()
                .sort(new Document("createdAt", -1))
                .first();

        if (document == null) {
            return null;
        }

        return load(UUID.fromString(document.getString("replayId")));
    }


}
