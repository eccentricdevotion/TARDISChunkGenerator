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
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

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
    @SuppressWarnings("deprecation")
    public byte[] generate(World world, Random random, int cx, int cz) {
        byte[] result = new byte[32768];
        for (int i = 0; i < result.length; i++) {
            //result[i] = ((byte) Material.AIR.getId());
            result[i] = ((byte) 0);
        }
        return result;
    }
}
