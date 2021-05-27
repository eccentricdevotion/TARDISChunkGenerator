/*
 * Copyright (C) 2020 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.tardishelper;

import me.eccentric_nz.tardishelper.helpers.TARDISPlanetData;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.map.MapView;

import java.util.UUID;

public interface TARDISHelperAPI {

	/**
	 * Gets a Villager's willingness to breed/trade.
	 *
	 * @param villager the Villager
	 * @return whether the villager is willing
	 */
	boolean getVillagerWilling(Villager villager);

	/**
	 * Set a Villager's willingness to breed/trade.
	 *
	 * @param villager the Villager
	 * @param willing  whether the villager is willing
	 */
	void setVillagerWilling(Villager villager, boolean willing);

	/**
	 * Refresh a chunk.
	 *
	 * @param chunk the chunk to refresh
	 */
	void refreshChunk(Chunk chunk);

	/**
	 * Set the FallFlying NBT tag on an entity.
	 *
	 * @param entity the entity to set the NBT tag for
	 */
	void setFallFlyingTag(Entity entity);

	/**
	 * Open a sign editing GUI.
	 *
	 * @param player the player to open the GUI for
	 * @param sign   the sign block that is being edited
	 */
	void openSignGUI(Player player, Sign sign);

	/**
	 * Stop listening for sign updates
	 */
	void finishSignEditing(Player player);

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
	 * @param gamemode the GameMode to set the world to
	 */
	void setWorldGameMode(String world, GameMode gamemode);

	/**
	 * Gets some data of a world by reading its level.dat file.
	 *
	 * @param world the world to get the data for
	 * @return the GameMode, Environment and WorldType of a world
	 */
	TARDISPlanetData getLevelData(String world);

	/**
	 * Disguises a player as another entity.
	 *
	 * @param entityType the entity type to disguise as
	 * @param player     the player to disguise
	 */
	void disguise(EntityType entityType, Player player);

	/**
	 * Disguises a player as another entity.
	 *
	 * @param entityType the entity type to disguise as
	 * @param player     the player to disguise
	 * @param options    an array of entity options
	 */
	void disguise(EntityType entityType, Player player, Object[] options);

	/**
	 * Disguises a player as a randomly named Chameleon Arch player.
	 *
	 * @param player the player to disguise
	 * @param name   the random name for the disguise
	 */
	void disguise(Player player, String name);

	/**
	 * Disguises a player as another player.
	 *
	 * @param player the player to disguise
	 * @param uuid   the UUID of the player to disguise as
	 */
	void disguise(Player player, UUID uuid);

	/**
	 * Undisguises a player.
	 *
	 * @param player the player to undisguise
	 */
	void undisguise(Player player);

	/**
	 * Undisguises a Chameleon arched player.
	 *
	 * @param player the player to undisguise
	 */
	void reset(Player player);

	/**
	 * Spawns Emergency Programme One.
	 *
	 * @param player   the player to disguise the EP1 NPC as
	 * @param location the location to spawn the NPC disguise
	 */
	int spawnEmergencyProgrammeOne(Player player, Location location);

	/**
	 * Removes an NPC.
	 *
	 * @param npcId the id of the NPC to undisguise
	 */
	void removeNPC(int npcId, World world);

	/**
	 * Disguises an armour stand as another entity.
	 *
	 * @param stand      the armour stand to disguise
	 * @param entityType the entity type to disguise as
	 * @param options    an array of entity options
	 */
	void disguiseArmourStand(ArmorStand stand, EntityType entityType, Object[] options);

	/**
	 * Undisguises an armour stand.
	 *
	 * @param stand the armour stand to undisguise
	 */
	void undisguiseArmourStand(ArmorStand stand);

	/**
	 * Create a light source
	 *
	 * @param location the location where the light will be created
	 */
	void createLight(Location location);

	/**
	 * Delete a light source
	 *
	 * @param location the location where the light will be removed
	 */
	void deleteLight(Location location);

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
	Location searchBiome(World world, Biome biome, Player player);

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
