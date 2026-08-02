package misakplak.deathLogging.commands;

import misakplak.deathLogging.guis.ReplayGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplayCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        switch (cmd.getName()) {
            case "replay" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cYou must be a player to use this command.");
                    return true;
                }

                if (player.hasPermission("replay.view")) {
                    ReplayGui gui  = new ReplayGui();
                    player.openInventory(gui.getInventory(player));
                }

            }
        }


        return true;
    }
}
