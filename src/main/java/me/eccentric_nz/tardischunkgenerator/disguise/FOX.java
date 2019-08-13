package me.eccentric_nz.tardischunkgenerator.disguise;

import net.minecraft.server.v1_14_R1.EntityFox;

public enum FOX {

    RED(EntityFox.Type.RED),
    SNOW(EntityFox.Type.SNOW);

    private final EntityFox.Type nmsType;

    FOX(EntityFox.Type nmsType) {
        this.nmsType = nmsType;
    }

    public EntityFox.Type getNmsType() {
        return nmsType;
    }
}
