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

import me.eccentric_nz.tardischunkgenerator.custombiome.BiomeHelper;
import me.eccentric_nz.tardischunkgenerator.custombiome.BiomeUtilities;
import me.eccentric_nz.tardischunkgenerator.helpers.TardisFactions;
import me.eccentric_nz.tardischunkgenerator.helpers.TardisMapUpdater;
import me.eccentric_nz.tardischunkgenerator.helpers.TardisPlanetData;
import me.eccentric_nz.tardischunkgenerator.logging.TardisLogFilter;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.nbt.NBTCompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.util.FormattedString;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntityFurnace;
import net.minecraft.world.level.block.state.IBlockData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Level;

public class TardisHelperPlugin extends JavaPlugin implements TardisHelperApi {

    public static final String MESSAGE_PREFIX = ChatColor.AQUA + "[TARDISChunkGenerator] " + ChatColor.RESET;
    public static TardisHelperPlugin tardisHelper;

    public static TardisHelperPlugin getTardisHelper() {
        return tardisHelper;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        tardisHelper = this;
        String basePath = getServer().getWorldContainer() + File.separator + "plugins" + File.separator + "TARDIS" + File.separator;
        // Add custom biomes
        BiomeUtilities.addBiomes(basePath, MESSAGE_PREFIX);
        // get the TARDIS config
        FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(basePath + "config.yml"));
        // should we filter the log?
        if (configuration.getBoolean("debug")) {
            // yes we should!
            filterLog(basePath + "filtered.log");
            Bukkit.getLogger().log(Level.INFO, MESSAGE_PREFIX + "Starting filtered logging for TARDIS plugins...");
            Bukkit.getLogger().log(Level.INFO, MESSAGE_PREFIX + "Log file located at 'plugins/TARDIS/filtered.log'");
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
        return new TardisChunkGenerator();
    }

    public void nameFurnaceGui(Block block) {
        WorldServer worldServer = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        TileEntity tileEntity = worldServer.getTileEntity(blockPosition, true);
        if (!(tileEntity instanceof TileEntityFurnace furnaceBlockEntity)) {
            return;
        }
        furnaceBlockEntity.setCustomName(new IChatBaseComponent() {
            @Override
            public ChatModifier getChatModifier() {
                return null;
            }

            @Override
            public String getText() {
                return null;
            }

            @Override
            public List<IChatBaseComponent> getSiblings() {
                return null;
            }

            @Override
            public IChatMutableComponent g() {
                return null;
            }

            @Override
            public IChatMutableComponent mutableCopy() {
                return null;
            }

            @Override
            public FormattedString f() {
                return null;
            }
        });
    }

    public boolean isArtronFurnace(Block block) {
        WorldServer worldServer = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition blockPosition = new BlockPosition(block.getX(), block.getY(), block.getZ());
        TileEntity tileEntity = worldServer.getTileEntity(blockPosition, true);
        if (!(tileEntity instanceof TileEntityFurnace tileEntityFurnace)) {
            return false;
        }
        boolean is = false;
        if (tileEntityFurnace.getCustomName() != null) {
            is = tileEntityFurnace.getCustomName().getString().equals("TARDIS Artron Furnace");
        }
        return is;
    }

    @Override
    public void setFallFlyingTag(org.bukkit.entity.Entity entity) {
        Entity nmsEntity = ((CraftEntity) entity).getHandle();
        NBTTagCompound nbtTagCompound = new NBTTagCompound();
        // writes the entity's NBT data to the `tag` object
        nmsEntity.save(nbtTagCompound);
        nbtTagCompound.setBoolean("FallFlying", true);
        // sets the entity's tag to the altered `tag`
        nmsEntity.load(nbtTagCompound);
    }

