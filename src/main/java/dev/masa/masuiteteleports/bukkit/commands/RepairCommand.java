package dev.masa.masuiteteleports.bukkit.commands;

import dev.masa.masuiteteleports.bukkit.MaSuiteTeleports;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class RepairCommand implements CommandExecutor {
    private final FileConfiguration config = MaSuiteTeleports.config.load("teleports", "config.yml");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (player.hasPermission("masuite.repair")) {
                Inventory inv = player.getInventory();

                for (ItemStack item : inv.getContents()) {
                    if (item != null && item.getItemMeta() instanceof Damageable) {
                        Damageable damageable = (Damageable) item.getItemMeta();
                        damageable.setDamage(0);

                        item.setItemMeta((ItemMeta) damageable);
                    }
                }
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', config.getString("repair")));
            }
        }
        return false;
    }
}
