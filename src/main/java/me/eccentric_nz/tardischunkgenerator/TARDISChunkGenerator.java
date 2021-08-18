/*
 * Copyright (C) 2020 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.tardischunkgenerator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;

import java.util.Random;

/**
 * @author eccentric_nz
 */
public class TARDISChunkGenerator extends ChunkGenerator {

    /**
     * Generates an empty world!
     *
     * @param worldInfo the world to generate chunks in
     * @param random    a pseudorandom number generator
     * @param x         the chunk's x coordinate
     * @param z         the chunk's z coordinate
     * @param chunkData the ChunkData being generated
     */
    @Override
    public void generateNoise(WorldInfo worldInfo, Random random, int x, int z, ChunkData chunkData) {
        chunkData.setRegion(0, 0, 0, 16, chunkData.getMaxHeight(), 16, Material.VOID_AIR);
    }

    @Override
    public boolean shouldGenerateNoise() {
        return true;
    }

    @Override
    public boolean shouldGenerateSurface() {
        return false;
    }

    @Override
    public boolean shouldGenerateBedrock() {
        return false;
    }

    @Override
    public boolean shouldGenerateCaves() {
        return false;
    }

    @Override
    public boolean shouldGenerateDecorations() {
        return false;
    }

    @Override
    public boolean shouldGenerateMobs() {
        return false;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }

    /**
     * Gets the fixed spawn location of a world.
     *
     * @param world  the world from which to get the spawn location
     * @param random a pseudorandom number generator
     * @return the spawn location of the world
     */
    @Override
    public Location getFixedSpawnLocation(World world, Random random) {
        return new Location(world, 0, 70, 0);
    }
}
