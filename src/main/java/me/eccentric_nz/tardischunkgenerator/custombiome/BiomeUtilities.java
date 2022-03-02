package me.eccentric_nz.tardischunkgenerator.custombiome;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Properties;

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
        }
        if (planets.getBoolean("planets." + levelName + "_tardis_skaro.enabled")) {
            Bukkit.getConsoleSender().sendMessage(messagePrefix + "Adding custom biomes for planet Skaro...");
            CustomBiome.addCustomBiome(TARDISBiomeData.DESERT);
        }
    }

    public static Location searchBiome(World world, org.bukkit.block.Biome biome, Player player, Location policeBox) {
        ServerLevel worldServer = ((CraftWorld) world).getHandle();
        CommandSourceStack commandListenerWrapper = ((CraftPlayer) player).getHandle().createCommandSourceStack();
        Optional<Biome> optional = commandListenerWrapper.getServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(new ResourceLocation
                (biome.getKey().getKey()));
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
}
