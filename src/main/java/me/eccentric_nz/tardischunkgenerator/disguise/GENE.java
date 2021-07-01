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
package me.eccentric_nz.tardischunkgenerator.disguise;

import net.minecraft.world.entity.animal.EntityPanda;
import org.bukkit.entity.Panda;

public enum GENE {

    NORMAL(EntityPanda.Gene.a), // NORMAL
    LAZY(EntityPanda.Gene.b), // LAZY
    WORRIED(EntityPanda.Gene.c), // WORRIED
    PLAYFUL(EntityPanda.Gene.d), // PLAYFUL
    BROWN(EntityPanda.Gene.e), // BROWN
    WEAK(EntityPanda.Gene.f), // WEAK
    AGGRESSIVE(EntityPanda.Gene.g); // AGGRESSIVE

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
