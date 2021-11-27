package me.eccentric_nz.tardischunkgenerator.custombiome;

import net.minecraft.core.BlockPos;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;

import java.util.Optional;
import java.util.Random;

public class CustomTree {

    public static final ConfiguredFeature<HugeFungusConfiguration, ?> SKARO_TREE = FeatureUtils.register("skaro_tree", Feature.HUGE_FUNGUS.configured(new HugeFungusConfiguration(Blocks.SAND
            .defaultBlockState(), Blocks.ACACIA_LOG
            .defaultBlockState(), Blocks.SLIME_BLOCK
            .defaultBlockState(), Blocks.HONEY_BLOCK
            .defaultBlockState(), true)));
    public static final ConfiguredFeature<HugeFungusConfiguration, ?> GALLIFREY_TREE = FeatureUtils.register("gallifrey_tree", Feature.HUGE_FUNGUS.configured(new HugeFungusConfiguration(Blocks.RED_SAND
            .defaultBlockState(), Blocks.STRIPPED_BIRCH_LOG
            .defaultBlockState(), Blocks.COBWEB
            .defaultBlockState(), Blocks.RED_WOOL
            .defaultBlockState(), true)));
    private static final Random random = new Random();

    public static void grow(TARDISTree tree, Location location) {
        ConfiguredFeature<HugeFungusConfiguration, ?> gen = (tree == TARDISTree.GALLIFREY) ? GALLIFREY_TREE : SKARO_TREE;
        ServerLevel worldServer = ((CraftWorld) location.getWorld()).getHandle();
        ChunkGenerator chunkGenerator = worldServer.getChunkSource().getGenerator();
        BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        gen.feature.place(new FeaturePlaceContext(Optional.empty(), worldServer, chunkGenerator, random, pos, gen.config));
    }

    public enum TARDISTree {
        GALLIFREY, SKARO
    }
}
