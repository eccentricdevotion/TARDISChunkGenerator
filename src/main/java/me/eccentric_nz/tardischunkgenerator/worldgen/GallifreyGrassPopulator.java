package me.eccentric_nz.tardischunkgenerator.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.BaseDiskFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.CraftRegionAccessor;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class GallifreyGrassPopulator extends BlockPopulator {

    public final Holder<ConfiguredFeature<DiskConfiguration, ?>> DISK_GRASS;
    List<Material> buildable = Arrays.asList(Material.RED_SAND, Material.TERRACOTTA);

    public GallifreyGrassPopulator() {
        DISK_GRASS = FeatureUtils.register("gallifrey_disk_grass", Feature.DISK,
                new DiskConfiguration(
                        Blocks.GRASS_BLOCK.defaultBlockState(),
                        UniformInt.of(2, 8),
                        2,
                        List.of(Blocks.TERRACOTTA.defaultBlockState(), Blocks.RED_SAND.defaultBlockState())
                )
        );
    }

    @Override
    public void populate(WorldInfo worldInfo, Random random, int x, int z, LimitedRegion limitedRegion) {
        if (isFeatureChunk(random, 30)) {
            int grassX = x * 16;
            int grassZ = z * 16;
            int grassY = 128;
            for (int i = 128; i > 60; i--) {
                if (limitedRegion.getType(grassX, grassY, grassZ).equals(Material.AIR)) {
                    grassY--;
                } else {
                    break;
                }
            }
            if (grassY > 60 && limitedRegion.isInRegion(grassX, grassY, grassZ) && !limitedRegion.getType(grassX, grassY, grassZ).equals(Material.WATER)) {
                BaseDiskFeature base = new BaseDiskFeature(DiskConfiguration.CODEC);
                WorldGenLevel level = ((CraftRegionAccessor) limitedRegion).getHandle();
                ChunkGenerator generator = level.getMinecraftWorld().getChunkSource().getGenerator();
                base.place(DISK_GRASS.value().config(), level, generator, random, new BlockPos(grassX, grassY, grassZ));
            }
        }
    }

    public boolean isFeatureChunk(Random random, int chance) {
        return random.nextInt(100) < chance;
    }
}
