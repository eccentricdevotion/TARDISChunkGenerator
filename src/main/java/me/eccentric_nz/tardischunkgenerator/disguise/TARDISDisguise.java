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

import me.eccentric_nz.tardischunkgenerator.TARDISHelper;
import net.minecraft.core.IRegistry;
import net.minecraft.network.chat.ChatMessage;
import net.minecraft.world.entity.EntityAgeable;
import net.minecraft.world.entity.EntityTameableAnimal;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.entity.ambient.EntityBat;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.animal.horse.*;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.EntityVillager;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_17_R1.util.CraftNamespacedKey;
import org.bukkit.entity.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class TARDISDisguise {

    private final EntityType entityType;
    private final Object[] options;

    public TARDISDisguise(EntityType entityType, Object[] options) {
        this.entityType = entityType;
        this.options = options;
    }

    public static net.minecraft.world.entity.Entity createMobDisguise(TARDISDisguise disguise, World w) {
        String str;
        String packagePath = "net.minecraft.world.entity.";
        boolean hasEntityStr = true;
        switch (disguise.getEntityType()) {
            case AXOLOTL -> {
                str = "Axolotl";
                packagePath += "animal.axolotl.";
                hasEntityStr = false;
            }
            case BAT -> {
                str = "Bat";
                packagePath += "ambient.";
            }
            case GOAT -> {
                str = "Goat";
                packagePath += "animal.goat.";
                hasEntityStr = false;
            }
            case ZOMBIE_HORSE, SKELETON_HORSE, TRADER_LLAMA -> {
                str = switchAndCapitalise(disguise.getEntityType().toString());
                packagePath += "animal.horse.";
            }
            case ELDER_GUARDIAN, WITHER_SKELETON -> {
                str = switchAndCapitalise(disguise.getEntityType().toString());
                packagePath += "monster.";
            }
            case WANDERING_TRADER -> {
                str = "VillagerTrader";
                packagePath += "npc.";
            }
            case HUSK -> {
                str = "ZombieHusk";
                packagePath += "monster.";
            }
            case STRAY -> {
                str = "SkeletonStray";
                packagePath += "monster.";
            }
            case PUFFERFISH -> {
                str = "PufferFish";
                packagePath += "animal.";
            }
            case ILLUSIONER -> {
                str = "IllagerIllusioner";
                packagePath += "monster.";
            }
            case GIANT -> {
                str = "GiantZombie";
                packagePath += "monster.";
            }
            case HORSE, LLAMA -> {
                str = capitalise(disguise.getEntityType().toString());
                packagePath += "animal.horse.";
            }
            case DONKEY, MULE -> {
                str = "Horse" + capitalise(disguise.getEntityType().toString());
                packagePath += "animal.horse.";
            }
            case VILLAGER -> {
                str = "Villager";
                packagePath += "npc.";
            }
            case ZOMBIFIED_PIGLIN -> {
                str = "PigZombie";
                packagePath += "monster.";
            }
            case BLAZE, CAVE_SPIDER, CREEPER, DROWNED, ENDERMAN, ENDERMITE, EVOKER, GHAST, GUARDIAN, MAGMA_CUBE, PHANTOM, PILLAGER, RAVAGER, SHULKER, SILVERFISH, SKELETON, SLIME, SPIDER, STRIDER, VEX, VINDICATOR, WITCH, ZOGLIN, ZOMBIE, ZOMBIE_VILLAGER -> {
                str = capitalise(disguise.getEntityType().toString());
                packagePath += "monster.";
            }
            case HOGLIN -> {
                str = "Hoglin";
                packagePath += "monster.hoglin.";
            }
            case WITHER -> {
                str = "Wither";
                packagePath += "boss.wither.";
            }
            case PIGLIN, PIGLIN_BRUTE -> {
                str = capitalise(disguise.getEntityType().toString());
                packagePath += "monster.piglin.";
            }
            case GLOW_SQUID -> {
                str = "GlowSquid";
//                packagePath = "";
                hasEntityStr = false;
            }
            default -> {
                str = capitalise(disguise.getEntityType().toString());
                packagePath += "animal.";
            }
        }
        try {
            String entityPackage = packagePath + ((hasEntityStr) ? "Entity" : "") + str;
            Class entityClass = Class.forName(entityPackage);
            Constructor constructor = entityClass.getConstructor(EntityTypes.class, net.minecraft.world.level.World.class);
            EntityTypes type = IRegistry.Y.get(CraftNamespacedKey.toMinecraft(disguise.getEntityType().getKey()));
            net.minecraft.world.level.World world = ((CraftWorld) w).getHandle();
            net.minecraft.world.entity.Entity entity = (net.minecraft.world.entity.Entity) constructor.newInstance(type, world);
            if (disguise.getOptions() != null) {
                for (Object o : disguise.getOptions()) {
                    if (o instanceof DyeColor) {
                        // colour a sheep / wolf collar
                        switch (disguise.getEntityType()) {
                            case SHEEP -> {
                                EntitySheep sheep = (EntitySheep) entity;
                                sheep.setColor(EnumColor.valueOf(o.toString()));
                            }
                            case WOLF -> {
                                EntityWolf wolf = (EntityWolf) entity;
                                wolf.setTamed(true);
                                wolf.setCollarColor(EnumColor.valueOf(o.toString()));
                            }
                            default -> {
                            }
                        }
                    }
                    if (o instanceof Axolotl.Variant && disguise.getEntityType().equals(EntityType.AXOLOTL)) {
                        net.minecraft.world.entity.animal.axolotl.Axolotl axolotl = (net.minecraft.world.entity.animal.axolotl.Axolotl) entity;
                        net.minecraft.world.entity.animal.axolotl.Axolotl.Variant variant = net.minecraft.world.entity.animal.axolotl.Axolotl.Variant.values()[((Axolotl.Variant) o).ordinal()];
                        axolotl.setVariant(variant);
                    }
                    if (o instanceof Rabbit.Type && disguise.getEntityType().equals(EntityType.RABBIT)) {
                        EntityRabbit rabbit = (EntityRabbit) entity;
                        rabbit.setRabbitType(((Rabbit.Type) o).ordinal());
                    }
                    if (o instanceof GENE && disguise.getEntityType().equals(EntityType.PANDA)) {
                        EntityPanda panda = (EntityPanda) entity;
                        EntityPanda.Gene gene = ((GENE) o).getNmsGene();
                        panda.setMainGene(gene);
                        panda.setHiddenGene(gene);
                    }
                    if (o instanceof PROFESSION) {
                        if (disguise.getEntityType().equals(EntityType.VILLAGER)) {
                            EntityVillager villager = (EntityVillager) entity;
                            villager.setVillagerData(villager.getVillagerData().withProfession(((PROFESSION) o).getNmsProfession()));
                        } else if (disguise.getEntityType().equals(EntityType.ZOMBIE_VILLAGER)) {
                            EntityZombieVillager zombie = (EntityZombieVillager) entity;
                            zombie.setVillagerData(zombie.getVillagerData().withProfession(((PROFESSION) o).getNmsProfession()));
                        }
                    }
                    if (o instanceof Parrot.Variant && disguise.getEntityType().equals(EntityType.PARROT)) {
                        EntityParrot parrot = (EntityParrot) entity;
                        parrot.setVariant(((Parrot.Variant) o).ordinal());
                    }
                    if (o instanceof MUSHROOM_COW && disguise.getEntityType().equals(EntityType.MUSHROOM_COW)) {
                        EntityMushroomCow cow = (EntityMushroomCow) entity;
                        cow.setVariant(((MUSHROOM_COW) o).getNmsType());
                    }
                    if (o instanceof Cat.Type && disguise.getEntityType().equals(EntityType.CAT)) {
                        EntityCat cat = (EntityCat) entity;
                        cat.setCatType(((Cat.Type) o).ordinal());
                    }
                    if (o instanceof FOX && disguise.getEntityType().equals(EntityType.FOX)) {
                        EntityFox fox = (EntityFox) entity;
                        fox.setFoxType(((FOX) o).getNmsType());
                    }
                    if (o instanceof Horse.Color && disguise.getEntityType().equals(EntityType.HORSE)) {
                        EntityHorse horse = (EntityHorse) entity;
                        horse.setVariant(((HorseColor) o), HorseStyle.values()[new Random().nextInt(HorseStyle.values().length)]);
                    }
                    if (o instanceof Llama.Color && disguise.getEntityType().equals(EntityType.LLAMA)) {
                        EntityLlama llama = (EntityLlama) entity;
                        llama.setVariant(((Llama.Color) o).ordinal());
                    }
                    if (o instanceof Boolean) {
                        // tamed fox, wolf, cat / decorated llama, chest carrying mule or donkey / trusting ocelot
                        // rainbow sheep / saddled pig / block carrying enderman / powered creeper / hanging bat / blazing blaze
                        switch (disguise.getEntityType()) {
                            case FOX, WOLF, CAT -> {
                                EntityTameableAnimal tameable = (EntityTameableAnimal) entity;
                                tameable.setTamed((Boolean) o);
                            }
                            case DONKEY, MULE -> {
                                EntityHorseChestedAbstract chesty = (EntityHorseChestedAbstract) entity;
                                chesty.setCarryingChest((Boolean) o);
                            }
                            case SHEEP -> {
                                if ((Boolean) o) {
                                    entity.setCustomName(new ChatMessage("jeb_"));
                                    entity.setCustomNameVisible(true);
                                }
                            }
                            case PIG -> {
                                EntityPig pig = (EntityPig) entity;
                                pig.saddle(null);
                            }
                            case ENDERMAN -> {
                                if ((Boolean) o) {
                                    EntityEnderman enderman = (EntityEnderman) entity;
                                    IBlockData block = Blocks.iN.getBlockData(); // iN = PURPUR_BLOCK
                                    enderman.setCarried(block);
                                }
                            }
                            case CREEPER -> {
                                EntityCreeper creeper = (EntityCreeper) entity;
                                creeper.setPowered((Boolean) o);
                            }
                            case BAT -> {
                                EntityBat bat = (EntityBat) entity;
                                bat.setAsleep((Boolean) o);
                            }
                            case SNOWMAN -> {
                                EntitySnowman snowman = (EntitySnowman) entity;
                                snowman.setHasPumpkin(!(Boolean) o);
                            }
                            case PILLAGER -> {
                                if ((Boolean) o) {
                                    EntityPillager pillager = (EntityPillager) entity;
                                    ItemStack crossbow = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(org.bukkit.Material.CROSSBOW));
                                    pillager.setSlot(EnumItemSlot.a, crossbow); // a = MAINHAND
                                    pillager.a(pillager, 1.0f);
                                }
                            }
                            case LLAMA -> {
                                EntityLlama llama = (EntityLlama) entity;
                                org.bukkit.inventory.ItemStack bukkitItemStack = new org.bukkit.inventory.ItemStack(CARPET.values()[ThreadLocalRandom.current().nextInt(16)].getCarpet());
                                ItemStack nmsItemStack = CraftItemStack.asNMSCopy(bukkitItemStack);
                                llama.cd.setItem(1, nmsItemStack); // cd = inventoryChest
                            }
                            default -> {
                            }
                        }
                    }
                    if (o instanceof Integer) {
                        // magma cube and slime size / pufferfish state
                        switch (disguise.getEntityType()) {
                            case MAGMA_CUBE -> {
                                EntityMagmaCube magma = (EntityMagmaCube) entity;
                                magma.setSize((Integer) o, false);
                            }
                            case SLIME -> {
                                EntitySlime slime = (EntitySlime) entity;
                                slime.setSize((Integer) o, false);
                            }
                            case PUFFERFISH -> {
                                EntityPufferFish puffer = (EntityPufferFish) entity;
                                puffer.setPuffState((Integer) o);
                            }
                            default -> {
                            }
                        }
                    }
                    if (o instanceof TropicalFish.Pattern && disguise.getEntityType().equals(EntityType.TROPICAL_FISH)) {
                        EntityTropicalFish fish = (EntityTropicalFish) entity;
                        int var5 = ThreadLocalRandom.current().nextInt(2); // shape
                        int var6 = ((TropicalFish.Pattern) o).ordinal(); // pattern
                        int var7 = ThreadLocalRandom.current().nextInt(15); // base colour
                        int var8 = ThreadLocalRandom.current().nextInt(15); // pattern colour
                        fish.setVariant(var5 | var6 << 8 | var7 << 16 | var8 << 24);
                    }
                    if (o instanceof AGE && EntityAgeable.class.isAssignableFrom(entityClass)) {
                        // adult or baby
                        EntityAgeable ageable = (EntityAgeable) entity;
                        ageable.setAgeRaw(((AGE) o).getAge());
                    }
                }
            }
            return entity;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            Bukkit.getLogger().log(Level.SEVERE, TARDISHelper.messagePrefix + "~TARDISDisguise~ " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private static String switchAndCapitalise(String s) {
        String[] split = s.split("_");
        return uppercaseFirst(split[1]) + uppercaseFirst(split[0]);
    }

    private static String capitalise(String s) {
        String[] split = s.split("_");
        return (split.length > 1) ? uppercaseFirst(split[0]) + uppercaseFirst(split[1]) : uppercaseFirst(split[0]);
    }

    private static String uppercaseFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public Object[] getOptions() {
        return options;
    }
}
