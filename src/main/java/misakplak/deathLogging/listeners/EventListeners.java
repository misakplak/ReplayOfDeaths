package misakplak.deathLogging.listeners;

import misakplak.deathLogging.DeathLogging;
import misakplak.deathLogging.database.MongoReplayStorage;
import misakplak.deathLogging.misc.SwingHand;
import misakplak.deathLogging.recordables.*;
import misakplak.deathLogging.replay.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

public class EventListeners implements Listener {

    private final ReplayManager manager;
    private final LocationRecorder locationRecorder = new LocationRecorder();
    private final MongoReplayStorage replayStorage = DeathLogging.getInstance().getReplayStorage();


    public EventListeners(ReplayManager manager) {
        this.manager = manager;
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event){

        manager.create(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        manager.remove(event.getPlayer());
        DeathLogging.getInstance().getReplayManaging().stop(event.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        manager.remove(event.getPlayer());
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {

        ReplayBuffer buffer = manager.get(event.getPlayer());


        if (buffer == null) {
            return;
        }

        Position position = new Position(
                event.getPlayer().getX(),
                event.getPlayer().getY(),
                event.getPlayer().getZ(),
                event.getPlayer().getPitch(),
                event.getPlayer().getYaw()
        );


        Replay replay = new Replay(
                UUID.randomUUID(),
                event.getPlayer().getUniqueId(),
                position,
                event.getPlayer().getKiller() == null
                        ? null
                        : event.getPlayer().getKiller().getUniqueId(),
                System.currentTimeMillis(),
                buffer.getRecords()
        );


        replayStorage.save(replay);

        manager.remove(event.getPlayer());
        manager.create(event.getPlayer());
    }


    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){

        ReplayBuffer buffer = manager.get(event.getPlayer());

        if (buffer == null) {
            return;
        }

        Position position = new Position(
                event.getBlock().getX(),
                event.getBlock().getY(),
                event.getBlock().getZ(),
                0f,
                0f
        );
        long tick = TickTracker.getTick();

        buffer.add(new BlockPlaceRecord(
                tick,
                position,
                event.getBlock().getType()
        ));


    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        ReplayBuffer buffer = manager.get(event.getPlayer());


        Position position = new Position(
                event.getBlock().getX(),
                event.getBlock().getY(),
                event.getBlock().getZ(),
                0f,
                0f
        );

        if (buffer == null) {
            return;
        }




        long tick = TickTracker.getTick();

        buffer.add(new BlockBreakRecord(
                tick,
                position,
                event.getBlock().getType())
        );
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        ReplayBuffer buffer = manager.get(player);

        Position position = new Position(
                event.getEntity().getX(),
                event.getEntity().getY(),
                event.getEntity().getZ(),
                event.getEntity().getYaw(),
                event.getEntity().getPitch()
        );


        if (buffer == null) {
            return;
        }

        long tick = TickTracker.getTick();

        buffer.add(new DamageRecord(
                tick,
                position,
                event.getFinalDamage(),
                event.getCause()
        ));
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event){

        Player player = event.getPlayer();
        ReplayBuffer buffer = manager.get(player);

        if (buffer == null) {
            return;
        }

        Position position = new Position(
                event.getItemDrop().getX(),
                event.getItemDrop().getY(),
                event.getItemDrop().getZ(),
                event.getItemDrop().getYaw(),
                event.getItemDrop().getPitch()
        );

        Material material = event.getItemDrop().getItemStack().getType();

        long tick = TickTracker.getTick();

        buffer.add(new DropItemRecord(
                tick,
                position,
                material
        ));

    }


    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event){

        Player player = event.getPlayer();
        ReplayBuffer buffer = manager.get(player);

        Position position = new Position(
                event.getPlayer().getX(),
                event.getPlayer().getY(),
                event.getPlayer().getZ(),
                event.getPlayer().getYaw(),
                event.getPlayer().getPitch()
        );

        if (buffer == null) {
            return;
        }


        Material material = event.getItem().getItemStack().getType();

        long tick = TickTracker.getTick();

        buffer.add(new PickupItemRecord(
                tick,
                position,
                material
        ));

    }

    @EventHandler
    public void onSwing(PlayerAnimationEvent event) {
        Player player = event.getPlayer();
        ReplayBuffer buffer = manager.get(player);

        if (buffer == null) {
            return;
        }

        buffer.add(new SwingArmRecord(TickTracker.getTick(), SwingHand.MAIN));
    }


}
