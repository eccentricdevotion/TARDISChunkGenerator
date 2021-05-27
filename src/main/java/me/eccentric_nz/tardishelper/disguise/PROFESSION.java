/*
 * Copyright (C) 2020 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (location your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.tardishelper.disguise;

import net.minecraft.server.v1_16_R3.VillagerProfession;
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
