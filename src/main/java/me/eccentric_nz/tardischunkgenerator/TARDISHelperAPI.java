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

public interface TARDISHelperAPI {

    /**
     * Gets the horse's speed.
     *
     * @param h the horse
     * @return the horse's speed
     */
    public Double getHorseSpeed(AbstractHorse h);

    /**
     * Sets the horse's speed.
     *
     * @param h the horse
     * @param speed the speed to apply
     */
    public void setHorseSpeed(AbstractHorse h, double speed);

    /**
     * Names a furnace GUI
     *
     * @param block the furnace block
     * @param name the name to give the furnace GUI
     */
    public void nameFurnaceGUI(Block block, String name);

    /**
     * Gets a Villager's willingness to breed/trade.
     *
     * @param v the Villager
     * @return whether the villager is willing
     */
    public boolean getVillagerWilling(Villager v);

    /**
     * Set a Villager's willingness to breed/trade.
     *
     * @param v the Villager
     * @param l the career level to set
     */
    public void setVillagerCareerLevel(Villager v, int l);

    /**
     * Gets a Villager's willingness to breed/trade.
     *
     * @param v the Villager
     * @return whether the villager is willing
     */
    public int getVillagerCareerLevel(Villager v);

    /**
     * Set a Villager's willingness to breed/trade.
     *
     * @param v the Villager
     * @param w whether the villager is willing
     */
    public void setVillagerWilling(Villager v, boolean w);

    /**
     * Set a furnace's total cook time.
     *
     * @param b the furnace block
     * @param c the total cook time to set
     */
    public void setCookTimeTotal(Block b, int c);

    /**
     * Refresh a chunk.
     *
     * @param c the chunk to refresh
     */
    public void refreshChunk(Chunk c);

    /**
     * Set the FallFlying NBT tag on an entity.
     *
     * @param e the entity to set the NBT tag for
     */
    public void setFallFlyingTag(Entity e);

    /**
     *
     */
    public void sendJson(Player player, String json);
}
