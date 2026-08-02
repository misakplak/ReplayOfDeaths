package misakplak.deathLogging.misc;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MakeItem {

    private final ItemStack item;
    private final ItemMeta meta;

    public MakeItem(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    // Adventure Component name
    public MakeItem setName(Component name) {
        meta.displayName(name);
        return this;
    }

    // Legacy String name
    public MakeItem setName(String name) {
        meta.setDisplayName(name);
        return this;
    }

    // Adventure Component lore
    public MakeItem setLore(List<Component> lore) {
        meta.lore(lore);
        return this;
    }

    // Legacy String lore
    public MakeItem setLoreLegacy(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(meta);
        return item;
    }
}