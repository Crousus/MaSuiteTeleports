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

public class FlyCommand implements CommandExecutor {
    private final HashMap<String, PlayerSavedStates> players;

    private final FileConfiguration config = MaSuiteTeleports.config.load("teleports", "config.yml");

    public FlyCommand(HashMap<String, PlayerSavedStates> players) {
        this.players = players;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        Player target = null;

        if (sender instanceof Player) {
            player = (Player) sender;
            if (args.length >= 1 && player.hasPermission("masuite.fly.others")) {
                target = Bukkit.getPlayer(args[0]);
            } else if (!player.hasPermission("masuite.fly.others")) {
                target = player;
            } else {
                target = player;
            }
            if (player.hasPermission("masuite.fly.others") || player.hasPermission("masuite.fly")) {
                target.setAllowFlight(!target.getAllowFlight());
                if (target != player) {
                    if (target.getAllowFlight())
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("fly.other.enabled").replaceAll("%player%", target.getName())));
                    else
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("fly.other.disabled").replaceAll("%player%", target.getName())));
                }
                if (target.getAllowFlight())
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("fly.self.enabled")));
                else
                    target.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("fly.self.disabled")));
            }

        } else {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("nopermission")));
        }

        return true;
    }
}
