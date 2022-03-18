package me.eccentric_nz.tardischunkgenerator.worldgen;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;

import java.util.Random;
import java.util.logging.Level;

public class IslandBlockPopulator extends BlockPopulator {

    private final int chance = 1;

    @Override
    public void populate(WorldInfo worldInfo, Random random, int x, int z, LimitedRegion limitedRegion) {
        Bukkit.getLogger().log(Level.INFO, "Chunk coords: " + x + ", " + z);
        if ((x == 0 && z == 0) || random.nextInt(100) < chance) {
            Bukkit.getLogger().log(Level.INFO, "Making an island?");
            // make an island ?
            int randomX = x * 16 + random.nextInt(16);
            int randomZ = z * 16 + random.nextInt(16);
            int y = 91;

            Material material;
            if (random.nextInt(100) < 90) {
                material = Material.DIRT;
            } else {
                material = Material.STONE;
            }
            limitedRegion.setType(randomX + 8, y, randomZ + 8, material);

            boolean[] booleans = new boolean[2048];
            int r = random.nextInt(4) + 4;
            int j, j1, k1;

            for (j = 0; j < r; ++j) {
                double d0 = random.nextDouble() * 6.0D + 3.0D;
                double d1 = random.nextDouble() * 4.0D + 2.0D;
                double d2 = random.nextDouble() * 6.0D + 3.0D;
                double d3 = random.nextDouble() * (16.0D - d0 - 2.0D) + 1.0D + d0 / 2.0D;
                double d4 = random.nextDouble() * (8.0D - d1 - 4.0D) + 2.0D + d1 / 2.0D;
                double d5 = random.nextDouble() * (16.0D - d2 - 2.0D) + 1.0D + d2 / 2.0D;

                for (int k = 1; k < 15; ++k) {
                    for (int l = 1; l < 15; ++l) {
                        for (int i1 = 0; i1 < 7; ++i1) {
                            double d6 = (k - d3) / (d0 / 2.0D);
                            double d7 = (i1 - d4) / (d1 / 2.0D);
                            double d8 = (l - d5) / (d2 / 2.0D);
                            double d9 = d6 * d6 + d7 * d7 + d8 * d8;
                            if (d9 < 1.0D) {
                                booleans[(k * 16 + l) * 8 + i1] = true;
                            }
                        }
                    }
                }
            }
            for (j = 0; j < 16; ++j) {
                for (k1 = 0; k1 < 16; ++k1) {
                    for (j1 = 0; j1 < 8; ++j1) {
                        if (booleans[(j * 16 + k1) * 8 + j1]) {
                            limitedRegion.setType(randomX + j, y + j1, randomZ + k1, (j1 > 4 ? Material.AIR : material));
                        }
                    }
                }
            }
            for (j = 0; j < 16; ++j) {
                for (k1 = 0; k1 < 16; ++k1) {
                    for (j1 = 4; j1 < 8; ++j1) {
                        if (booleans[(j * 16 + k1) * 8 + j1]) {
                            int X1 = randomX + j;
                            int Y1 = y + j1 - 1;
                            int Z1 = randomZ + k1;
                            if (limitedRegion.getType(X1, Y1, Z1) == Material.DIRT) {
                                limitedRegion.setType(X1, Y1, Z1, Material.GRASS_BLOCK);
                            }
                        }
                    }
                }
            }
            int treeX = x * 16 + random.nextInt(16);
            int treeZ = z * 16 + random.nextInt(16);
            int treeY = 95;
            if (!limitedRegion.isInRegion(treeX, treeY, treeZ)) {
                Bukkit.getLogger().log(Level.INFO, "Location (" + treeX + "," + treeY + "," + treeZ + ") is not in limited region!");
                return;
            }
            if (limitedRegion.isInRegion(treeX, treeY, treeZ)) {
                limitedRegion.generateTree(new Location(null, treeX, treeY, treeZ), random, TreeType.TREE);
            }
        }
    }
}
