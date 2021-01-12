package dev.masa.masuiteteleports.bukkit.commands;

import dev.masa.masuiteteleports.bukkit.MaSuiteTeleports;
import dev.masa.masuiteteleports.core.objects.PlayerSavedStates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class GodCommand implements CommandExecutor {

    private final HashMap<String, PlayerSavedStates> players;
    private final FileConfiguration config = MaSuiteTeleports.config.load("teleports", "config.yml");

    public GodCommand(HashMap<String, PlayerSavedStates> players) {
        this.players = players;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        Player target = null;

        if (sender instanceof Player) {
            player = (Player) sender;
            if (args.length >= 1 && player.hasPermission("masuite.god.others")) {
                target = Bukkit.getPlayer(args[0]);
            } else if (!player.hasPermission("masuite.god.others")) {
                target = player;
            } else {
                target = player;
            }
            if (player.hasPermission("masuite.god.others") || player.hasPermission("masuite.god")) {
                target.setInvulnerable(!target.isInvulnerable());

                if (target != player) {
                    if (target.isInvulnerable())
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("god.other.enabled").replaceAll("%player%", target.getName())));
                    else
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("god.other.disabled").replaceAll("%player%", target.getName())));
                }
                if (target.isInvulnerable())
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("god.self.enabled")));
                else
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("god.self.disabled")));

            }
        }


        return true;
    }

}
