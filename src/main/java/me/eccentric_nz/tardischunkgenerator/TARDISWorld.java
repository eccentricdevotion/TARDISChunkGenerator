/*
 *  Copyright 2013 eccentric_nz.
 */
package me.eccentric_nz.tardischunkgenerator;

import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author eccentric_nz
 */
public class TARDISWorld extends JavaPlugin {

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new TARDISChunkGenerator();
    }
}
