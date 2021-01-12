package dev.masa.masuiteteleports.bukkit.commands;

import dev.masa.masuiteteleports.bukkit.MaSuiteTeleports;
import dev.masa.masuiteteleports.core.objects.PlayerSavedStates;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;


public class GamemodeCommand implements CommandExecutor {

    private final HashMap<String, PlayerSavedStates> players;
    private final FileConfiguration config = MaSuiteTeleports.config.load("teleports", "config.yml");

    public GamemodeCommand(HashMap<String, PlayerSavedStates> players) {
        this.players = players;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = null;
        Player target = null;

        if (sender instanceof Player) {
            player = (Player) sender;
            if (args.length >= 2 && Bukkit.getPlayer(args[1]) != null && player.hasPermission("masuite.gamemode.others")) {
                target = Bukkit.getPlayer(args[1]);
            } else if (!player.hasPermission("masuite.gamemode.others")) {
                target = player;
            } else {
                target = player;
            }
        }

        setGamemode(player, target, args[0], !(sender instanceof Player));
        return true;
    }

    private void setGamemode(Player sender, Player player, String gm, boolean ignorePerm) {
        boolean success = false;
        if (gm.equals("0") || "survival".startsWith(gm.toLowerCase())) {
            if (ignorePerm || sender.hasPermission("masuite.gamemode.survival")) {
                player.setGameMode(GameMode.SURVIVAL);
                success = true;
            }
        } else if (gm.equals("1") || "creative".startsWith(gm.toLowerCase())) {
            if (ignorePerm || sender.hasPermission("masuite.gamemode.creative")) {
                player.setGameMode(GameMode.CREATIVE);
                success = true;
            }
        } else if (gm.equals("2") || "adventure".startsWith(gm.toLowerCase())) {
            if (ignorePerm || sender.hasPermission("masuite.gamemode.adventure")) {
                player.setGameMode(GameMode.ADVENTURE);
                success = true;
            }
        } else if (gm.equals("3") || "spectator".startsWith(gm.toLowerCase())) {
            if (ignorePerm || sender.hasPermission("masuite.gamemode.spectator")) {
                player.setGameMode(GameMode.SPECTATOR);
                success = true;
            }
        }
        if (success) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("gamemode.self")));
            if (sender != player) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("gamemode.other")).replaceAll("%player%", player.getName()));
            }
        }
    }
}
