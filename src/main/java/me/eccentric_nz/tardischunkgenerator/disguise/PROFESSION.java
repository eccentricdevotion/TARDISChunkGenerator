package me.eccentric_nz.tardischunkgenerator.disguise;

import net.minecraft.server.v1_14_R1.VillagerProfession;

public enum PROFESSION {

    NONE(VillagerProfession.NONE),
    ARMORER(VillagerProfession.ARMORER),
    BUTCHER(VillagerProfession.BUTCHER),
    CARTOGRAPHER(VillagerProfession.CARTOGRAPHER),
    CLERIC(VillagerProfession.CLERIC),
    FARMER(VillagerProfession.FARMER),
    FISHERMAN(VillagerProfession.FISHERMAN),
    FLETCHER(VillagerProfession.FLETCHER),
    LEATHERWORKER(VillagerProfession.LEATHERWORKER),
    LIBRARIAN(VillagerProfession.LIBRARIAN),
    MASON(VillagerProfession.MASON),
    NITWIT(VillagerProfession.NITWIT),
    SHEPHERD(VillagerProfession.SHEPHERD),
    TOOLSMITH(VillagerProfession.TOOLSMITH),
    WEAPONSMITH(VillagerProfession.WEAPONSMITH);

    private final VillagerProfession nms;

    PROFESSION(VillagerProfession nmsProfession) {
        nms = nmsProfession;
    }

    public VillagerProfession getNmsProfession() {
        return nms;
    }
}
