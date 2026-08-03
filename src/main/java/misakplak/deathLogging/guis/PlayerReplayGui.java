package misakplak.deathLogging.guis;

import misakplak.deathLogging.DeathLogging;
import misakplak.deathLogging.database.MongoManager;
import misakplak.deathLogging.database.ReplayStorage;
import misakplak.deathLogging.misc.MakeItem;
import misakplak.deathLogging.replay.Replay;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerReplayGui implements Listener {

    public Inventory getInventory(Player player, Player target, int page) {

        Inventory inventory = Bukkit.createInventory(null, 54, "replays");

        List<Replay> replays = new ArrayList<>();

        NamespacedKey replayKey = new NamespacedKey(DeathLogging.getInstance(), "history-key");
        MongoManager manager = DeathLogging.getInstance().getMongoManager();

        ReplayStorage storage = DeathLogging.getInstance().getReplayStorage(manager.getReplayCollection());

        for (Document doc : manager.getReplayCollection().find()) {
            UUID replayId = UUID.fromString(doc.getString("replayId"));
            replays.add(storage.load(replayId));
        }



            if (records == null) {
                return inventory;
            }

        int PAGE_SIZE = 45;
        int totalPages = (int) Math.ceil(replays.size() / (double) PAGE_SIZE);
        if (totalPages == 0) totalPages = 1;

        page = Math.max(0, Math.min(page, totalPages - 1));

        int start = page * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, replays.size());

        List<Replay> pageReplays = replays.subList(start, end);


            int slot = 0;


            for (Replay replay : pageReplays) {

                ItemStack item = new MakeItem(Material.DARK_OAK_SIGN)
                        .setName("§fReplay")
                        .setLoreLegacy(List.of(
                                "§7Replay id:",
                                "§f§l" + replay.getReplayId()
                        ))
                        .build();

                ItemMeta meta = item.getItemMeta();

                meta.getPersistentDataContainer().set(replayKey,
                        PersistentDataType.STRING,
                        replay.getReplayId().toString()
                        );

                inventory.setItem(slot++, item);
            }


            if (page > 0) {
                ItemStack prev = new MakeItem(Material.ARROW)
                        .setName("§ePrevious Page")
                        .build();

                inventory.setItem(45, prev);
            }

            if (page < totalPages - 1) {
                ItemStack next = new MakeItem(Material.ARROW)
                        .setName("§eNext Page")
                        .build();


                inventory.setItem(53, next);
            }

            ItemStack pageInfo = new MakeItem(Material.PAPER)
                    .setName("§7Page " + (page + 1) + " / " + totalPages)
                    .build();

            inventory.setItem(49, pageInfo);


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

        //what happens on clicks
    }
}
