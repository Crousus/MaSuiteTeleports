package dev.masa.masuiteteleports.bukkit.listeners;

import dev.masa.masuiteteleports.bukkit.MaSuiteTeleports;
import dev.masa.masuiteteleports.core.objects.PlayerSavedStates;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;

public class PlayerJoinListener implements Listener {

    private final HashMap<String, PlayerSavedStates> players;

    public PlayerJoinListener(HashMap<String, PlayerSavedStates> players) {
        this.players = players;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (players.containsKey(e.getPlayer().getUniqueId().toString())) {
                    PlayerSavedStates state = players.get(e.getPlayer().getUniqueId().toString());
                    if (state.getGamemode() != null) {
                        e.getPlayer().setGameMode(state.getGamemode());
                    }
                    if (state.isGod() != null) {
                        e.getPlayer().setInvulnerable(state.isGod());
                    }
                    if (state.isFly() != null) {
                        e.getPlayer().setAllowFlight(state.isFly());
                        e.getPlayer().setFlying(state.isFly());
                    }
                } else {
                    PlayerSavedStates state = new PlayerSavedStates();
                    if (e.getPlayer().hasPermission("masuite.gamemode.auto")) {
                        e.getPlayer().setGameMode(GameMode.CREATIVE);
                        state.setGamemode(GameMode.CREATIVE);
                    }
                    if (e.getPlayer().hasPermission("masuite.god.auto")) {
                        e.getPlayer().setInvulnerable(true);
                        state.setGod(true);
                    }
                    if (e.getPlayer().hasPermission("masuite.fly.auto")) {
                        e.getPlayer().setAllowFlight(true);
                        e.getPlayer().setFlying(true);
                        state.setFly(true);
                    }
                    players.put(e.getPlayer().getUniqueId().toString(), state);
                }
            }
        }.runTaskLater(MaSuiteTeleports.getPl(), 15L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!players.containsKey(e.getPlayer().getUniqueId().toString()))
            players.put(e.getPlayer().getUniqueId().toString(), new PlayerSavedStates());

        PlayerSavedStates state = players.get(e.getPlayer().getUniqueId().toString());

        if (e.getPlayer().hasPermission("masuite.gamemode.persist")) {
            state.setGamemode(e.getPlayer().getGameMode());
        } else {
            state.setGamemode(null);
        }
        if (e.getPlayer().hasPermission("masuite.god.persist")) {
            state.setGod(e.getPlayer().isInvulnerable());
        } else {
            state.setGod(null);
        }
        if (e.getPlayer().hasPermission("masuite.fly.persist")) {
            state.setFly(e.getPlayer().getAllowFlight());
        } else {
            state.setFly(null);
        }

    }

}
