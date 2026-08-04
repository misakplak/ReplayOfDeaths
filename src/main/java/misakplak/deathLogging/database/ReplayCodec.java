package misakplak.deathLogging.database;

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

public class ReplayCodec {

    public Document toDocument(Replay replay) {

        List<Document> records = new ArrayList<>();

        long lastTick = replay.records().getLast().tick();
        long firstTick = lastTick - 300;

        for (Recordable record : replay.records()) {

            long tick = Math.max(0, record.tick() - firstTick);

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

        return new Document()
                .append("replayId", replay.replayId().toString())
                .append("playerId", replay.playerId().toString())
                .append("deathlocation", position(replay.deathlocation()))
                .append("killerId", replay.killerId() == null ? null : replay.killerId().toString())
                .append("createdAt", replay.createdAt())
                .append("records", records);
    }

    public Replay fromDocument(UUID replayId, Document document) {

        UUID playerId = UUID.fromString(document.getString("playerId"));

        String killerString = document.getString("killerId");
        UUID killerId = killerString == null ? null : UUID.fromString(killerString);

        Position deathPosition = position(document.get("deathlocation", Document.class));
        long createdAt = document.getLong("createdAt");

        List<Document> recordsDocs = document.getList("records", Document.class);
        List<Recordable> records = new ArrayList<>();

        for (Document record : recordsDocs) {
            switch (record.getString("type")) {

                case "SWING" -> records.add(new SwingArmRecord(
                        record.getLong("tick"),
                        SwingHand.valueOf(record.getString("swing"))
                ));

                case "DAMAGE" -> records.add(new DamageRecord(
                        record.getLong("tick"),
                        position(record.get("position", Document.class)),
                        record.getDouble("damage"),
                        EntityDamageEvent.DamageCause.valueOf(record.getString("cause"))
                ));

                case "BLOCK_PLACE" -> records.add(new BlockPlaceRecord(
                        record.getLong("tick"),
                        position(record.get("position", Document.class)),
                        Material.getMaterial(record.getString("material"))
                ));

                case "BLOCK_BREAK" -> records.add(new BlockBreakRecord(
                        record.getLong("tick"),
                        position(record.get("position", Document.class)),
                        Material.getMaterial(record.getString("material"))
                ));

                case "MOVE" -> records.add(new LocationRecord(
                        record.getLong("tick"),
                        position(record.get("position", Document.class))
                ));

                case "ITEM_DROP" -> records.add(new DropItemRecord(
                        record.getLong("tick"),
                        position(record.get("position", Document.class)),
                        Material.getMaterial(record.getString("material"))
                ));

                case "ITEM_PICKUP" -> records.add(new PickupItemRecord(
                        record.getLong("tick"),
                        position(record.get("position", Document.class)),
                        Material.getMaterial(record.getString("material"))
                ));

                case "ENTITY_SPAWN" -> records.add(new EntitySpawnRecord(
                        record.getLong("tick"),
                        UUID.fromString(record.getString("uuid")),
                        EntityType.valueOf(record.getString("entityType")),
                        position(record.get("position", Document.class))
                ));

                case "ENTITY_MOVE" -> records.add(new EntityMoveRecord(
                        record.getLong("tick"),
                        UUID.fromString(record.getString("uuid")),
                        position(record.get("position", Document.class))
                ));

                case "ENTITY_REMOVE" -> records.add(new EntityRemoveRecord(
                        record.getLong("tick"),
                        UUID.fromString(record.getString("uuid"))
                ));
            }
        }

        return new Replay(replayId, playerId, deathPosition, killerId, createdAt, records);
    }

    private Document position(Position p) {
        return new Document()
                .append("x", p.x())
                .append("y", p.y())
                .append("z", p.z())
                .append("yaw", p.yaw())
                .append("pitch", p.pitch());
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
}