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
package me.eccentric_nz.tardishelper;

import me.eccentric_nz.tardishelper.disguise.*;
import me.eccentric_nz.tardishelper.helpers.TARDISFactions;
import me.eccentric_nz.tardishelper.helpers.TARDISMapUpdater;
import me.eccentric_nz.tardishelper.helpers.TARDISPacketMapChunk;
import me.eccentric_nz.tardishelper.helpers.TARDISPlanetData;
import me.eccentric_nz.tardishelper.keyboard.SignInputHandler;
import me.eccentric_nz.tardishelper.light.ChunkInfo;
import me.eccentric_nz.tardishelper.light.Light;
import me.eccentric_nz.tardishelper.light.LightType;
import me.eccentric_nz.tardishelper.light.RequestSteamMachine;
import me.eccentric_nz.tardishelper.logging.TARDISLogFilter;
import net.minecraft.server.v1_16_R3.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftVillager;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

public class TARDISHelperPlugin extends JavaPlugin implements TARDISHelperAPI {

	public static final String messagePrefix = ChatColor.AQUA + "[TARDISHelper] " + ChatColor.RESET;
	public static final RequestSteamMachine machine = new RequestSteamMachine();
	public static TARDISHelperPlugin tardisHelper;

	public static TARDISHelperPlugin getTardisHelper() {
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
		// should we filter the log?
		String basePath = getServer().getWorldContainer() + File.separator + "plugins" + File.separator + "TARDIS" + File.separator;
		// get the TARDIS config
		FileConfiguration configuration = YamlConfiguration.loadConfiguration(new File(basePath + "config.yml"));
		if (configuration.getBoolean("debug")) {
			// yes we should!
			filterLog(basePath + "filtered.log");
			Bukkit.getLogger().log(Level.INFO, messagePrefix + "Starting filtered logging for TARDIS plugins...");
			Bukkit.getLogger().log(Level.INFO, messagePrefix + "Log file located at 'plugins/TARDIS/filtered.log'");
		}
	}

	@Override
	public ChunkGenerator getDefaultWorldGenerator(@NotNull String worldName, String id) {
		return new TARDISChunkGenerator();
	}

	@Override
	public boolean getVillagerWilling(Villager v) {
		try {
			EntityVillager villager = ((CraftVillager) v).getHandle();
			Field willingField = EntityVillager.class.getDeclaredField("bu");
			willingField.setAccessible(true);
			return willingField.getBoolean(villager);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			Bukkit.getLogger().log(Level.SEVERE, messagePrefix + "Failed to get villager willingness: " + ex.getMessage());
			return false;
		}
	}

	@Override
	public void setVillagerWilling(Villager v, boolean willing) {
		try {
			EntityVillager villager = ((CraftVillager) v).getHandle();
			Field willingField = EntityVillager.class.getDeclaredField("bu");
			willingField.setAccessible(true);
			willingField.set(villager, willing);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			Bukkit.getLogger().log(Level.SEVERE, messagePrefix + "Failed to set villager willingness: " + ex.getMessage());
		}
	}

	@Override
	public void refreshChunk(Chunk chunk) {
		TARDISPacketMapChunk.refreshChunk(chunk);
	}

