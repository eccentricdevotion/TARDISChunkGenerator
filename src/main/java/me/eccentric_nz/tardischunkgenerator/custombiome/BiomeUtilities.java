package me.eccentric_nz.tardischunkgenerator.custombiome;

import net.minecraft.commands.CommandListenerWrapper;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.IRegistry;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.biome.BiomeBase;
import net.minecraft.world.level.biome.BiomeFog;
import net.minecraft.world.level.chunk.BiomeStorage;
import net.minecraft.world.phys.Vec3D;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_17_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Level;

public class BiomeUtilities {


    public static String getLevelName() {
        try {
            BufferedReader is = new BufferedReader(new FileReader("server.properties"));
            Properties props = new Properties();
            props.load(is);
            is.close();
            return props.getProperty("level-name");
        } catch (IOException e) {
            return "world"; // minecraft / spigot default
        }
    }

    public static void addBiomes(String basePath, String messagePrefix) {
        // get the TARDIS planets config
        String levelName = getLevelName();
        FileConfiguration planets = YamlConfiguration.loadConfiguration(new File(basePath + "planets.yml"));
        if (planets.getBoolean("planets." + levelName + "_tardis_gallifrey.enabled")) {
            Bukkit.getConsoleSender().sendMessage(messagePrefix + "Adding custom biomes for planet Gallifrey...");
            CustomBiome.addCustomBiome(TardisBiomeData.BADLANDS);
            CustomBiome.addCustomBiome(TardisBiomeData.ERODED);
            CustomBiome.addCustomBiome(TardisBiomeData.PLATEAU);
        }
        if (planets.getBoolean("planets." + levelName + "_tardis_skaro.enabled")) {
            Bukkit.getConsoleSender().sendMessage(messagePrefix + "Adding custom biomes for planet Skaro...");
            CustomBiome.addCustomBiome(TardisBiomeData.DESERT);
            CustomBiome.addCustomBiome(TardisBiomeData.HILLS);
            CustomBiome.addCustomBiome(TardisBiomeData.LAKES);
        }
    }

    public static Location searchBiome(World world, Biome biome, Player player, Location policeBox) {
        WorldServer worldServer = ((CraftWorld) world).getHandle();
        CommandListenerWrapper commandListenerWrapper = ((CraftPlayer) player).getHandle().getCommandListener();
        Optional<BiomeBase> optional = commandListenerWrapper.getServer().getCustomRegistry().d(IRegistry.aO).getOptional(MinecraftKey.a(biome.getKey().getKey())); // aO = ResourceKey<IRegistry<BiomeBase>>
        if (optional.isPresent()) {
            BiomeBase biomeBase = optional.get();
            Vec3D vector = new Vec3D(policeBox.getX(), policeBox.getY(), policeBox.getZ());
            BlockPosition startPosition = new BlockPosition(vector);
            BlockPosition biomePosition = worldServer.a(biomeBase, startPosition, 6400, 8);
            if (biomePosition != null) {
                return new Location(world, biomePosition.getX(), biomePosition.getY(), biomePosition.getZ());
            }
        }
        return null;
    }

    public static String getBiomeKey(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        assert world != null;
        WorldServer worldServer = world.getHandle();
        IRegistry<BiomeBase> registry = worldServer.t().d(IRegistry.aO);
        net.minecraft.world.level.chunk.Chunk chunk = ((CraftChunk) location.getChunk()).getHandle();
        BiomeStorage biomeStorage = chunk.getBiomeIndex();
        MinecraftKey key;
        if (biomeStorage != null) {
            BiomeBase base = biomeStorage.getBiome(8, 64, 8);
            BiomeFog fog = base.l();
            if (fog.d() == TardisBiomeData.LAKES.getSkyColour()) {
                key = new MinecraftKey("tardis", "skaro_lakes");
            } else if (fog.d() == TardisBiomeData.HILLS.getSkyColour()) {
                key = new MinecraftKey("tardis", "skaro_hills");
            } else if (fog.d() == TardisBiomeData.DESERT.getSkyColour()) {
                key = new MinecraftKey("tardis", "skaro_desert");
            } else if (fog.d() == TardisBiomeData.BADLANDS.getSkyColour()) {
                key = new MinecraftKey("tardis", "gallifrey_badlands");
            } else if (fog.d() == TardisBiomeData.ERODED.getSkyColour()) {
                key = new MinecraftKey("tardis", "gallifrey_eroded");
            } else if (fog.d() == TardisBiomeData.PLATEAU.getSkyColour()) {
                key = new MinecraftKey("tardis", "gallifrey_plateau");
            } else {
                key = registry.getKey(base);
            }
            if (key != null) {
                return key.toString();
            } else {
                System.out.println("Biome key was null for " + location);
                switch (world.getEnvironment()) {
                    case NETHER -> {
                        return "minecraft:nether_wastes";
                    }
                    case THE_END -> {
                        return "minecraft:the_end";
                    }
                    default -> {
                        if (world.getName().contains("skaro")) {
                            return "tardis:skaro_lakes";
                        } else if (world.getName().contains("gallifrey")) {
                            return "tardis:gallifrey_badlands";
                        } else {
                            return "minecraft:plains";
                        }
                    }
                }
            }
        }
        return "minecraft:ocean";
    }

    public static String getBiomeKey(Chunk c) {
        net.minecraft.world.level.chunk.Chunk chunk = ((CraftChunk) c).getHandle();
        BiomeStorage biomeStorage = chunk.getBiomeIndex();
        if (biomeStorage != null) {
            BiomeBase base = biomeStorage.getBiome(8, 64, 8);
            BiomeFog fog = base.l();
            if (fog.d() == TardisBiomeData.LAKES.getSkyColour()) {
                return "tardis:skaro_lakes";
            } else if (fog.d() == TardisBiomeData.HILLS.getSkyColour()) {
                return "tardis:skaro_hills";
            } else if (fog.d() == TardisBiomeData.DESERT.getSkyColour()) {
                return "tardis:skaro_desert";
            } else if (fog.d() == TardisBiomeData.BADLANDS.getSkyColour()) {
                return "tardis:gallifrey_badlands";
            } else if (fog.d() == TardisBiomeData.ERODED.getSkyColour()) {
                return "tardis:gallifrey_eroded";
            } else if (fog.d() == TardisBiomeData.PLATEAU.getSkyColour()) {
                return "tardis:gallifrey_plateau";
            } else {
                Bukkit.getLogger().log(Level.INFO, "Biome key was not found for chunk skyColour");
                DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();
                IRegistry<BiomeBase> registry = dedicatedServer.getCustomRegistry().d(IRegistry.aO);
                return Objects.requireNonNull(registry.getKey(base)).toString();
            }
        }
        return "minecraft:plains";
    }
}
