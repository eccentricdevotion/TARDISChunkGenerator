package me.eccentric_nz.tardischunkgenerator.disguise;

import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.EntitySnowman;
import net.minecraft.server.v1_14_R1.EntityTypes;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;

public class TARDISDalekDisguise {

    public static Entity createDalekDisguise(World w) {
        net.minecraft.server.v1_14_R1.World world = ((CraftWorld) w).getHandle();
        EntitySnowman entity = new EntitySnowman(EntityTypes.SNOW_GOLEM, world);
        entity.setHasPumpkin(false);
        return entity;
    }
}
