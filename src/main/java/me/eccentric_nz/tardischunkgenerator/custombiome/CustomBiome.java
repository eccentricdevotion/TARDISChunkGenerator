package me.eccentric_nz.tardischunkgenerator.custombiome;

import com.mojang.serialization.Lifecycle;
import me.eccentric_nz.tardischunkgenerator.TARDISHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
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
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;

import java.lang.reflect.Field;

public class CustomBiome {

    public static void addCustomBiome(CustomBiomeData data) {
        DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();
        ResourceKey<Biome> minecraftKey = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("minecraft", data.getMinecraftName()));
        ResourceKey<Biome> customKey = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("tardis", data.getCustomName()));
        WritableRegistry<Biome> registrywritable = (WritableRegistry<Biome>) dedicatedServer.registryAccess().ownedRegistry(Registry.BIOME_REGISTRY).get();
        Biome minecraftbiome = registrywritable.get(minecraftKey);
        Holder<Biome> biomeHolder = Holder.direct(minecraftbiome);
        Biome.BiomeBuilder newBiome = new Biome.BiomeBuilder();
        newBiome.biomeCategory(minecraftbiome.getBiomeCategory(biomeHolder));
        newBiome.precipitation(minecraftbiome.getPrecipitation());
        MobSpawnSettings biomeSettingMobs = minecraftbiome.getMobSettings();
        newBiome.mobSpawnSettings(biomeSettingMobs);
        BiomeGenerationSettings biomeSettingGen = minecraftbiome.getGenerationSettings();
        newBiome.generationSettings(biomeSettingGen);
        newBiome.temperature(data.getTemperature());
        newBiome.downfall(data.getDownfall());
        newBiome.temperatureAdjustment(data.isFrozen() ? Biome.TemperatureModifier.NONE : Biome.TemperatureModifier.FROZEN);
        BiomeSpecialEffects.Builder newFog = new BiomeSpecialEffects.Builder();
        newFog.grassColorModifier(BiomeSpecialEffects.GrassColorModifier.NONE);
        newFog.fogColor(data.getFogColour());
        newFog.waterColor(data.getWaterColour());
        newFog.waterFogColor(data.getWaterFogColour());
        newFog.skyColor(data.getSkyColour());
        newFog.foliageColorOverride(data.getFoliageColour());
        newFog.grassColorOverride(data.getGrassColour());
        newBiome.specialEffects(newFog.build());
        Biome biome = newBiome.build();
        TARDISHelper.biomeMap.put(data.getCustomName(), biome);
        changeRegistryLock(dedicatedServer, false);
        registrywritable.register(customKey, biome, Lifecycle.stable());
        changeRegistryLock(dedicatedServer, true);
    }

    public static void changeRegistryLock(DedicatedServer dedicatedServer, boolean isLocked) {
        MappedRegistry<Biome> materials = ((MappedRegistry<Biome>) dedicatedServer.registryAccess().ownedRegistry(Registry.BIOME_REGISTRY).get());
        try {
            Field isFrozen = materials.getClass().getDeclaredField("bL");
            isFrozen.setAccessible(true);
            isFrozen.set(materials, isLocked);
        } catch (IllegalAccessException | NoSuchFieldException e) {

        }
    }
}

