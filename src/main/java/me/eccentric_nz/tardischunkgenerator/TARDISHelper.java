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
import me.eccentric_nz.tardischunkgenerator.custombiome.CubicMaterial;
import me.eccentric_nz.tardischunkgenerator.custombiome.CustomTree;
import me.eccentric_nz.tardischunkgenerator.disguise.*;
import me.eccentric_nz.tardischunkgenerator.helpers.TARDISDatapackUpdater;
import me.eccentric_nz.tardischunkgenerator.helpers.TARDISFactions;
import me.eccentric_nz.tardischunkgenerator.helpers.TARDISMapUpdater;
import me.eccentric_nz.tardischunkgenerator.helpers.TARDISPlanetData;
import me.eccentric_nz.tardischunkgenerator.keyboard.SignInputHandler;
import me.eccentric_nz.tardischunkgenerator.light.ChunkInfo;
import me.eccentric_nz.tardischunkgenerator.light.Light;
import me.eccentric_nz.tardischunkgenerator.light.LightType;
import me.eccentric_nz.tardischunkgenerator.light.RequestSteamMachine;
import me.eccentric_nz.tardischunkgenerator.logging.TARDISLogFilter;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ClientboundChatPacket;
import net.minecraft.network.protocol.game.ClientboundOpenSignEditorPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_18_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
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
import java.util.*;
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
        // update datapacks!
        TARDISDatapackUpdater updater = new TARDISDatapackUpdater(this);
        updater.updateDimension("gallifrey");
        updater.updateDimension("siluria");
        updater.updateDimension("skaro");
        // get the TARDIS config
        String basePath = getServer().getWorldContainer() + File.separator + "plugins" + File.separator + "TARDIS" + File.separator;
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
        if (block != null) {
            ServerLevel ws = ((CraftWorld) block.getWorld()).getHandle();
            BlockPos bp = new BlockPos(block.getX(), block.getY(), block.getZ());
            BlockEntity tile = ws.getBlockEntity(bp);
            if (tile instanceof FurnaceBlockEntity furnace) {
                furnace.setCustomName(new TextComponent(name));
            }
        }
    }

    @Override
    public boolean isArtronFurnace(Block block) {
        if (block != null) {
            ServerLevel ws = ((CraftWorld) block.getWorld()).getHandle();
            BlockPos bp = new BlockPos(block.getX(), block.getY(), block.getZ());
            BlockEntity tile = ws.getBlockEntity(bp);
            if (tile instanceof FurnaceBlockEntity furnace && furnace.getCustomName() != null) {
                return furnace.getCustomName().getString().equals("TARDIS Artron Furnace");
            }
        }
        return false;
    }

    @Override
    public void setFallFlyingTag(org.bukkit.entity.Entity e) {
        Entity nmsEntity = ((CraftEntity) e).getHandle();
        CompoundTag tag = new CompoundTag();
        // writes the entity's NBT data to the `tag` object
        nmsEntity.save(tag);
        tag.putBoolean("FallFlying", true);
        // sets the entity's tag to the altered `tag`
        nmsEntity.load(tag);
    }

    @Override
    public void openSignGUI(Player player, Sign sign) {
        Location l = sign.getLocation();
        SignBlockEntity t = (SignBlockEntity) ((CraftWorld) l.getWorld()).getHandle().getBlockEntity(new BlockPos(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        ServerPlayer entityPlayer = ((CraftPlayer) player.getPlayer()).getHandle();
        entityPlayer.connection.connection.send(t.getUpdatePacket());
        t.setEditable(true);
        t.executeClickCommands(entityPlayer);
        ClientboundOpenSignEditorPacket packet = new ClientboundOpenSignEditorPacket(t.getBlockPos());
        entityPlayer.connection.connection.send(packet);
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
                CompoundTag tagCompound = NbtIo.readCompressed(fileinputstream);
                CompoundTag data = tagCompound.getCompound("Data");
                fileinputstream.close();
                long random = new Random().nextLong();
                // set RandomSeed tag
                data.putLong("RandomSeed", random);
                tagCompound.put("Data", data);
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                NbtIo.writeCompressed(tagCompound, fileoutputstream);
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
                CompoundTag tagCompound = NbtIo.readCompressed(fileinputstream);
                CompoundTag data = tagCompound.getCompound("Data");
                fileinputstream.close();
                // set LevelName tag
                data.putString("LevelName", newName);
                tagCompound.put("Data", data);
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                NbtIo.writeCompressed(tagCompound, fileoutputstream);
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
                CompoundTag tagCompound = NbtIo.readCompressed(fileinputstream);
                CompoundTag data = tagCompound.getCompound("Data");
                fileinputstream.close();
                int mode = switch (gm) {
                    case CREATIVE -> 1;
                    case ADVENTURE -> 2;
                    case SPECTATOR -> 3;
                    default -> 0; // SURVIVAL
                };
                // set GameType tag
                data.putInt("GameType", mode);
                tagCompound.put("Data", data);
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                NbtIo.writeCompressed(tagCompound, fileoutputstream);
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
                CompoundTag tagCompound = NbtIo.readCompressed(fileinputstream);
                fileinputstream.close();
                CompoundTag data = tagCompound.getCompound("Data");
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
                worldType = switch (wt.toLowerCase(Locale.ROOT)) {
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
        Connection connection = ((CraftPlayer) player).getHandle().connection.connection;
        if (connection == null) {
            return;
        }
        BaseComponent component = new TextComponent(message);
        ClientboundChatPacket packet = new ClientboundChatPacket(component, ChatType.GAME_INFO, player.getUniqueId());
        connection.send(packet);
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
    public void removeTileEntity(org.bukkit.block.BlockState tile) {
        net.minecraft.world.level.chunk.ChunkAccess chunk = ((CraftChunk) tile.getChunk()).getHandle();
        BlockPos position = new BlockPos(tile.getLocation().getX(), tile.getLocation().getY(), tile.getLocation().getZ());
        chunk.removeBlockEntity(position);
        tile.getBlock().setType(Material.AIR);
    }

    @Override
    public void setPowerableBlockInteract(Block block) {
        BlockState data = ((CraftBlock) block).getNMS();
        net.minecraft.world.level.Level world = ((CraftWorld) block.getWorld()).getHandle();
        BlockPos position = ((CraftBlock) block).getPosition();
        data.use(world, null, null, BlockHitResult.miss(data.getOffset(world, position), data.getValue(DirectionalBlock.FACING), position));
    }

    @Override
    public void growTree(String tree, Location location) {
        try {
            CustomTree.TARDISTree type = CustomTree.TARDISTree.valueOf(tree.toUpperCase(Locale.ROOT));
            CustomTree.grow(type, location);
        } catch (IllegalArgumentException e) {
            Bukkit.getLogger().log(Level.WARNING, messagePrefix + "Invalid TARDISTree type specified!");
        }
    }

    @Override
    public void growTree(Location location, Material base, Material hat, Material stem, Material decor) {
        CustomTree.grow(location, base, hat, stem, decor);
    }

    @Override
    public List<Material> getTreeMatrials() {
        return CubicMaterial.cubes;
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
