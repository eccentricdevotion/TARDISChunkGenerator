package me.eccentric_nz.tardischunkgenerator;

import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldType;

public class TARDISPlanetData {

    private final GameMode gameMode;
    private final World.Environment environment;
    private final WorldType worldType;

    public TARDISPlanetData(GameMode gameMode, World.Environment environment, WorldType worldType) {
        this.gameMode = gameMode;
        this.environment = environment;
        this.worldType = worldType;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public World.Environment getEnvironment() {
        return environment;
    }

    public WorldType getWorldType() {
        return worldType;
    }
}
