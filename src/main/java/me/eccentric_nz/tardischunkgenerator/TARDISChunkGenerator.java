/*
 * Copyright (C) 2018 eccentric_nz
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

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.MaterialData;

/**
 *
 * @author eccentric_nz
 */
public class TARDISChunkGenerator extends ChunkGenerator {

    /**
     * Generates an empty world!
     *
     * @param world the world to generate chunks in
     * @param random
     * @param cx
     * @param cz
     * @return
     */
    @Override
    public ChunkData generateChunkData(World world, Random random, int cx, int cz, BiomeGrid biome) {

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                    biome.setBiome(x, z, Biome.THE_VOID);
            }
        }
        return createChunkData(world);
    }
}
