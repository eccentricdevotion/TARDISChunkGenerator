package me.eccentric_nz.tardischunkgenerator.worldgen;

import org.bukkit.util.BlockVector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SkaroStructureUtility {

    public static Set<BlockVector> vectorLeft = new HashSet<>();
    public static Set<BlockVector> vectorUp = new HashSet<>();
    public static Set<BlockVector> vectorRight = new HashSet<>();
    public static Set<BlockVector> vectorDown = new HashSet<>();
    public static List<String> structures = Arrays.asList("small", "farm", "tower", "pen", "water", "small");

    static {
        // left
        vectorLeft.add(new BlockVector(-11, 0, 2)); // connected small
        vectorLeft.add(new BlockVector(1, 0, -15)); // farm
        vectorLeft.add(new BlockVector(17, 0, 16)); // tower
        vectorLeft.add(new BlockVector(19, 0, 18)); // animal pen
        vectorLeft.add(new BlockVector(4, 0, 19)); // water feature
        vectorLeft.add(new BlockVector(-16, 0, 16)); // small
        // up
        vectorUp.add(new BlockVector(2, 0, -11)); // connected small
        vectorUp.add(new BlockVector(19, 0, 1)); // farm
        vectorUp.add(new BlockVector(17, 0, 17)); // tower
        vectorUp.add(new BlockVector(-16, 0, 19)); // animal pen
        vectorUp.add(new BlockVector(-16, 0, 4)); // water feature
        vectorUp.add(new BlockVector(-16, 0, -16)); // small
        // right
        vectorRight.add(new BlockVector(15, 0, 2)); // connected small
        vectorRight.add(new BlockVector(3, 0, 19)); // farm
        vectorRight.add(new BlockVector(-16, 0, 17)); // tower
        vectorRight.add(new BlockVector(-16, 0, -16)); // animal pen
        vectorRight.add(new BlockVector(4, 0, -15)); // water feature
        vectorRight.add(new BlockVector(20, 0, -16)); // small
        // down
        vectorDown.add(new BlockVector(2, 0, 15)); // connected small
        vectorDown.add(new BlockVector(-16, 0, 3)); // farm
        vectorDown.add(new BlockVector(-16, 0, -16)); // tower
        vectorDown.add(new BlockVector(18, 0, -16)); // animal pen
        vectorDown.add(new BlockVector(19, 0, 4)); // water feature
        vectorDown.add(new BlockVector(20, 0, 17)); // small
    }
}
