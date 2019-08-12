package me.eccentric_nz.tardischunkgenerator;

import net.minecraft.server.v1_14_R1.EntityMushroomCow;

public enum MUSHROOM_COW {

    BROWN(EntityMushroomCow.Type.BROWN),
    RED(EntityMushroomCow.Type.RED);

    private final EntityMushroomCow.Type nmsType;

    MUSHROOM_COW(EntityMushroomCow.Type nmsType) {
        this.nmsType = nmsType;
    }

    public EntityMushroomCow.Type getNmsType() {
        return nmsType;
    }
}
