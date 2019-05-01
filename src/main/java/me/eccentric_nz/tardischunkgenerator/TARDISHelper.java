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

import net.minecraft.server.v1_14_R1.*;
import net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftVillager;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Random;

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
    public boolean isArtronFurnace(Block block) {
        WorldServer ws = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        TileEntity tile = ws.getTileEntity(bp);
        if (tile == null || !(tile instanceof TileEntityFurnace)) {
            return false;
        }
        TileEntityFurnace furnace = (TileEntityFurnace) tile;
        return furnace.getCustomName().getText().equals("TARDIS Artron Furnace");
    }

    @Override
    public boolean getVillagerWilling(Villager v) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field willingField = EntityVillager.class.getDeclaredField("bF");
            willingField.setAccessible(true);
            return willingField.getBoolean(villager);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to get villager willingness: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public void setVillagerWilling(Villager v, boolean w) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field willingField = EntityVillager.class.getDeclaredField("bF");
            willingField.setAccessible(true);
            willingField.set(villager, w);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to set villager willingness: " + ex.getMessage());
        }
    }

    @Override
    public int getVillagerLevel(Villager v) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            return villager.getVillagerData().getLevel();
        } catch (SecurityException | IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to get villager level: " + ex.getMessage());
            return 0;
        }
    }

    @Override
    public void setVillagerLevel(Villager v, int l) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            villager.setVillagerData(villager.getVillagerData().withLevel(l));
        } catch (SecurityException | IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to set villager level: " + ex.getMessage());
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
    public void openSignGUI(Player player, Sign sign) {
        TARDISSignEditor tse = new TARDISSignEditor(new TARDISReflector());
        tse.commit(player, sign);
    }

    @Override
    public void setRandomSeed(String world) {
        File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "level.dat");
        if (file.exists()) {
            try {
                FileInputStream fileinputstream = new FileInputStream(file);
                NBTTagCompound tagCompound = NBTCompressedStreamTools.a(fileinputstream);
                NBTTagCompound data = tagCompound.getCompound("Data");
                fileinputstream.close();
                long random = new Random().nextLong();
                // set RandomSeed tag
                data.setLong("RandomSeed", random);
                tagCompound.set("Data", data);
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                NBTCompressedStreamTools.a(tagCompound, fileoutputstream);
                fileoutputstream.close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    @Override
    public void setWorldGameMode(String world, GameMode gm) {
        File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "level.dat");
        if (file.exists()) {
            try {
                FileInputStream fileinputstream = new FileInputStream(file);
                NBTTagCompound tagCompound = NBTCompressedStreamTools.a(fileinputstream);
                NBTTagCompound data = tagCompound.getCompound("Data");
                fileinputstream.close();
                int mode;
                switch (gm) {
                    case CREATIVE:
                        mode = 1;
                        break;
                    case ADVENTURE:
                        mode = 2;
                        break;
                    case SPECTATOR:
                        mode = 3;
                        break;
                    default: // SURVIVAL
                        mode = 0;
                        break;
                }
                // set GameType tag
                data.setInt("GameType", mode);
                tagCompound.set("Data", data);
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                NBTCompressedStreamTools.a(tagCompound, fileoutputstream);
                fileoutputstream.close();
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

    @Override
    public TARDISPlanetData getLevelData(String world) {
        File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "level.dat");
        if (file.exists()) {
            try {
                FileInputStream fileinputstream = new FileInputStream(file);
                NBTTagCompound tagCompound = NBTCompressedStreamTools.a(fileinputstream);
                fileinputstream.close();
                NBTTagCompound data = tagCompound.getCompound("Data");
                // get GameType tag
                GameMode gameMode;
                int gm = data.getInt("GameType");
                switch (gm) {
                    case 1:
                        gameMode = GameMode.CREATIVE;
                        break;
                    case 2:
                        gameMode = GameMode.ADVENTURE;
                        break;
                    case 3:
                        gameMode = GameMode.SPECTATOR;
                        break;
                    default:
                        gameMode = GameMode.SURVIVAL;
                        break;
                }
                // get generatorName tag
                WorldType worldType;
                String wt = data.getString("generatorName");
                switch (wt.toLowerCase(Locale.ENGLISH)) {
                    case "flat":
                        worldType = WorldType.FLAT;
                        break;
                    case "largeBiomes":
                        worldType = WorldType.LARGE_BIOMES;
                        break;
                    case "amplified":
                        worldType = WorldType.AMPLIFIED;
                        break;
                    case "buffet":
                        worldType = WorldType.BUFFET;
                        break;
                    case "customized":
                        worldType = WorldType.CUSTOMIZED;
                        break;
                    case "verion_1_1":
                        worldType = WorldType.VERSION_1_1;
                        break;
                    default: // default or unknown
                        worldType = WorldType.NORMAL;
                        break;
                }
                World.Environment environment = World.Environment.NORMAL;
                if ((new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "DIM-1")).exists()) {
                    environment = World.Environment.NETHER;
                }
                if ((new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "DIM1")).exists()) {
                    environment = World.Environment.THE_END;
                }
                return new TARDISPlanetData(gameMode, environment, worldType);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
                return new TARDISPlanetData(GameMode.SURVIVAL, World.Environment.NORMAL, WorldType.NORMAL);
            }
        }
        System.out.println("Defaulted to GameMode.SURVIVAL, World.Environment.NORMAL, WorldType.NORMAL");
        return new TARDISPlanetData(GameMode.SURVIVAL, World.Environment.NORMAL, WorldType.NORMAL);
    }
}
