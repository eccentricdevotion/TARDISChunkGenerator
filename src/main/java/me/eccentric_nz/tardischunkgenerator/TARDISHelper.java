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

import net.minecraft.server.v1_13_R1.*;
import net.minecraft.server.v1_13_R1.IChatBaseComponent.ChatSerializer;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TARDISHelper extends JavaPlugin implements TARDISHelperAPI {

    private TARDISHelper tardisHelper;

    @Override
    public void onEnable() {
        tardisHelper = this;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new TARDISChunkGenerator();
    }

    public TARDISHelper getTardisHelper() {
        return tardisHelper;
    }

    @Override
    public void nameFurnaceGUI(Block block, String name) {
        WorldServer ws = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        TileEntity tile = ws.getTileEntity(bp);
        if (tile == null || !(tile instanceof TileEntityFurnace)) {
            return;
        }
        TileEntityFurnace furnace = (TileEntityFurnace) tile;
        furnace.setCustomName(new ChatMessage(name));
    }

    @Override
    public boolean getVillagerWilling(Villager v) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field willingField = EntityVillager.class.getDeclaredField("bM");
            willingField.setAccessible(true);
            return willingField.getBoolean(villager);
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to get villager willingness: " + ex.getMessage());
            return false;
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to get villager willingness: " + ex.getMessage());
            return false;
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to get villager willingness: " + ex.getMessage());
            return false;
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to get villager willingness: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public void setVillagerWilling(Villager v, boolean w) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field willingField = EntityVillager.class.getDeclaredField("bM");
            willingField.setAccessible(true);
            willingField.set(villager, w);
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to set villager willingness: " + ex.getMessage());
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to set villager willingness: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to set villager willingness: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to set villager willingness: " + ex.getMessage());
        }
    }

    @Override
    public int getVillagerCareerLevel(Villager v) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field careerLevelField = EntityVillager.class.getDeclaredField("bQ");
            careerLevelField.setAccessible(true);
            return careerLevelField.getInt(villager);
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career level: " + ex.getMessage());
            return 0;
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career level: " + ex.getMessage());
            return 0;
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career level: " + ex.getMessage());
            return 0;
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career level: " + ex.getMessage());
            return 0;
        }
    }

    @Override
    public void setVillagerCareerLevel(Villager v, int l) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field careerField = EntityVillager.class.getDeclaredField("bQ");
            careerField.setAccessible(true);
            careerField.set(villager, l);
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career level: " + ex.getMessage());
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career level: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career level: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career level: " + ex.getMessage());
        }
    }

    @Override
    public void setCookTimeTotal(Block f, int c) {
        try {
            TileEntityFurnace furnace = (TileEntityFurnace) ((CraftWorld) f.getWorld()).getTileEntityAt(f.getX(), f.getY(), f.getZ());
            Field cttField = TileEntityFurnace.class.getDeclaredField("cookTimeTotal");
            cttField.setAccessible(true);
            cttField.set(furnace, c);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(TARDISHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TARDISHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(TARDISHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(TARDISHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void refreshChunk(Chunk c) {
        TARDISPacketMapChunk.refreshChunk(c);
    }

    @Override
    public void setFallFlyingTag(org.bukkit.entity.Entity e) {
        Entity nmsEntity = ((CraftEntity) e).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        // writes the entity's NBT data to the `tag` object
        nmsEntity.c(tag);
        tag.setBoolean("FallFlying", true);
        // sets the entity's tag to the altered `tag`
        ((EntityLiving) nmsEntity).a(tag);
    }

    @Override
    public void sendJson(Player player, String json) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(json)));
    }

    @Override
    public void openSignGUI(Player player, Block sign) {
        Location l = sign.getLocation();
        TileEntitySign t = (TileEntitySign) ((CraftWorld) l.getWorld()).getTileEntityAt(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        t.a(((CraftPlayer) player).getHandle());
        t.isEditable = true;
        BlockPosition.MutableBlockPosition mbp = new BlockPosition.MutableBlockPosition();
        PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(mbp.c(l.getX(), l.getY(), l.getZ()));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
