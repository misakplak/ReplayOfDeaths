package misakplak.deathLogging.replay.loading;

import misakplak.deathLogging.DeathLogging;
import misakplak.deathLogging.misc.SwingHand;
import misakplak.deathLogging.recordables.*;
import misakplak.deathLogging.replay.LocationRecorder;
import misakplak.deathLogging.replay.Replay;
import misakplak.deathLogging.replay.ReplayNPC;
import misakplak.deathLogging.replay.world.ReplayOffset;
import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReplayViewer {

    private final Player viewer;
    private final Replay replay;
    private final Location plotOrigin;

    private BukkitTask task;

    private int nextRecord = 0;
    private long replayTick = 0;

    /*

    private final double offsetX;
    private final double offsetZ;
    private final double offsetY;

     */

    private ReplayOffset offset;
    private final Runnable onEnd;

    private boolean active = true;


    private ReplayNPC npc;

    private final Map<UUID, Entity> entities = new HashMap<>();

    public ReplayViewer(Player viewer,
                        Replay replay,
                        Location plotOrigin,
                        Runnable onEnd) {

        this.viewer = viewer;
        this.replay = replay;
        this.plotOrigin = plotOrigin;
        this.onEnd = onEnd;

        this.offset = ReplayOffset.between(replay.getDeathlocation(), plotOrigin);
    }


    public void spawn() {

        LocationRecord first = replay.getRecords().stream()
                .filter(r -> r instanceof LocationRecord)
                .map(r -> (LocationRecord) r)
                .findFirst()
                .orElse(null);

        if (first == null)
            return;

        Location spawn = offset.apply(Position.toLocation(first.position(), plotOrigin.getWorld()));

        npc = new ReplayNPC(spawn, "Replay");
        npc.spawn(viewer);

        task = Bukkit.getScheduler().runTaskTimer(
                DeathLogging.getInstance(),
                this::tick,
                1,
                1
        );
    }
    public void despawn() {
        if (!active) return;
        active = false;

        if (task != null) task.cancel();

        npc.destroy(viewer);
        entities.values().forEach(Entity::remove);
        entities.clear();

        if (onEnd != null) onEnd.run();
    }

    private void tick() {
        while (nextRecord < replay.getRecords().size()) {

            Recordable record = replay.getRecords().get(nextRecord);
            if (record.tick() > replayTick) break;
            play(record);
            nextRecord++;

        }

        replayTick++;

        if (nextRecord >= replay.getRecords().size()) {
            despawn();
        }
    }

    private void play(Recordable record) {



        switch (record) {

            case LocationRecord move ->
                    npc.teleport(viewer, toReplayLocation(move.position()));

            case DamageRecord damage ->
                    npc.PlayHurtAnimation(viewer);

            case SwingArmRecord swing ->
                    npc.PlayHandSwingAnimation(viewer, swing.hand());

            case BlockBreakRecord block ->
                    breakBlock(block);

            case BlockPlaceRecord block ->
                    placeBlock(block);

            case EntitySpawnRecord spawn ->
                    spawnEntity(spawn);

            case EntityMoveRecord move ->
                    moveEntity(move);

            case EntityRemoveRecord remove ->
                    removeEntity(remove);


            /* TODO: /*

            case CritAnimationRecord crit ->
                    npc.playCritAnimation(viewer);
                    }


                 and more data



             */

            default -> {
            }
        }
    }

    private Location toReplayLocation(Position position) {



        return offset.apply(Position.toLocation(position, plotOrigin.getWorld()));
    }

    private void spawnEntity(EntitySpawnRecord record) {
        Location location = toReplayLocation(record.position());

        Entity entity = plotOrigin.getWorld().spawnEntity(location, record.entityType());
        entity.setInvulnerable(true);
        entity.setSilent(true);
        if (entity instanceof Mob mob) {
            mob.setAI(false);
        }

        entities.put(record.entityId(), entity);
    }

    private void moveEntity(EntityMoveRecord record) {

        Entity entity = entities.get(record.entityId());

        if (entity == null) {
            return;
        }

        entity.teleport(toReplayLocation(record.position()));
    }

    public void removeEntity(EntityRemoveRecord record) {

        Entity entity = entities.remove(record.entityId());

        if (entity != null) {
            entity.remove();
        }
    }


    private void breakBlock(BlockBreakRecord record) {
        toReplayLocation(record.position()).getBlock().setType(Material.AIR);
    }

    public void placeBlock(BlockPlaceRecord record) {
        toReplayLocation(record.position()).getBlock().setType(record.material());
    }

}
