package me.eccentric_nz.tardischunkgenerator.custombiome;

import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.CraftServer;

import java.util.logging.Level;

public class CustomBiome {

    public static void addCustomBiome(CustomBiomeData data) {

        DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();
        ResourceKey<Biome> minecraftKey = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("minecraft", data.getMinecraftName()));
        ResourceKey<Biome> customKey = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("tardis", data.getCustomName()));
        WritableRegistry<Biome> registrywritable = dedicatedServer.registryAccess().ownedRegistry(Registry.BIOME_REGISTRY).get();
        Biome minecraftbiome = registrywritable.get(minecraftKey);
        Biome.BiomeBuilder newBiome = new Biome.BiomeBuilder();
        try {
            newBiome.biomeCategory(minecraftbiome.getBiomeCategory());
            newBiome.precipitation(minecraftbiome.getPrecipitation());
            MobSpawnSettings biomeSettingMobs = minecraftbiome.getMobSettings();
            newBiome.mobSpawnSettings(biomeSettingMobs);
            BiomeGenerationSettings biomeSettingGen = minecraftbiome.getGenerationSettings();
            newBiome.generationSettings(biomeSettingGen);
//            newBiome.a(data.getDepth()); // depth of biome
//            newBiome.b(data.getScale()); // scale of biome
            newBiome.temperature(data.getTemperature()); // temperature of biome
            newBiome.downfall(data.getDownfall()); // downfall of biome
            newBiome.temperatureAdjustment(data.isFrozen() ? Biome.TemperatureModifier.NONE : Biome.TemperatureModifier.FROZEN); // Biome.TemperatureModifier.a will make your biome normal, Biome.TemperatureModifier.b will make your biome frozen
            BiomeSpecialEffects.Builder newFog = new BiomeSpecialEffects.Builder();
            newFog.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE); // this doesn't affect the actual final grass color, just leave this line as it is or you will get errors
            // necessary values; removing them will break your biome
            newFog.fogColor(data.getFogColour()); // fogcolour
            newFog.waterColor(data.getWaterColour()); // water colour
            newFog.waterFogColor(data.getWaterFogColour()); // water fog colour
            newFog.skyColor(data.getSkyColour()); // sky colour
            newFog.foliageColorOverride(data.getFoliageColour()); // foliage colour (leaves, vines and more)
            newFog.grassColorOverride(data.getGrassColour()); // grass blocks colour
            newBiome.specialEffects(newFog.build());
            registrywritable.register(customKey, newBiome.build(), Lifecycle.stable());
        } catch (IllegalArgumentException | SecurityException e) {
            Bukkit.getLogger().log(Level.WARNING, "Exception adding custom biome to registry: %s", e.getMessage());
        }
    }
}

