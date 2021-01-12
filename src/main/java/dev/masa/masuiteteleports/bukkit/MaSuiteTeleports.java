package dev.masa.masuiteteleports.bukkit;

import dev.masa.masuitecore.acf.PaperCommandManager;
import dev.masa.masuitecore.core.Updator;
import dev.masa.masuitecore.core.adapters.BukkitAdapter;
import dev.masa.masuitecore.core.api.MaSuiteCoreBukkitAPI;
import dev.masa.masuitecore.core.channels.BukkitPluginChannel;
import dev.masa.masuitecore.core.configuration.BukkitConfiguration;
import dev.masa.masuitecore.core.utils.CommandManagerUtil;
import dev.masa.masuiteteleports.bukkit.commands.*;
import dev.masa.masuiteteleports.bukkit.commands.force.TpCommand;
import dev.masa.masuiteteleports.bukkit.listeners.PlayerJoinListener;
import dev.masa.masuiteteleports.bukkit.listeners.TeleportListener;
import dev.masa.masuiteteleports.core.objects.PlayerSavedStates;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class MaSuiteTeleports extends JavaPlugin implements Listener {

    public static BukkitConfiguration config = new BukkitConfiguration();
    @Getter
    private final MaSuiteCoreBukkitAPI api = new MaSuiteCoreBukkitAPI();

    public List<UUID> tpQue = new ArrayList<>();
    public static List<Player> ignoreTeleport = new ArrayList<>();
    PaperCommandManager manager;

    private static MaSuiteTeleports plugin;

    @Override
    public void onEnable() {
        plugin = this;
        // Create configs
        config.create(this, "teleports", "config.yml");

        config.addDefault("teleports/config.yml", "cooldown", 3);
        config.addDefault("teleports/config.yml", "warmup", 3);

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new TeleportListener(this));

        // Load commands
        manager = new PaperCommandManager(this);
        loadCommands();
        CommandManagerUtil.registerMaSuitePlayerCommandCompletion(manager);
        CommandManagerUtil.registerLocationContext(manager);
        CommandManagerUtil.registerCooldownCondition(manager);

        new Updator(getDescription().getVersion(), getDescription().getName(), "60125").checkUpdates();

        api.getCooldownService().addCooldownLength("requests", config.load("teleports", "config.yml").getInt("cooldown"));
        api.getCooldownService().addCooldownLength("spawns", config.load("teleports", "config.yml").getInt("cooldown"));
        api.getCooldownService().addCooldownLength("back", config.load("teleports", "config.yml").getInt("cooldown"));

        api.getWarmupService().addWarmupTime("teleports", config.load("teleports", "config.yml").getInt("warmup"));

    }

    private void loadCommands() {
        manager.registerCommand(new TeleportForceCommands(this));
        manager.registerCommand(new TpCommand(this));
        manager.registerCommand(new TeleportRequestCommands(this));
        manager.registerCommand(new SpawnCommands(this));
        manager.registerCommand(new BackCommand(this));

        HashMap<String, PlayerSavedStates> players = new HashMap<>();

        getCommand("gamemode").setExecutor(new GamemodeCommand(players));
        getCommand("repair").setExecutor(new RepairCommand());
        getCommand("god").setExecutor(new GodCommand(players));
        getCommand("fly").setExecutor(new FlyCommand(players));
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(players), this);
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent e) {
        Player player = e.getPlayer();
        switch (config.load("teleports", "config.yml").getString("respawn-type").toLowerCase()) {
            case ("none"):
                break;
            case ("bed"):
                if (player.getBedSpawnLocation() != null) {
                    player.teleport(player.getBedSpawnLocation());
                    break;
                }
                new BukkitPluginChannel(this, player, "MaSuiteTeleports", "SpawnPlayer", player.getName()).send();
                break;
            case ("home"):
                new BukkitPluginChannel(this, player, "MaSuiteTeleports", "HomeCommand", player.getName(), "home").send();
                break;
            case ("spawn"):
                new BukkitPluginChannel(this, player, "MaSuiteTeleports", "SpawnPlayer", player.getName()).send();
                break;
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Location loc = e.getEntity().getLocation();
        new BukkitPluginChannel(this, e.getEntity(), "MaSuiteTeleports", "GetLocation", e.getEntity().getName(), BukkitAdapter.adapt(loc).serialize()).send();
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (tpQue.contains(e.getEntity().getUniqueId())) {
                e.setCancelled(true);
            }
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void playerTeleport(PlayerTeleportEvent e) {
        //Ignore non-players and no command or plugins reasons
        if ((e.getCause() == PlayerTeleportEvent.TeleportCause.PLUGIN || e.getCause() == PlayerTeleportEvent.TeleportCause.COMMAND) && !e.getPlayer().hasMetadata("NPC")) {

            if (ignoreTeleport.contains(e.getPlayer())) {
                ignoreTeleport.remove(e.getPlayer());
                return;
            }

            Location loc = e.getPlayer().getLocation();
            new BukkitPluginChannel(this, e.getPlayer(), "MaSuiteTeleports", "GetLocation", e.getPlayer().getName(), BukkitAdapter.adapt(loc).serialize()).send();
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        ignoreTeleport.remove(e.getPlayer());
        Location loc = e.getPlayer().getLocation();
        new BukkitPluginChannel(this, e.getPlayer(), "MaSuiteTeleports", "GetLocation", e.getPlayer().getName(), BukkitAdapter.adapt(loc).serialize()).send();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        // Prevent save back location across servers on the destination server
        ignoreTeleport.add(e.getPlayer());
        getServer().getScheduler().runTaskLaterAsynchronously(this, () -> ignoreTeleport.remove(e.getPlayer()), 20 * 5);

        if (getConfig().getBoolean("spawn.first")) {
            if (!e.getPlayer().hasPlayedBefore()) {
                getServer().getScheduler().runTaskLaterAsynchronously(this, () -> new BukkitPluginChannel(this, e.getPlayer(), "MaSuiteTeleports", "FirstSpawnPlayer", e.getPlayer().getName()).send(), 10);
            }
        }
    }

    public static MaSuiteTeleports getPl() {
        return plugin;
    }
}
