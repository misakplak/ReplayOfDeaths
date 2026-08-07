    package misakplak.deathLogging.guis;

    import com.mongodb.client.model.Filters;
    import com.mongodb.client.model.Sorts;
    import misakplak.deathLogging.DeathLogging;
    import misakplak.deathLogging.database.ReplayStorage;
    import misakplak.deathLogging.database.ReplaySummary;
    import misakplak.deathLogging.misc.MakeItem;
    import org.bson.Document;
    import org.bukkit.Bukkit;
    import org.bukkit.Material;
    import org.bukkit.NamespacedKey;
    import org.bukkit.OfflinePlayer;
    import org.bukkit.entity.Player;
    import org.bukkit.event.EventHandler;
    import org.bukkit.event.Listener;
    import org.bukkit.event.inventory.InventoryClickEvent;
    import org.bukkit.inventory.Inventory;
    import org.bukkit.inventory.InventoryHolder;
    import org.bukkit.inventory.ItemStack;
    import org.bukkit.inventory.meta.ItemMeta;
    import org.bukkit.persistence.PersistentDataType;

    import java.util.ArrayList;
    import java.util.Date;
    import java.util.List;
    import java.util.UUID;

    public class PlayerReplayGui implements Listener {

        private static final int PAGE_SIZE = 45;
        private boolean kills;

        private final NamespacedKey replayKey =
                new NamespacedKey(DeathLogging.getInstance(), "history-key");

        private static class Holder implements InventoryHolder {
            final UUID target;
            final int page;
            final boolean kills;

            Holder(UUID target, int page, boolean kills) {
                this.target = target;
                this.page = page;
                this.kills = kills;
            }

            @Override
            public Inventory getInventory() {
                return null;
            }
        }

        public Inventory getInventory(OfflinePlayer target, int page, boolean kills) {
            this.kills = kills;

            ReplayStorage storage = DeathLogging.getInstance().getReplayStorage();


            int total = storage.count(target.getUniqueId(), kills);

            int totalPages = Math.max(1, (int) Math.ceil(total / (double) PAGE_SIZE));
            page = Math.max(0, Math.min(page, totalPages - 1));

            List<ReplaySummary> summaries = storage.list(target.getUniqueId(), kills, page, PAGE_SIZE);

            Holder holder = new Holder(target.getUniqueId(), page, kills);
            Inventory inventory = Bukkit.createInventory(
                    holder, 54, "§8" + target.getName() + "s Replays");

            int slot = 0;
            for (ReplaySummary summary : summaries) {

                ItemStack item = new MakeItem(Material.DARK_OAK_SIGN)
                        .setName("§fReplay")
                        .setLoreLegacy(List.of(
                                "§7Replay id:",
                                "§f§l" + summary.replayId(),
                                "",
                                "§7" + new Date(summary.createdAt())
                        ))
                        .build();

                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(replayKey, PersistentDataType.STRING, summary.replayId().toString());
                item.setItemMeta(meta);

                inventory.setItem(slot++, item);
            }

            if (page > 0) {
                inventory.setItem(45, new MakeItem(Material.ARROW).setName("§ePrevious Page").build());
            }

            if (page < totalPages - 1) {
                inventory.setItem(53, new MakeItem(Material.ARROW).setName("§eNext Page").build());
            }

            inventory.setItem(49, new MakeItem(Material.PAPER)
                    .setName("§7Page " + (page + 1) + " / " + totalPages)
                    .build());

            return inventory;
        }

        @EventHandler
        public void onClick(InventoryClickEvent e) {

            if (!(e.getInventory().getHolder() instanceof Holder holder)) {
                return;
            }

            e.setCancelled(true);

            Player p = (Player) e.getWhoClicked();
            ItemStack item = e.getCurrentItem();

            if (item == null || item.getType() == Material.AIR) {
                return;
            }

            if (item.getType() == Material.ARROW) {
                OfflinePlayer target = Bukkit.getOfflinePlayer(holder.target);
                String name = item.getItemMeta().getDisplayName();

                if (name.equals("§ePrevious Page")) {
                    p.openInventory(getInventory(target, holder.page - 1, holder.kills));
                } else if (name.equals("§eNext Page")) {
                    p.openInventory(getInventory(target, holder.page + 1, holder.kills));
                }
                return;
            }

            ItemMeta meta = item.getItemMeta();
            if (meta == null) return;

            String replayIdString = meta.getPersistentDataContainer().get(replayKey, PersistentDataType.STRING);
            if (replayIdString == null) return;

            DeathLogging.getInstance().getReplayManaging().play(p, UUID.fromString(replayIdString));
            p.closeInventory();
            p.sendMessage("§aPlaying replay");
        }
    }