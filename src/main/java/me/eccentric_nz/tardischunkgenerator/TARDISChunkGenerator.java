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
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

/**
 * @author eccentric_nz
 */
public class TARDISChunkGenerator extends ChunkGenerator {

    /**
     * Generates an empty world!
     *
     * @param world  the world to generate chunks in
     * @param random a pseudorandom number generator
     * @param cx     the chunk's x coordinate
     * @param cz     the chunk's z coordinate
     * @return the ChunkData to be generated
     */
    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int cx, int cz, @NotNull BiomeGrid biome) {

        ChunkData result = createChunkData(world);
        result.setRegion(0, 0, 0, 16, world.getMaxHeight(), 16, Material.VOID_AIR);
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < world.getMaxHeight(); y++) {
                    biome.setBiome(x, y, z, Biome.THE_VOID);
                }
            }
        }
        return result;
    }

    /**
     * Gets the fixed spawn location of a world.
     *
     * @param world  the world from which to get the spawn location
     * @param random a pseudorandom number generator
     * @return the spawn location of the world
     */
    @Override
    public Location getFixedSpawnLocation(@NotNull World world, @NotNull Random random) {
        return new Location(world, 0, 70, 0);
    }
}