    @Override
    public void setRandomSeed(String world) {
        File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "level.dat");
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                NBTTagCompound tagCompound = NBTCompressedStreamTools.a(fileInputStream);
                NBTTagCompound data = tagCompound.getCompound("Data");
                fileInputStream.close();
                long random = new Random().nextLong();
                // set RandomSeed tag
                data.setLong("RandomSeed", random);
                tagCompound.set("Data", data);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                NBTCompressedStreamTools.a(tagCompound, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, MESSAGE_PREFIX + ex.getMessage());
            }
        }
    }

    @Override
    public void setLevelName(String oldName, String newName) {
        File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + oldName + File.separator + "level.dat");
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                NBTTagCompound tagCompound = NBTCompressedStreamTools.a(fileInputStream);
                NBTTagCompound data = tagCompound.getCompound("Data");
                fileInputStream.close();
                // set LevelName tag
                data.setString("LevelName", newName);
                tagCompound.set("Data", data);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                NBTCompressedStreamTools.a(tagCompound, fileOutputStream);
                fileOutputStream.close();
                Bukkit.getLogger().log(Level.INFO, MESSAGE_PREFIX + "Renamed level to " + newName);
                // rename the directory
                File directory = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + oldName);
                File folder = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + newName);
                directory.renameTo(folder);
                Bukkit.getLogger().log(Level.INFO, MESSAGE_PREFIX + "Renamed directory to " + newName);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, MESSAGE_PREFIX + ex.getMessage());
            }
        }
    }

    @Override
    public void setWorldGameMode(String world, GameMode gameMode) {
        File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "level.dat");
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                NBTTagCompound tagCompound = NBTCompressedStreamTools.a(fileInputStream);
                NBTTagCompound data = tagCompound.getCompound("Data");
                fileInputStream.close();
                int mode = switch (gameMode) {
                    case CREATIVE -> 1;
                    case ADVENTURE -> 2;
                    case SPECTATOR -> 3;
                    default -> // SURVIVAL
                            0;
                };
                // set GameType tag
                data.setInt("GameType", mode);
                tagCompound.set("Data", data);
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                NBTCompressedStreamTools.a(tagCompound, fileOutputStream);
                fileOutputStream.close();
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, MESSAGE_PREFIX + ex.getMessage());
            }
        }
    }

    @Override
    public TardisPlanetData getLevelData(String world) {
        File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "level.dat");
        if (file.exists()) {
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                NBTTagCompound tagCompound = NBTCompressedStreamTools.a(fileInputStream);
                fileInputStream.close();
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
                String generatorName = data.getString("generatorName");
                worldType = switch (generatorName.toLowerCase(Locale.ENGLISH)) {
                    case "flat" -> WorldType.FLAT;
                    case "largeBiomes" -> WorldType.LARGE_BIOMES;
                    case "amplified" -> WorldType.AMPLIFIED;
                    default -> // default or unknown
                            WorldType.NORMAL;
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
                return new TardisPlanetData(gameMode, environment, worldType);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, MESSAGE_PREFIX + ex.getMessage());
                return new TardisPlanetData(GameMode.SURVIVAL, World.Environment.NORMAL, WorldType.NORMAL);
            }
        }
        Bukkit.getLogger().log(Level.INFO, MESSAGE_PREFIX + "Defaulted to GameMode.SURVIVAL, World.Environment.NORMAL, WorldType.NORMAL");
        return new TardisPlanetData(GameMode.SURVIVAL, World.Environment.NORMAL, WorldType.NORMAL);
    }

    @Override
    public boolean isInFaction(Player player, Location location) {
        return new TardisFactions().isInFaction(player, location);
    }

    @Override
    public void updateMap(World world, MapView mapView) {
        new TardisMapUpdater(world, mapView.getCenterX(), mapView.getCenterZ()).update(mapView);
    }

    @Override
    public void sendActionBarMessage(Player player, String message) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().b; // b = playerConnection;
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
        CraftWorld world = (CraftWorld) location.getWorld();
        assert world != null;
        WorldServer worldServer = world.getHandle();
        BiomeBase base = worldServer.getBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2);
        IRegistry<BiomeBase> registry = worldServer.t().d(IRegistry.aO);
        MinecraftKey key = registry.getKey(base);
        if (key != null) {
            return key.toString();
        } else {
            switch (world.getEnvironment()) {
                case NETHER:
                    return "minecraft:nether_wastes";
                case THE_END:
                    return "minecraft:the_end";
                default:
                    if (world.getName().equalsIgnoreCase("skaro")) {
                        return "tardis:skaro_lakes";
                    } else if (world.getName().equalsIgnoreCase("gallifrey")) {
                        return "tardis:gallifrey_badlands";
                    } else {
                        return "minecraft:plains";
                    }
            }
        }
    }

    @Override
    public void removeTileEntity(BlockState tile) {
        net.minecraft.world.level.chunk.Chunk chunk = ((CraftChunk) tile.getChunk()).getHandle();
        BlockPosition position = new BlockPosition(tile.getLocation().getX(), tile.getLocation().getY(), tile.getLocation().getZ());
        chunk.removeTileEntity(position);
        tile.getBlock().setType(Material.AIR);
    }

    @Override
    public void reloadCommandsForPlayer(Player player) {
        ((CraftServer) Bukkit.getServer()).getHandle().getServer().getCommandDispatcher().a(((CraftPlayer) player).getHandle());
    }

    @Override
    public void setPowerableBlockInteract(Block block) {
        IBlockData data = ((CraftBlock) block).getNMS();
        net.minecraft.world.level.World world = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition position = ((CraftBlock) block).getPosition();
        if (block.getType().equals(Material.LEVER)) {
            Blocks.cw.interact(data, world, position, null, null, null);
        } else {
            // BUTTON
            switch (block.getType()) {
                case ACACIA_BUTTON -> Blocks.fn.interact(data, world, position, null, null, null);
                case BIRCH_BUTTON -> Blocks.fl.interact(data, world, position, null, null, null);
                case CRIMSON_BUTTON -> Blocks.ne.interact(data, world, position, null, null, null);
                case DARK_OAK_BUTTON -> Blocks.fo.interact(data, world, position, null, null, null);
                case JUNGLE_BUTTON -> Blocks.fm.interact(data, world, position, null, null, null);
                case OAK_BUTTON -> Blocks.fj.interact(data, world, position, null, null, null);
                case POLISHED_BLACKSTONE_BUTTON -> Blocks.nS.interact(data, world, position, null, null, null);
                case SPRUCE_BUTTON -> Blocks.fk.interact(data, world, position, null, null, null);
                case STONE_BUTTON -> Blocks.cJ.interact(data, world, position, null, null, null);
                case WARPED_BUTTON -> Blocks.nf.interact(data, world, position, null, null, null);
            }
        }
    }

    /**
     * Start filtering logs for TARDIS related information
     *
     * @param path the file path for the filtered log file
     */
    public void filterLog(String path) {
        ((Logger) LogManager.getRootLogger()).addFilter(new TardisLogFilter(path));
    }
}