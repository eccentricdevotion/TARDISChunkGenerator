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
import org.bukkit.block.Block;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;

public interface TARDISHelperAPI {

    /**
     * Gets the horse's speed.
     *
     * @param h the horse
     * @return the horse's speed
     */
    Double getHorseSpeed(AbstractHorse h);

    /**
     * Sets the horse's speed.
     *
     * @param h the horse
     * @param speed the speed to apply
     */
    void setHorseSpeed(AbstractHorse h, double speed);

    /**
     * Names a furnace GUI
     *
     * @param block the furnace block
     * @param name the name to give the furnace GUI
     */
    void nameFurnaceGUI(Block block, String name);

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
     * @param l the career level to set
     */
    void setVillagerCareerLevel(Villager v, int l);

    /**
     * Gets a Villager's willingness to breed/trade.
     *
     * @param v the Villager
     * @return whether the villager is willing
     */
    int getVillagerCareerLevel(Villager v);

    /**
     * Set a Villager's willingness to breed/trade.
     *
     * @param v the Villager
     * @param w whether the villager is willing
     */
    void setVillagerWilling(Villager v, boolean w);

    /**
     * Set a furnace's total cook time.
     *
     * @param b the furnace block
     * @param c the total cook time to set
     */
    void setCookTimeTotal(Block b, int c);

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
     * @param json the JSON text to send
     */
    void sendJson(Player player, String json);

    /**
     * Open a sign editing GUI.
     *
     * @param player the player to open the GUI for
     * @param sign the sign block that is being edited
     */
    void openSignGUI(Player player, Block sign);

    /**
     * Set the NBT tag:map:int entry on a Filled Map ItemStack.
     */
    ItemStack setMapNBT(ItemStack itemStack, int map);
}
