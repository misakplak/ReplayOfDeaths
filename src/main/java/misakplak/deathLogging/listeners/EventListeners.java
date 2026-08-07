package misakplak.deathLogging.listeners;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import misakplak.deathLogging.DeathLogging;
import misakplak.deathLogging.database.MongoReplayStorage;
import misakplak.deathLogging.database.ReplayStorage;
import misakplak.deathLogging.misc.SwingHand;
import misakplak.deathLogging.recordables.*;
import misakplak.deathLogging.replay.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class EventListeners implements Listener {

    private final ReplayManager manager;
    private final LocationRecorder locationRecorder = new LocationRecorder();
    private final ReplayStorage replayStorage = DeathLogging.getInstance().getReplayStorage();


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



            ReplayBuffer buffer = manager.getOrCreate(event.getPlayer());


            if (buffer == null) {
                return;
            }

            Position position = new Position(
                    event.getPlayer().getX(),
                    event.getPlayer().getY(),
                    event.getPlayer().getZ(),
                    event.getPlayer().getPitch(),
                    event.getPlayer().getYaw(),
                    event.getPlayer().getWorld().getName()
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

        Bukkit.getScheduler().runTaskAsynchronously(DeathLogging.getInstance(), () ->
                replayStorage.save(replay));

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
                0f,
                event.getPlayer().getWorld().getName()
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
                0f,
                event.getBlock().getWorld().getName()
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
                event.getEntity().getPitch(),
                event.getEntity().getWorld().getName()
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
                event.getItemDrop().getPitch(),
                event.getItemDrop().getWorld().getName()
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
                event.getPlayer().getPitch(),
                event.getPlayer().getWorld().getName()
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

    @EventHandler
    public void onItemChange(PlayerInventorySlotChangeEvent event) {

        Player player = event.getPlayer();
        ReplayBuffer buffer = manager.get(player);
        if (buffer == null) return;

        if (event.getSlot() != player.getInventory().getHeldItemSlot()) {
            return;
        }

        Material material = event.getNewItemStack().getType();

        buffer.add(new HotbarItemChangeRecord(TickTracker.getTick(), material));
    }

    @EventHandler
    public void onHeldSlotChange(PlayerItemHeldEvent event) {

        Player player = event.getPlayer();
        ReplayBuffer buffer = manager.get(player);
        if (buffer == null) return;

        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());
        Material material = newItem == null ? Material.AIR : newItem.getType();

        buffer.add(new HotbarItemChangeRecord(TickTracker.getTick(), material));
    }

}