	@Override
	public void setFallFlyingTag(org.bukkit.entity.Entity entity) {
		Entity nmsEntity = ((CraftEntity) entity).getHandle();
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
		TileEntitySign t = (TileEntitySign) ((CraftWorld) Objects.requireNonNull(l.getWorld())).getHandle().getTileEntity(new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
		EntityPlayer entityPlayer = ((CraftPlayer) Objects.requireNonNull(player.getPlayer())).getHandle();
		assert t != null;
		entityPlayer.playerConnection.sendPacket(t.getUpdatePacket());
		t.isEditable = true;
		t.a(entityPlayer);
		PacketPlayOutOpenSignEditor packet = new PacketPlayOutOpenSignEditor(t.getPosition());
		entityPlayer.playerConnection.sendPacket(packet);
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
	public void setWorldGameMode(String world, GameMode gamemode) {
		File file = new File(Bukkit.getWorldContainer().getAbsolutePath() + File.separator + world + File.separator + "level.dat");
		if (file.exists()) {
			try {
				FileInputStream fileinputstream = new FileInputStream(file);
				NBTTagCompound tagCompound = NBTCompressedStreamTools.a(fileinputstream);
				NBTTagCompound data = tagCompound.getCompound("Data");
				fileinputstream.close();
				int mode = switch (gamemode) {
					case CREATIVE -> 1;
					case ADVENTURE -> 2;
					case SPECTATOR -> 3;
					default -> // SURVIVAL
							0;
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
	public void removeNPC(int npcId, World world) {
		TARDISEPSDisguiser.removeNPC(npcId, world);
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
		Collection<Player> players = Objects.requireNonNull(location.getWorld()).getPlayers();
		for (ChunkInfo info : Light.collectChunks(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), LightType.BLOCK, 15)) {
			Light.updateChunk(info, LightType.BLOCK, players);
		}
	}

	@Override
	public void deleteLight(Location location) {
		Light.deleteLight(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), LightType.BLOCK, true);
		Collection<Player> players = Objects.requireNonNull(location.getWorld()).getPlayers();
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
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		if (connection == null) {
			return;
		}
		IChatBaseComponent component = new ChatComponentText(message);
		PacketPlayOutChat packet = new PacketPlayOutChat(component, ChatMessageType.GAME_INFO, player.getUniqueId());
		connection.sendPacket(packet);
	}

	@Override
	public Location searchBiome(World world, Biome biome, Player player) {
		WorldServer worldServer = ((CraftWorld) world).getHandle();
		BiomeBase biomeBase = worldServer.r().b(IRegistry.ay).get(MinecraftKey.a(biome.getKey().getKey()));
		CommandListenerWrapper commandListenerWrapper = ((CraftPlayer) player).getHandle().getCommandListener();
		BlockPosition playerBlockPosition = new BlockPosition(commandListenerWrapper.getPosition());
		BlockPosition blockPosition = worldServer.a(biomeBase, playerBlockPosition, 6400, 8);
		if (blockPosition != null) {
			return new Location(world, blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
		}
		return null;
	}

	@Override
	public String getBiomeKey(Location location) {
		CraftWorld world = (CraftWorld) location.getWorld();
		assert world != null;
		WorldServer worldServer = world.getHandle();
		BiomeBase base = worldServer.getBiome(location.getBlockX() >> 2, location.getBlockY() >> 2, location.getBlockZ() >> 2);
		IRegistry<BiomeBase> registry = world.getHandle().r().b(IRegistry.ay);
		MinecraftKey key = registry.getKey(base);
		if (key != null) {
			return key.toString();
		} else {
			//            Bukkit.getLogger().log(Level.INFO, messagePrefix + "Biome key was null for " + location.toString());
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
		net.minecraft.server.v1_16_R3.Chunk chunk = ((CraftChunk) tile.getChunk()).getHandle();
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
		net.minecraft.server.v1_16_R3.World world = ((CraftWorld) block.getWorld()).getHandle();
		BlockPosition position = ((CraftBlock) block).getPosition();
		if (block.getType().equals(Material.LEVER)) {
			Blocks.LEVER.interact(data, world, position, null, null, null);
		} else {
			// BUTTON
			switch (block.getType()) {
				case ACACIA_BUTTON -> Blocks.ACACIA_BUTTON.interact(data, world, position, null, null, null);
				case BIRCH_BUTTON -> Blocks.BIRCH_BUTTON.interact(data, world, position, null, null, null);
				case CRIMSON_BUTTON -> Blocks.CRIMSON_BUTTON.interact(data, world, position, null, null, null);
				case DARK_OAK_BUTTON -> Blocks.DARK_OAK_BUTTON.interact(data, world, position, null, null, null);
				case JUNGLE_BUTTON -> Blocks.JUNGLE_BUTTON.interact(data, world, position, null, null, null);
				case OAK_BUTTON -> Blocks.OAK_BUTTON.interact(data, world, position, null, null, null);
				case POLISHED_BLACKSTONE_BUTTON -> Blocks.POLISHED_BLACKSTONE_BUTTON.interact(data, world, position, null, null, null);
				case SPRUCE_BUTTON -> Blocks.SPRUCE_BUTTON.interact(data, world, position, null, null, null);
				case STONE_BUTTON -> Blocks.STONE_BUTTON.interact(data, world, position, null, null, null);
				case WARPED_BUTTON -> Blocks.WARPED_BUTTON.interact(data, world, position, null, null, null);
			}
		}
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