package me.eccentric_nz.tardischunkgenerator;

import net.minecraft.server.v1_14_R1.EntityPanda;

public enum GENE {

    NORMAL(EntityPanda.Gene.NORMAL),
    LAZY(EntityPanda.Gene.LAZY),
    WORRIED(EntityPanda.Gene.WORRIED),
    PLAYFUL(EntityPanda.Gene.PLAYFUL),
    BROWN(EntityPanda.Gene.BROWN),
    WEAK(EntityPanda.Gene.WEAK),
    AGGRESSIVE(EntityPanda.Gene.AGGRESSIVE);

    private final EntityPanda.Gene nmsGene;

    GENE(EntityPanda.Gene nmsGene) {
        this.nmsGene = nmsGene;
    }

    public EntityPanda.Gene getNmsGene() {
        return nmsGene;
    }
}
