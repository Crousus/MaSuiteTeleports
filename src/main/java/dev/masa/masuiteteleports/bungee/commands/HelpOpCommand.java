package dev.masa.masuiteteleports.bungee.commands;

import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class HelpOpCommand extends Command {

    private final MaSuiteTeleports plugin;
    private final Configuration config;

    public HelpOpCommand(MaSuiteTeleports plugin) {
        super("helpop");
        this.plugin = plugin;
        this.config = plugin.config.load("teleports", "messages.yml");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ProxiedPlayer) {
            ProxiedPlayer player = (ProxiedPlayer) sender;
            if (player.hasPermission("masuite.helpop")) {
                String msg = "";
                for (String s : args) {
                    msg = msg + " " + s;
                }
                plugin.formator.sendMessage(player, config.getString("helpop.confirmation"));
                for (ProxiedPlayer target : ProxyServer.getInstance().getPlayers()) {
                    if (target.hasPermission("masuite.helop.receive")) {
                        if (args.length > 0)
                            plugin.formator.sendMessage(target, config.getString("helpop.received").replaceAll("%player%", player.getName()).replaceAll("%message%", msg));
                        else
                            plugin.formator.sendMessage(target, config.getString("helpop.standard").replaceAll("%player%", player.getName()));
                    }
                }
            }
        }
    }
}
