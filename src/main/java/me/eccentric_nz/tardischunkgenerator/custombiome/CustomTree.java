package me.eccentric_nz.tardischunkgenerator.custombiome;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.HugeFungusConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_18_R2.CraftRegionAccessor;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.block.data.CraftBlockData;
import org.bukkit.generator.LimitedRegion;

import java.util.Locale;
import java.util.Random;

public class CustomTree {

    public static final Holder<ConfiguredFeature<HugeFungusConfiguration, ?>> SKARO_TREE = FeatureUtils.register("skaro_tree", Feature.HUGE_FUNGUS,
            new HugeFungusConfiguration(
                    Blocks.SAND.defaultBlockState(),
                    Blocks.ACACIA_LOG.defaultBlockState(),
                    Blocks.SLIME_BLOCK.defaultBlockState(),
                    Blocks.HONEY_BLOCK.defaultBlockState(),
                    true
            )
    );
    public static final Holder<ConfiguredFeature<HugeFungusConfiguration, ?>> GALLIFREY_TREE_RED_SAND = FeatureUtils.register("gallifrey_tree_red_sand", Feature.HUGE_FUNGUS,
            new HugeFungusConfiguration(
                    Blocks.RED_SAND.defaultBlockState(),
                    Blocks.STRIPPED_BIRCH_LOG.defaultBlockState(),
                    Blocks.COBWEB.defaultBlockState(),
                    Blocks.RED_WOOL.defaultBlockState(),
                    true
            )
    );
    public static final Holder<ConfiguredFeature<HugeFungusConfiguration, ?>> GALLIFREY_TREE_TERRACOTTA = FeatureUtils.register("gallifrey_tree_terracotta", Feature.HUGE_FUNGUS,
            new HugeFungusConfiguration(
                    Blocks.TERRACOTTA.defaultBlockState(),
                    Blocks.STRIPPED_BIRCH_LOG.defaultBlockState(),
                    Blocks.COBWEB.defaultBlockState(),
                    Blocks.RED_WOOL.defaultBlockState(),
                    true
            )
    );
    private static final Random random = new Random();

    public static void grow(TARDISTree tree, Location location) {
        Holder<ConfiguredFeature<HugeFungusConfiguration, ?>> gen;
        switch (tree) {
            case GALLIFREY_SAND -> gen = GALLIFREY_TREE_RED_SAND;
            case GALLIFREY_TERRACOTTA -> gen = GALLIFREY_TREE_TERRACOTTA;
            case SKARO -> gen = SKARO_TREE;
            default -> { // RANDOM
                Material stem = getRandomMaterial();
                Material hat = getRandomMaterial();
                Material decor = getRandomMaterial();
                gen = FeatureUtils.register(generateRandomString() + "_tree", Feature.HUGE_FUNGUS,
                        new HugeFungusConfiguration(
                                Blocks.GRASS_BLOCK.defaultBlockState(),
                                ((CraftBlockData) Bukkit.createBlockData(stem)).getState(),
                                ((CraftBlockData) Bukkit.createBlockData(hat)).getState(),
                                ((CraftBlockData) Bukkit.createBlockData(decor)).getState(),
                                true
                        )
                );
            }
        }
        ServerLevel worldServer = ((CraftWorld) location.getWorld()).getHandle();
        ChunkGenerator chunkGenerator = worldServer.getChunkSource().getGenerator();
        BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        gen.value().place(worldServer, chunkGenerator, random, pos);
    }

    public static void grow(TARDISTree tree, int x, int y, int z, LimitedRegion limitedRegion) {
        Holder<ConfiguredFeature<HugeFungusConfiguration, ?>> gen;
        switch (tree) {
            case GALLIFREY_SAND -> gen = GALLIFREY_TREE_RED_SAND;
            case GALLIFREY_TERRACOTTA -> gen = GALLIFREY_TREE_TERRACOTTA;
            case SKARO -> gen = SKARO_TREE;
            default -> { // RANDOM
                Material stem = getRandomMaterial();
                Material hat = getRandomMaterial();
                Material decor = getRandomMaterial();
                gen = FeatureUtils.register(generateRandomString() + "_tree", Feature.HUGE_FUNGUS,
                        new HugeFungusConfiguration(
                                Blocks.GRASS_BLOCK.defaultBlockState(),
                                ((CraftBlockData) Bukkit.createBlockData(stem)).getState(),
                                ((CraftBlockData) Bukkit.createBlockData(hat)).getState(),
                                ((CraftBlockData) Bukkit.createBlockData(decor)).getState(),
                                true
                        )
                );
            }
        }
        WorldGenLevel level = ((CraftRegionAccessor) limitedRegion).getHandle();
        ChunkGenerator chunkGenerator = level.getMinecraftWorld().getChunkSource().getGenerator();
        BlockPos pos = new BlockPos(x, y, z);
        gen.value().place(level, chunkGenerator, random, pos);
    }

    public static void grow(Location location, Material base, Material stem, Material hat, Material decor) {
        Holder<ConfiguredFeature<HugeFungusConfiguration, ?>> TREE = FeatureUtils.register(hat.toString().toLowerCase(Locale.ROOT) + "_tree", Feature.HUGE_FUNGUS,
                new HugeFungusConfiguration(
                        ((CraftBlockData) Bukkit.createBlockData(base)).getState(),
                        ((CraftBlockData) Bukkit.createBlockData(stem)).getState(),
                        ((CraftBlockData) Bukkit.createBlockData(hat)).getState(),
                        ((CraftBlockData) Bukkit.createBlockData(decor)).getState(),
                        true
                )
        );
        ServerLevel worldServer = ((CraftWorld) location.getWorld()).getHandle();
        ChunkGenerator chunkGenerator = worldServer.getChunkSource().getGenerator();
        BlockPos pos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        TREE.value().place(worldServer, chunkGenerator, random, pos);
    }

    private static String generateRandomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 6;
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString;
    }

    private static Material getRandomMaterial() {
        return CubicMaterial.cubes.get(random.nextInt(CubicMaterial.cubes.size()));
    }

    public enum TARDISTree {
        GALLIFREY_SAND, GALLIFREY_TERRACOTTA, SKARO, RANDOM
    }
}
