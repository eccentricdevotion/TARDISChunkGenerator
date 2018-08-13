package me.eccentric_nz.tardischunkgenerator;

import net.minecraft.server.v1_13_R1.BlockPosition;
import net.minecraft.server.v1_13_R1.PersistentVillage;
import net.minecraft.server.v1_13_R1.Village;
import net.minecraft.server.v1_13_R1.WorldServer;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TARDISVillageFinder {

    List<Location> villages = new ArrayList<>();
    Random random = new Random();

    public void find(org.bukkit.World world) {
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        PersistentVillage villageStorage = nmsWorld.ae();
        for (Village v : villageStorage.getVillages()) {
            BlockPosition b = v.a();
            Location l = new Location(world, b.getX(), b.getY(), b.getZ());
            villages.add(l);
        }
    }

    public List<Location> getVillages() {
        return villages;
    }

    public Location getRandomVillage() {
        if (villages.size() > 0) {
            return villages.get(random.nextInt(villages.size()));
        }
        return null;
    }
}
