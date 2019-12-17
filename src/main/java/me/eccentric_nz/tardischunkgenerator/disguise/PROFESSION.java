package me.eccentric_nz.tardischunkgenerator.disguise;

import net.minecraft.server.v1_15_R1.VillagerProfession;
import org.bukkit.entity.Villager;

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

    public static PROFESSION getFromVillagerProfression(Villager.Profession profession) {
        return PROFESSION.valueOf(profession.toString());
    }

    public VillagerProfession getNmsProfession() {
        return nms;
    }
}
