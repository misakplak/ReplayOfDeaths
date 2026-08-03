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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class ReplayGui implements Listener {


    public Inventory getInventory(Player player) {

        Inventory inventory = Bukkit.createInventory(player, 9, "§aReplay");

        ItemStack deaths = new MakeItem(Material.GREEN_DYE)
                .setName("§c§lDeaths")
                .build();

        inventory.setItem(3, deaths);


        return inventory;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();
        ItemStack item = e.getCurrentItem();

        if (!e.getView().getTitle().equals("§aReplay")) {
            return;
        }

        e.setCancelled(true);

        if (item == null) {
            return;
        }



        switch (item.getType()) {

            case GREEN_DYE -> {
                ReplayManaging replayManaging = DeathLogging.getInstance().getReplayManaging();
                Replay replay = DeathLogging.getInstance().getReplayStorage().loadLatest();

                if (replay == null) {
                    p.sendMessage("§cNo replays found!");
                    return;
                }

                replayManaging.play(p, replay.replayId());
                p.closeInventory();
                p.sendMessage("§aPlaying replay");
            }


        }
    }
}
