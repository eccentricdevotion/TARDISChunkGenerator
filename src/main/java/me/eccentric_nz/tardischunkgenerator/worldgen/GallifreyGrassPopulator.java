package me.eccentric_nz.tardischunkgenerator.worldgen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.DiskFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.CraftRegionAccessor;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

public class GallifreyGrassPopulator extends BlockPopulator {

    public final Holder<ConfiguredFeature<DiskConfiguration, ?>> DISK_GRASS;

    public GallifreyGrassPopulator() {
        DISK_GRASS = FeatureUtils.register("gallifrey_disk_grass", Feature.DISK,
                new DiskConfiguration(
                        RuleBasedBlockStateProvider.simple(Blocks.GRASS_BLOCK),
                        BlockPredicate.matchesBlocks(Blocks.TERRACOTTA, Blocks.RED_SAND),
                        UniformInt.of(2, 8),
                        2
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
                DiskFeature base = new DiskFeature(DiskConfiguration.CODEC);
                WorldGenLevel level = ((CraftRegionAccessor) limitedRegion).getHandle();
                ChunkGenerator generator = level.getMinecraftWorld().getChunkSource().getGenerator();
                base.place(DISK_GRASS.value().config(), level, generator, RandomSource.create(random.nextLong()), new BlockPos(grassX, grassY, grassZ));
            }
        }
    }

    public boolean isFeatureChunk(Random random, int chance) {
        return random.nextInt(100) < chance;
    }
}
