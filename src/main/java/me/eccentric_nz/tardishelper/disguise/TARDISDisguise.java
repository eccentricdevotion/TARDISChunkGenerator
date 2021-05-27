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

import me.eccentric_nz.tardishelper.TARDISHelperPlugin;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftNamespacedKey;
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

	public static Entity createMobDisguise(TARDISDisguise disguise, World w) {
		String str = switch (disguise.getEntityType()) {
			case ZOMBIE_HORSE, SKELETON_HORSE, ELDER_GUARDIAN, WITHER_SKELETON, TRADER_LLAMA -> switchAndCapitalise(disguise.getEntityType().toString());
			case WANDERING_TRADER -> "VillagerTrader";
			case HUSK -> "ZombieHusk";
			case STRAY -> "SkeletonStray";
			case PUFFERFISH -> "PufferFish";
			case ILLUSIONER -> "IllagerIllusioner";
			case GIANT -> "GiantZombie";
			case DONKEY, MULE -> "Horse" + capitalise(disguise.getEntityType().toString());
			default -> capitalise(disguise.getEntityType().toString());
		};
		try {
			Class<? extends Entity> entityClass = (Class<? extends Entity>) Class.forName("net.minecraft.server.v1_16_R3.Entity" + str);
			Constructor<? extends Entity> constructor = entityClass.getConstructor(EntityTypes.class, net.minecraft.server.v1_16_R3.World.class);
			EntityTypes<? extends Entity> type = IRegistry.ENTITY_TYPE.get(CraftNamespacedKey.toMinecraft(disguise.getEntityType().getKey()));
			net.minecraft.server.v1_16_R3.World world = ((CraftWorld) w).getHandle();
			Entity entity = constructor.newInstance(type, world);
			if (disguise.getOptions() != null) {
				for (Object o : disguise.getOptions()) {
					if (o instanceof DyeColor) {
						// colour a sheep / wolf collar
						switch (disguise.getEntityType()) {
							case SHEEP:
								EntitySheep sheep = (EntitySheep) entity;
								sheep.setColor(EnumColor.valueOf(o.toString()));
								break;
							case WOLF:
								EntityWolf wolf = (EntityWolf) entity;
								wolf.setTamed(true);
								wolf.setCollarColor(EnumColor.valueOf(o.toString()));
								break;
							default:
								break;
						}
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
							case FOX:
							case WOLF:
							case CAT:
								EntityTameableAnimal tameable = (EntityTameableAnimal) entity;
								tameable.setTamed((Boolean) o);
								break;
							case DONKEY:
							case MULE:
								EntityHorseChestedAbstract chesty = (EntityHorseChestedAbstract) entity;
								chesty.setCarryingChest((Boolean) o);
								break;
							case SHEEP:
								if ((Boolean) o) {
									entity.setCustomName(new ChatMessage("jeb_"));
									entity.setCustomNameVisible(true);
								}
								break;
							case PIG:
								EntityPig pig = (EntityPig) entity;
								pig.saddle(null);
								break;
							case ENDERMAN:
								if ((Boolean) o) {
									EntityEnderman enderman = (EntityEnderman) entity;
									IBlockData block = Blocks.PURPUR_BLOCK.getBlockData();
									enderman.setCarried(block);
								}
								break;
							case CREEPER:
								EntityCreeper creeper = (EntityCreeper) entity;
								creeper.setPowered((Boolean) o);
								break;
							case BAT:
								EntityBat bat = (EntityBat) entity;
								bat.setAsleep((Boolean) o);
								break;
							case SNOWMAN:
								EntitySnowman snowman = (EntitySnowman) entity;
								snowman.setHasPumpkin(!(Boolean) o);
								break;
							case PILLAGER:
								if ((Boolean) o) {
									EntityPillager pillager = (EntityPillager) entity;
									ItemStack crossbow = CraftItemStack.asNMSCopy(new org.bukkit.inventory.ItemStack(org.bukkit.Material.CROSSBOW));
									pillager.setSlot(EnumItemSlot.MAINHAND, crossbow);
									pillager.a(pillager, 1.0f);
								}
								break;
							case LLAMA:
								EntityLlama llama = (EntityLlama) entity;
								org.bukkit.inventory.ItemStack bukkitItemStack = new org.bukkit.inventory.ItemStack(CARPET.values()[ThreadLocalRandom.current().nextInt(16)].getCarpet());
								ItemStack nmsItemStack = CraftItemStack.asNMSCopy(bukkitItemStack);
								llama.inventoryChest.setItem(1, nmsItemStack);
							default:
								break;
						}
					}
					if (o instanceof Integer) {
						// magma cube and slime size / pufferfish state
						switch (disguise.getEntityType()) {
							case MAGMA_CUBE:
								EntityMagmaCube magma = (EntityMagmaCube) entity;
								magma.setSize((Integer) o, false);
								break;
							case SLIME:
								EntitySlime slime = (EntitySlime) entity;
								slime.setSize((Integer) o, false);
								break;
							case PUFFERFISH:
								EntityPufferFish puffer = (EntityPufferFish) entity;
								puffer.setPuffState((Integer) o);
								break;
							default:
								break;
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
			Bukkit.getLogger().log(Level.SEVERE, TARDISHelperPlugin.messagePrefix + "~TARDISDisguise~ " + e.getMessage());
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
