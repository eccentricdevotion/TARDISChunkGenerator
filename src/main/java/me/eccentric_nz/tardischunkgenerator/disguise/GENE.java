package me.eccentric_nz.tardischunkgenerator.disguise;

import net.minecraft.server.v1_15_R1.EntityPanda;
import org.bukkit.entity.Panda;

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

    public static GENE getFromPandaGene(Panda.Gene gene) {
        return GENE.valueOf(gene.toString());
    }

    public EntityPanda.Gene getNmsGene() {
        return nmsGene;
    }
}
