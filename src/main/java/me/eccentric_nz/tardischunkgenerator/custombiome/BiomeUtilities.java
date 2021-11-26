package me.eccentric_nz.tardischunkgenerator.custombiome;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_18_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
            CustomBiome.addCustomBiome(TARDISBiomeData.BADLANDS);
            CustomBiome.addCustomBiome(TARDISBiomeData.ERODED);
            CustomBiome.addCustomBiome(TARDISBiomeData.PLATEAU);
        }
        if (planets.getBoolean("planets." + levelName + "_tardis_skaro.enabled")) {
            Bukkit.getConsoleSender().sendMessage(messagePrefix + "Adding custom biomes for planet Skaro...");
            CustomBiome.addCustomBiome(TARDISBiomeData.DESERT);
            CustomBiome.addCustomBiome(TARDISBiomeData.HILLS);
            CustomBiome.addCustomBiome(TARDISBiomeData.LAKES);
        }
    }

    public static Location searchBiome(World world, org.bukkit.block.Biome biome, Player player, Location policeBox) {
        ServerLevel worldServer = ((CraftWorld) world).getHandle();
        CommandSourceStack commandListenerWrapper = ((CraftPlayer) player).getHandle().createCommandSourceStack();
        Optional<Biome> optional = commandListenerWrapper.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(new ResourceLocation
        (biome.getKey().getKey())); // aO = ResourceKey<Registry<Biome>>
        if (optional.isPresent()) {
            Biome biomeBase = optional.get();
            Vec3 vector = new Vec3(policeBox.getX(), policeBox.getY(), policeBox.getZ());
            BlockPos startPosition = new BlockPos(vector);
            BlockPos biomePosition = worldServer.findNearestBiome(biomeBase, startPosition, 6400, 8);
            if (biomePosition != null) {
                return new Location(world, biomePosition.getX(), biomePosition.getY(), biomePosition.getZ());
            }
        }
        return null;
    }

    public static String getBiomeKey(Location location) {
        CraftWorld world = (CraftWorld) location.getWorld();
        ServerLevel worldServer = world.getHandle();
        Registry<Biome> registry = worldServer.registryAccess().registry(Registry.BIOME_REGISTRY).get();
        LevelChunk chunk = ((CraftChunk) location.getChunk()).getHandle();
        ResourceLocation key;
        Biome base = chunk.getNoiseBiome(8, 64, 8);
        BiomeSpecialEffects fog = base.getSpecialEffects();
        if (fog.getSkyColor() == TARDISBiomeData.LAKES.getSkyColour()) {
            key = new ResourceLocation("tardis", "skaro_lakes");
        } else if (fog.getSkyColor() == TARDISBiomeData.HILLS.getSkyColour()) {
            key = new ResourceLocation("tardis", "skaro_hills");
        } else if (fog.getSkyColor() == TARDISBiomeData.DESERT.getSkyColour()) {
            key = new ResourceLocation("tardis", "skaro_desert");
        } else if (fog.getSkyColor() == TARDISBiomeData.BADLANDS.getSkyColour()) {
            key = new ResourceLocation("tardis", "gallifrey_badlands");
        } else if (fog.getSkyColor() == TARDISBiomeData.ERODED.getSkyColour()) {
            key = new ResourceLocation("tardis", "gallifrey_eroded");
        } else if (fog.getSkyColor() == TARDISBiomeData.PLATEAU.getSkyColour()) {
            key = new ResourceLocation("tardis", "gallifrey_plateau");
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

    public static String getBiomeKey(Chunk c) {
        LevelChunk chunk = ((CraftChunk) c).getHandle();
            Biome base = chunk.getNoiseBiome(8, 64, 8);
            BiomeSpecialEffects fog = base.getSpecialEffects();
            if (fog.getSkyColor() == TARDISBiomeData.LAKES.getSkyColour()) {
                return "tardis:skaro_lakes";
            } else if (fog.getSkyColor() == TARDISBiomeData.HILLS.getSkyColour()) {
                return "tardis:skaro_hills";
            } else if (fog.getSkyColor() == TARDISBiomeData.DESERT.getSkyColour()) {
                return "tardis:skaro_desert";
            } else if (fog.getSkyColor() == TARDISBiomeData.BADLANDS.getSkyColour()) {
                return "tardis:gallifrey_badlands";
            } else if (fog.getSkyColor() == TARDISBiomeData.ERODED.getSkyColour()) {
                return "tardis:gallifrey_eroded";
            } else if (fog.getSkyColor() == TARDISBiomeData.PLATEAU.getSkyColour()) {
                return "tardis:gallifrey_plateau";
            } else {
                Bukkit.getLogger().log(Level.INFO, "Biome key was not found for chunk skyColour");
                DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();
                Registry<Biome> registry = dedicatedServer.registryAccess().registry(Registry.BIOME_REGISTRY).get();
                return registry.getKey(base).toString();
            }
    }
}
