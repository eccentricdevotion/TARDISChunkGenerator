/*
 * Copyright (C) 2020 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (location your option) any later version.
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

import me.eccentric_nz.tardischunkgenerator.custombiome.BiomeHelper;
import me.eccentric_nz.tardischunkgenerator.custombiome.BiomeUtilities;
import me.eccentric_nz.tardischunkgenerator.disguise.*;
import me.eccentric_nz.tardischunkgenerator.helpers.TARDISFactions;
import me.eccentric_nz.tardischunkgenerator.helpers.TARDISMapUpdater;
import me.eccentric_nz.tardischunkgenerator.helpers.TARDISPlanetData;
import me.eccentric_nz.tardischunkgenerator.keyboard.SignInputHandler;
import me.eccentric_nz.tardischunkgenerator.light.ChunkInfo;
import me.eccentric_nz.tardischunkgenerator.light.Light;
import me.eccentric_nz.tardischunkgenerator.light.LightType;
import me.eccentric_nz.tardischunkgenerator.light.RequestSteamMachine;
import me.eccentric_nz.tardischunkgenerator.logging.TARDISLogFilter;
import net.minecraft.core.BlockPosition;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.network.chat.ChatMessageType;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.BlockAttachable;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityFurnace;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.MovingObjectPositionBlock;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class TARDISHelper extends JavaPlugin implements TARDISHelperAPI {

    public static final String messagePrefix = ChatColor.AQUA + "[TARDISChunkGenerator] " + ChatColor.RESET;
    public static final RequestSteamMachine machine = new RequestSteamMachine();
    public static TARDISHelper tardisHelper;

    public static TARDISHelper getTardisHelper() {
        return tardisHelper;
    }

    @Override
    public void onDisable() {
        if (machine.isStarted()) {
            machine.shutdown();
        }
    }

    @Override
    public void onEnable() {
        tardisHelper = this;
        // register disguise listener
        getServer().getPluginManager().registerEvents(new TARDISDisguiseListener(this), this);
        // start RequestStreamMachine
        machine.start(2, 400);
        String basePath = getServer().getWorldContainer() + File.separator + "plugins" + File.separator + "TARDIS" + File.separator;
        // Add custom biomes
        BiomeUtilities.addBiomes(basePath, messagePrefix);
        // get the TARDIS config
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(basePath + "config.yml"));
        // should we filter the log?
        if (configuration.getBoolean("debug")) {
            // yes we should!
            filterLog(basePath + "filtered.log");
            getServer().getConsoleSender().sendMessage(messagePrefix + "Starting filtered logging for TARDIS plugins...");
            getServer().getConsoleSender().sendMessage(messagePrefix + "Log file located at 'plugins/TARDIS/filtered.log'");
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new TARDISChunkGenerator();
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
        boolean is = false;
        if (furnace.getCustomName() != null) {
            is = furnace.getCustomName().getString().equals("TARDIS Artron Furnace");
        }
        return is;
    }

    @Override
    public void setFallFlyingTag(org.bukkit.entity.Entity e) {
        Entity nmsEntity = ((CraftEntity) e).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        // writes the entity's NBT data to the `tag` object
        nmsEntity.save(tag);
        tag.setBoolean("FallFlying", true);
        // sets the entity's tag to the altered `tag`
        nmsEntity.load(tag);
    }

    @Override
    public void openSignGUI(Player player, Sign sign) {
        Location l = sign.getLocation();
        TileEntitySign t = (TileEntitySign) ((CraftWorld) l.getWorld()).getHandle().getTileEntity(new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        EntityPlayer entityPlayer = ((CraftPlayer) player.getPlayer()).getHandle();
        entityPlayer.b.sendPacket(t.getUpdatePacket()); // b = playerConnection
        t.f = true; // f = isEditable
        t.a(entityPlayer);
        PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(t.getPosition());
        entityPlayer.b.sendPacket(packet);
        SignInputHandler.injectNetty(player, this);
    }

    @Override
    public void finishSignEditing(Player player) {
        SignInputHandler.ejectNetty(player);
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
                Bukkit.getLogger().log(Level.SEVERE, messagePrefix + ex.getMessage());
            }
        }
    }

    @Override
    public void setLevelName(String oldName, String newName) {
        File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + oldName + File.separator + "level.dat");
        if (file.exists()) {
            try {
                FileInputStream fileinputstream = new FileInputStream(file);
                NBTTagCompound tagCompound = NBTCompressedStreamTools.a(fileinputstream);
                NBTTagCompound data = tagCompound.getCompound("Data");
                fileinputstream.close();
                // set LevelName tag
                data.setString("LevelName", newName);
                tagCompound.set("Data", data);
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                NBTCompressedStreamTools.a(tagCompound, fileoutputstream);
                fileoutputstream.close();
                Bukkit.getLogger().log(Level.INFO, messagePrefix + "Renamed level to " + newName);
                // rename the directory
                File directory = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + oldName);
                File folder = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + newName);
                directory.renameTo(folder);
                Bukkit.getLogger().log(Level.INFO, messagePrefix + "Renamed directory to " + newName);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, messagePrefix + ex.getMessage());
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
                int mode = switch (gm) {
                    case CREATIVE -> 1;
                    case ADVENTURE -> 2;
                    case SPECTATOR -> 3;
                    default -> 0; // SURVIVAL
                };
                // set GameType tag
                data.setInt("GameType", mode);
                tagCompound.set("Data", data);
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                NBTCompressedStreamTools.a(tagCompound, fileoutputstream);
                fileoutputstream.close();
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, messagePrefix + ex.getMessage());
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
                gameMode = switch (gm) {
                    case 1 -> GameMode.CREATIVE;
                    case 2 -> GameMode.ADVENTURE;
                    case 3 -> GameMode.SPECTATOR;
                    default -> GameMode.SURVIVAL;
                };
                // get generatorName tag
                WorldType worldType;
                String wt = data.getString("generatorName");
                worldType = switch (wt.toLowerCase(Locale.ENGLISH)) {
                    case "flat" -> WorldType.FLAT;
                    case "largeBiomes" -> WorldType.LARGE_BIOMES;
                    case "amplified" -> WorldType.AMPLIFIED;
                    default -> WorldType.NORMAL; // default or unknown
                };
                World.Environment environment = World.Environment.NORMAL;
                File dimDashOne = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "DIM-1");
                File dimOne = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "DIM1");
                if (dimDashOne.exists() && !dimOne.exists()) {
                    environment = World.Environment.NETHER;
                }
                if (dimOne.exists() && !dimDashOne.exists()) {
                    environment = World.Environment.THE_END;
                }
                return new TARDISPlanetData(gameMode, environment, worldType);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, messagePrefix + ex.getMessage());
                return new TARDISPlanetData(GameMode.SURVIVAL, World.Environment.NORMAL, WorldType.NORMAL);
            }
        }
        Bukkit.getLogger().log(Level.INFO, messagePrefix + "Defaulted to GameMode.SURVIVAL, World.Environment.NORMAL, WorldType.NORMAL");
        return new TARDISPlanetData(GameMode.SURVIVAL, World.Environment.NORMAL, WorldType.NORMAL);
    }

    @Override
    public void disguise(EntityType entityType, Player player) {
        new TARDISDisguiser(entityType, player).disguiseToAll();
    }

    @Override
    public void disguise(EntityType entityType, Player player, Object[] options) {
        new TARDISDisguiser(entityType, player, options).disguiseToAll();
    }

    @Override
    public void disguise(Player player, String name) {
        new TARDISChameleonArchDisguiser(player).changeSkin(name);
    }

    @Override
    public void disguise(Player player, UUID uuid) {
        new TARDISPlayerDisguiser(player, uuid).disguiseToAll();
    }

    @Override
    public void undisguise(Player player) {
        new TARDISDisguiser(player).removeDisguise();
    }

    @Override
    public void reset(Player player) {
        new TARDISChameleonArchDisguiser(player).resetSkin();
    }

    @Override
    public int spawnEmergencyProgrammeOne(Player player, Location location) {
        return new TARDISEPSDisguiser(player, location).showToAll();
    }

    @Override
    public void removeNPC(int id, World world) {
        TARDISEPSDisguiser.removeNPC(id, world);
    }

    @Override
    public void disguiseArmourStand(ArmorStand stand, EntityType entityType, Object[] options) {
        new TARDISArmourStandDisguiser(stand, entityType, options).disguiseToAll();
    }

    @Override
    public void undisguiseArmourStand(ArmorStand stand) {
        TARDISArmourStandDisguiser.removeDisguise(stand);
    }

    @Override
    public void createLight(Location location) {
        Light.createLight(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), LightType.BLOCK, 15, true);
        Collection<Player> players = location.getWorld().getPlayers();
        for (ChunkInfo info : Light.collectChunks(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), LightType.BLOCK, 15)) {
            Light.updateChunk(info, LightType.BLOCK, players);
        }
    }

    @Override
    public void deleteLight(Location location) {
        Light.deleteLight(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), LightType.BLOCK, true);
        Collection<Player> players = location.getWorld().getPlayers();
        for (ChunkInfo info : Light.collectChunks(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), LightType.BLOCK, 15)) {
            Light.updateChunk(info, LightType.BLOCK, players);
        }
    }

    @Override
    public boolean isInFaction(Player player, Location location) {
        return new TARDISFactions().isInFaction(player, location);
    }

    @Override
    public void updateMap(World world, MapView mapView) {
        new TARDISMapUpdater(world, mapView.getCenterX(), mapView.getCenterZ()).update(mapView);
    }

    @Override
    public void sendActionBarMessage(Player player, String message) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().b; // b = playerConnection
        if (connection == null) {
            return;
        }
        IChatBaseComponent component = new ChatComponentText(message);
        PacketPlayOutChat packet = new PacketPlayOutChat(component, ChatMessageType.c, player.getUniqueId()); // c = GAME_INFO
        connection.sendPacket(packet);
    }

    @Override
    public Location searchBiome(World world, Biome biome, Player player, Location policeBox) {
        return BiomeUtilities.searchBiome(world, biome, player, policeBox);
    }

    @Override
    public void setCustomBiome(String biome, Chunk chunk) {
        new BiomeHelper().setCustomBiome(biome, chunk);
    }

    @Override
    public String getBiomeKey(Location location) {
        return BiomeUtilities.getBiomeKey(location);
    }

    @Override
    public String getBiomeKey(Chunk chunk) {
        return BiomeUtilities.getBiomeKey(chunk);
    }

    @Override
    public void removeTileEntity(BlockState tile) {
        net.minecraft.world.level.chunk.Chunk chunk = ((CraftChunk) tile.getChunk()).getHandle();
        BlockPosition position = new BlockPosition(tile.getLocation().getX(), tile.getLocation().getY(), tile.getLocation().getZ());
        chunk.removeTileEntity(position);
        tile.getBlock().setType(Material.AIR);
    }

    @Override
    public void setPowerableBlockInteract(Block block) {
        IBlockData data = ((CraftBlock) block).getNMS();
        net.minecraft.world.level.World world = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition position = ((CraftBlock) block).getPosition();
        data.interact(world, null, null, MovingObjectPositionBlock.a(data.n(world, position), data.get(BlockAttachable.aE), position)); // aE = BlockStateDirection
    }

    /**
     * Start filtering logs for TARDIS related information
     *
     * @param path the file path for the filtered log file
     */
    public void filterLog(String path) {
        ((Logger) LogManager.getRootLogger()).addFilter(new TARDISLogFilter(path));
    }
}
