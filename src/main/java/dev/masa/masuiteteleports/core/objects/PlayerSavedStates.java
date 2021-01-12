package dev.masa.masuiteteleports.core.objects;

import org.bukkit.GameMode;

public class PlayerSavedStates {

    private GameMode gamemode = null;
    private Boolean god = null;
    private Boolean fly = null;

    public PlayerSavedStates(GameMode gamemode, boolean god, boolean fly) {
        this.gamemode = gamemode;
        this.god = god;
        this.fly = fly;
    }

    public PlayerSavedStates() {
    }

    public GameMode getGamemode() {
        return gamemode;
    }

    public void setGamemode(GameMode gamemode) {
        this.gamemode = gamemode;
    }

    public Boolean isGod() {
        return god;
    }

    public void setGod(Boolean god) {
        this.god = god;
    }

    public Boolean isFly() {
        return fly;
    }

    public void setFly(Boolean fly) {
        this.fly = fly;
    }
}
