package misakplak.deathLogging.guis;

import com.mongodb.client.MongoDatabase;
import misakplak.deathLogging.DeathLogging;
import misakplak.deathLogging.database.MongoManager;
import misakplak.deathLogging.database.ReplayStorage;
import misakplak.deathLogging.misc.MakeItem;
import misakplak.deathLogging.replay.Replay;
import misakplak.deathLogging.replay.loading.ReplayManaging;
import misakplak.deathLogging.replay.world.ReplayWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;
import java.util.UUID;

public class ReplayGui implements Listener {


    public Inventory getInventory(Player player) {

        Inventory inventory = Bukkit.createInventory(player, 9, "replays");

        ItemStack deaths = new MakeItem(Material.GREEN_DYE)
                .setName("§c§lDeaths")
                .build();


         ItemStack kills = new MakeItem(Material.RED_DYE)
                .setName("§c§lKills")
                .build();

         Player target = DeathLogging.getInstance().getReplayTargets().get(player.getUniqueId()).getPlayer();


        inventory.setItem(2, deaths);
        inventory.setItem(5, kills);

        ItemStack playerhead = new MakeItem(Material.PLAYER_HEAD)
                .setName(target.getName())
                .build();

        SkullMeta skullmeta = (SkullMeta) playerhead.getItemMeta();
        skullmeta.setOwner(target.getName());
        playerhead.setItemMeta(skullmeta);


        return inventory;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if (!e.getView().getTitle().equals("replays")) {
            return;
        }

        e.setCancelled(true);

        if (item == null) {
            return;
        }



        PlayerReplayGui gui = new  PlayerReplayGui();

        switch (item.getType()) {

            case GREEN_DYE -> {

              p.openInventory(gui.getInventory(DeathLogging.getInstance().getReplayTargets().remove(p.getUniqueId()), 0, false));
            }

            case RED_DYE -> {

                p.openInventory(gui.getInventory(DeathLogging.getInstance().getReplayTargets().remove(p.getUniqueId()), 0, true));
            }


        }
    }
}
