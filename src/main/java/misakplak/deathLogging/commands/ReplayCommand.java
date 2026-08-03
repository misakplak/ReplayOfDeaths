package misakplak.deathLogging.commands;

import misakplak.deathLogging.DeathLogging;
import misakplak.deathLogging.guis.ReplayGui;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

                OfflinePlayer target = args.length > 0
                        ? Bukkit.getOfflinePlayer(args[0])
                        : player;

                if (args.length > 0 && !target.hasPlayedBefore()) {
                    player.sendMessage("§cThat player has never played on this servr.");
                    return true;
                }

                DeathLogging.getInstance().getReplayTargets().put(player.getUniqueId(), target);
                player.openInventory(new ReplayGui().getInventory(player));
            }

        }
        return true;
    }
}
