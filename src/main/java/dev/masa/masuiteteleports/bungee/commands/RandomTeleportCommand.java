package dev.masa.masuiteteleports.bungee.commands;

import dev.masa.masuitecore.core.configuration.BungeeConfiguration;
import dev.masa.masuiteteleports.bungee.MaSuiteTeleports;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class RandomTeleportCommand extends Command {

    private final MaSuiteTeleports plugin;
    private final Configuration config;

    public RandomTeleportCommand(MaSuiteTeleports plugin) {
        super("random-teleport", "masuiteteleports.commands.rtp", "rtp");
        this.plugin = plugin;
        this.config = plugin.config.load("teleports", "messages.yml");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) {
            return;
        }
        if(args.length != 2) {
            plugin.formator.sendMessage((ProxiedPlayer) sender, this.config.getString("randomtp.syntax-error"));
            return;
        }
        String targetArgument = args[0];
        String serverArgument = args[1];

        ProxiedPlayer player = plugin.getProxy().getPlayer(targetArgument);
        if(player == null) {
            plugin.formator.sendMessage((ProxiedPlayer) sender, this.config.getString("randomtp.player-not-online"));
            return;
        }

        ServerInfo serverInfo = plugin.getProxy().getServerInfo(serverArgument);
        if(serverInfo == null) {
            plugin.formator.sendMessage((ProxiedPlayer) sender, this.config.getString("randomtp.server-not-exists"));
            return;
        }

        tpRandom(player, serverInfo.getName());
    }


    public void tpRandom(ProxiedPlayer player, String server) {
        plugin.getPlayerTeleportService().teleportPlayerToRandom(player, server);
    }
}
