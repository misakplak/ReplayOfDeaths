package misakplak.deathLogging.replay.world;

import misakplak.deathLogging.recordables.EntityMoveRecord;
import misakplak.deathLogging.recordables.EntityRemoveRecord;
import misakplak.deathLogging.recordables.EntitySpawnRecord;
import misakplak.deathLogging.recordables.Position;
import misakplak.deathLogging.replay.ReplayBuffer;
import misakplak.deathLogging.replay.TickTracker;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EntityRecorder {


    public void record(Player player, ReplayBuffer buffer) {

        Collection<Entity> nearby = player.getNearbyEntities(50, 50, 50);

        Set<UUID> seen = new HashSet<>();

        for (Entity entity : nearby) {

            if (entity == player)
                continue;

            if (entity.getType() == EntityType.ARMOR_STAND)
                continue;

            if (!entity.isValid() || entity.isDead())
                continue;

            seen.add(entity.getUniqueId());

            Position position = new Position(
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    entity.getYaw(),
                    entity.getPitch()
            );

            if (!buffer.isTracking(entity)) {

                buffer.track(entity);

                buffer.add(new EntitySpawnRecord(
                        TickTracker.getTick(),
                        entity.getUniqueId(),
                        entity.getType(),
                        position
                ));
            }

            buffer.add(new EntityMoveRecord(
                    TickTracker.getTick(),
                    entity.getUniqueId(),
                    position
            ));
        }

        for (UUID uuid : new HashSet<>(buffer.getTrackedEntities())) {

            if (!seen.contains(uuid)){
                buffer.add(new EntityRemoveRecord(
                        TickTracker.getTick(),
                        uuid
                ));

                buffer.untrack(uuid);
            }


        }
    }
}
