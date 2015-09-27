package me.eccentric_nz.tardischunkgenerator;

import java.util.ArrayList;
import org.bukkit.block.Block;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

public interface TARDISHelperAPI {

    /**
     * Gets the horse's speed.
     *
     * @param h the horse
     * @return the horse's speed
     */
    public Double getHorseSpeed(Horse h);

    /**
     * Sets the horse's speed.
     *
     * @param h the horse
     * @param speed the speed to apply
     */
    public void setHorseSpeed(Horse h, double speed);

    /**
     * Gets a villager's trades.
     *
     * @param v the villager
     * @param p the player who is farming this villager
     * @return a list of trades
     */
    public ArrayList getTrades(Villager v, Player p);

    /**
     * Sets a villager's trades.
     *
     * @param v the villager
     * @param list the list of trades
     */
    public void setTrades(Villager v, ArrayList list);

    /**
     * Sets the invulnerable NBTTag of an item to true. This make the item
     * indestructible.
     *
     * @param item the item to protect
     */
    public void protect(Item item);

    /**
     * Names a furnace GUI
     *
     * @param block the furnace block
     * @param name the name to give the furnace GUI
     */
    public void nameFurnaceGUI(Block block, String name);

    /**
     * Gets a Villager's career.
     *
     * @param v the Villager
     * @return the career number or null
     */
    public int getVillagerCareer(Villager v);

    /**
     * Set a Villager's career.
     *
     * @param v the Villager
     * @param c the career to set
     */
    public void setVillagerCareer(Villager v, int c);

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
}
