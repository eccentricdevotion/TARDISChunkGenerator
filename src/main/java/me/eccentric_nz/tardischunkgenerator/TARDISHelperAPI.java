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
package me.eccentric_nz.tardischunkgenerator;

import me.eccentric_nz.tardischunkgenerator.helpers.TARDISPlanetData;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.map.MapView;

import java.util.List;
import java.util.UUID;

public interface TARDISHelperAPI {

    /**
     * Gets the TARDISChunkGenerator Biome Provider
     *
     * @return the TARDISVoidBiomeProvider
     */
    BiomeProvider getBiomeProvider();

    /**
     * Names a furnace GUI
     *
     * @param block the furnace block
     * @param name  the name to give the furnace GUI
     */
    void nameFurnaceGUI(Block block, String name);

    /**
     * Gets the name from a furnace GUI
     *
     * @param block the furnace block
     * @return true if the block is a furnace named 'TARDIS Artron Furnace'
     */
    boolean isArtronFurnace(Block block);

    /**
     * Set the FallFlying NBT tag on an entity.
     *
     * @param e the entity to set the NBT tag for
     */
    void setFallFlyingTag(Entity e);

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
     * @param world the world to set the GameMode for
     * @param gm    the GameMode to set the world to
     */
    public void setWorldGameMode(String world, GameMode gm);

    /**
     * Gets some data of a world by reading its level.dat file.
     *
     * @param world the world to get the data for
     * @return the GameMode, Environment and WorldType of a world
     */
    public TARDISPlanetData getLevelData(String world);

    /**
     * Disguises a player as another entity.
     *
     * @param entityType the entity type to disguise as
     * @param player     the player to disguise
     */
    public void disguise(EntityType entityType, Player player);

    /**
     * Disguises a player as another entity.
     *
     * @param entityType the entity type to disguise as
     * @param player     the player to disguise
     * @param options    an array of entity options
     */
    public void disguise(EntityType entityType, Player player, Object[] options);

    /**
     * Disguises a player as a randomly named Chameleon Arch player.
     *
     * @param player the player to disguise
     * @param name   the random name for the disguise
     */
    public void disguise(Player player, String name);

    /**
     * Disguises a player as another player.
     *
     * @param player the player to disguise
     * @param uuid   the UUID of the player to disguise as
     */
    public void disguise(Player player, UUID uuid);

    /**
     * Undisguises a player.
     *
     * @param player the player to undisguise
     */
    public void undisguise(Player player);

    /**
     * Undisguises a Chameleon arched player.
     *
     * @param player the player to undisguise
     */
    public void reset(Player player);

    /**
     * Spawns Emergency Programme One.
     *
     * @param player   the player to disguise the EP1 NPC as
     * @param location the location to spawn the NPC disguise
     */
    public int spawnEmergencyProgrammeOne(Player player, Location location);

    /**
     * Removes an NPC.
     *
     * @param id the id of the NPC to undisguise
     */
    public void removeNPC(int id, World world);

    /**
     * Disguises an armour stand as another entity.
     *
     * @param stand      the armour stand to disguise
     * @param entityType the entity type to disguise as
     * @param options    an array of entity options
     */
    public void disguiseArmourStand(ArmorStand stand, EntityType entityType, Object[] options);

    /**
     * Undisguises an armour stand.
     *
     * @param stand the armour stand to undisguise
     */
    public void undisguiseArmourStand(ArmorStand stand);

    /**
     * Create a light source
     *
     * @param location the location where the light will be created
     */
    public void createLight(Location location);

    /**
     * Delete a light source
     *
     * @param location the location where the light will be removed
     */
    public void deleteLight(Location location);

    /**
     * Check a Factions location is valid for TARDIS actions
     *
     * @param player   the player who is performing the TARDIS action
     * @param location the location where the light will be removed
     */
    public boolean isInFaction(Player player, Location location);

    /**
     * Update a TARDIS scanner map
     *
     * @param world   the world the map is displaying
     * @param mapView the mapview of the map
     */
    public void updateMap(World world, MapView mapView);

    /**
     * Send an action bar message to a player
     *
     * @param player  the player to send the action bar to
     * @param message the message to send
     */
    public void sendActionBarMessage(Player player, String message);

    /**
     * Search for a biome
     *
     * @param world     the world to search in
     * @param biome     the biome to search for
     * @param player    the player who is searching
     * @param policeBox the location of the TARDIS exterior
     */
    public Location searchBiome(World world, Biome biome, Player player, Location policeBox);

    /**
     * Set a chunk to a custom biome
     *
     * @param biome the key of the biome to set e.g. tardis:gallifrey_badlands
     * @param chunk the chunk to set the biome for
     */
    public void setCustomBiome(String biome, Chunk chunk);

    /**
     * Gets the Namespaced key from a biome
     *
     * @param location the location to get the biome for
     */
    public String getBiomeKey(Location location);

    /**
     * Gets the Namespaced key from a biome
     *
     * @param chunk the chunk to get the biome for
     */
    public String getBiomeKey(Chunk chunk);

    /**
     * Remove a TileEntity from the world
     */
    public void removeTileEntity(BlockState tile);

    /**
     * Calls a powerable block's interact method
     */
    public void setPowerableBlockInteract(Block block);

    /**
     * Grows a TARDIS tree
     *
     * @param tree     the name of the planet the tree will be grown on - must be "gallifrey" or "skaro"
     * @param location the location to grow the tree
     */
    public void growTree(String tree, Location location);

    /**
     * Grow a custom tree based on a huge fungus tree. All materials specified should be cubic blocks.
     *
     * @param location the location to grow the tree
     * @param base     the material that the tree should grow on e.g. DIRT
     * @param stem     the material for the tree's stem / trunk e.g. OAK_LOG
     * @param hat      the material for the tree's hat / leaves e.g. OAK_LEAVES
     * @param decor    the material for the tree's decoration e.g. PURPLE_GLAZED_TERRACOTTA
     */
    public void growTree(Location location, Material base, Material stem, Material hat, Material decor);

    /**
     * Gets a list of valid tree materials
     */
    public List<Material> getTreeMatrials();
}
