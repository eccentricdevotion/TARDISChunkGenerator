/*
 * Copyright (C) 2021 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.tardischunkgenerator;

import me.eccentric_nz.tardischunkgenerator.helpers.TardisPlanetData;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

public interface TardisHelperApi {

    /**
     * Set the FallFlying NBT tag on an entity.
     *
     * @param entity the entity to set the NBT tag for
     */
    void setFallFlyingTag(Entity entity);

    /**
     * Sets a random seed value for a world.
     *
     * @param world the world to set the seed for
     */
    void setRandomSeed(String world);

    /**
     * Sets LevelName value for a world.
     *
     * @param oldName the current name of the level
     * @param newName the new level name to change to
     */
    void setLevelName(String oldName, String newName);

    /**
     * Sets the GameType value for a world.
     *
     * @param world    the world to set the GameMode for
     * @param gameMode the GameMode to set the world to
     */
    void setWorldGameMode(String world, GameMode gameMode);

    /**
     * Gets some data of a world by reading its level.dat file.
     *
     * @param world the world to get the data for
     * @return the GameMode, Environment and WorldType of a world
     */
    TardisPlanetData getLevelData(String world);

    /**
     * Check a Factions location is valid for TARDIS actions
     *
     * @param player   the player who is performing the TARDIS action
     * @param location the location where the light will be removed
     */
    boolean isInFaction(Player player, Location location);

    /**
     * Update a TARDIS scanner map
     *
     * @param world   the world the map is displaying
     * @param mapView the mapview of the map
     */
    void updateMap(World world, MapView mapView);

    /**
     * Send an action bar message to a player
     *
     * @param player  the player to send the action bar to
     * @param message the message to send
     */
    void sendActionBarMessage(Player player, String message);

    /**
     * Search for a biome
     *
     * @param world  the world to search in
     * @param biome  the biome to search for
     * @param player the player who is searching
     */
    Location searchBiome(World world, Biome biome, Player player, Location policeBox);

    /**
     * Set a chunk to a custom biome
     *
     * @param biome the key of the biome to set e.g. tardis:gallifrey_badlands
     * @param chunk the chunk to set the biome for
     */
    void setCustomBiome(String biome, Chunk chunk);

    /**
     * Gets the Namespaced key from a biome
     *
     * @param location the location to get the biome for
     */
    String getBiomeKey(Location location);

    /**
     * Remove a TileEntity from the world
     */
    void removeTileEntity(BlockState tile);

    /**
     * Reload commands for a player
     */
    void reloadCommandsForPlayer(Player player);

    /**
     * Calls a powerable block's interact method
     */
    void setPowerableBlockInteract(Block block);
}
