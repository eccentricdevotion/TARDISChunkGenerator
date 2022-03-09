package me.eccentric_nz.tardischunkgenerator.worldgen;

import com.google.common.collect.Lists;
import org.bukkit.block.Biome;
import org.bukkit.generator.BiomeProvider;
import org.bukkit.generator.WorldInfo;

import java.util.List;

public class SkaroBiomeProvider extends BiomeProvider {

    @Override
    public Biome getBiome(WorldInfo worldInfo, int x, int y, int z) {
        return Biome.DESERT;
    }

    @Override
    public List<Biome> getBiomes(WorldInfo worldInfo) {
        return Lists.newArrayList(Biome.DESERT);
    }
}
