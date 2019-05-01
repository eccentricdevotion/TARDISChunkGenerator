/*
 * Copyright (C) 2018 eccentric_nz
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

import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public interface TARDISHelperAPI {

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
     * Gets a Villager's willingness to breed/trade.
     *
     * @param v the Villager
     * @return whether the villager is willing
     */
    boolean getVillagerWilling(Villager v);

    /**
     * Set a Villager's willingness to breed/trade.
     *
     * @param v the Villager
     * @param w whether the villager is willing
     */
    void setVillagerWilling(Villager v, boolean w);

    /**
     * Set a Villager's level.
     *
     * @param v the Villager
     * @param l the career level to set
     */
    void setVillagerLevel(Villager v, int l);

    /**
     * Gets a Villager's level.
     *
     * @param v the Villager
     * @return whether the villager is willing
     */
    int getVillagerLevel(Villager v);

    /**
     * Refresh a chunk.
     *
     * @param c the chunk to refresh
     */
    void refreshChunk(Chunk c);

    /**
     * Set the FallFlying NBT tag on an entity.
     *
     * @param e the entity to set the NBT tag for
     */
    void setFallFlyingTag(Entity e);

    /**
     * Send JSON chat to a player.
     *
     * @param player the player to send the chat to
     * @param json   the JSON text to send
     */
    void sendJson(Player player, String json);

    /**
     * Open a sign editing GUI.
     *
     * @param player the player to open the GUI for
     * @param sign   the sign block that is being edited
     */
    void openSignGUI(Player player, Sign sign);

    /**
     * Sets a random seed value for a world.
     *
     * @param world the world to set the seed for
     */
    void setRandomSeed(String world);

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
}
