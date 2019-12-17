package me.eccentric_nz.tardischunkgenerator.disguise;

import net.minecraft.server.v1_15_R1.EntityFox;
import org.bukkit.entity.Fox;

public enum FOX {

    RED(EntityFox.Type.RED),
    SNOW(EntityFox.Type.SNOW);

    private final EntityFox.Type nmsType;

    FOX(EntityFox.Type nmsType) {
        this.nmsType = nmsType;
    }

    public static FOX getFromFoxType(Fox.Type type) {
        return FOX.valueOf(type.toString());
    }

    public EntityFox.Type getNmsType() {
        return nmsType;
    }
}
