package me.eccentric_nz.tardischunkgenerator;

import net.minecraft.server.v1_14_R1.Entity;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftNamespacedKey;
import org.bukkit.entity.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

public class TARDISDisguise {

    private final Player player;
    private final boolean nameVisible = false;
    private Object[] options;
    private EntityType entityType;
    private Class<?> entityClass;
    private Entity entity;

    public TARDISDisguise(Player player) {
        this.player = player;
    }

    public TARDISDisguise(EntityType entityType, Player player) {
        this.entityType = entityType;
        this.player = player;
        options = null;
        createDisguise();
    }

    public TARDISDisguise(EntityType entityType, Player player, Object[] options) {
        this.entityType = entityType;
        this.player = player;
        this.options = options;
        createDisguise();
    }

    private void createDisguise() {
        if (entityType != null) {
            Location location = player.getLocation();
            String str;
            switch (entityType) {
                case ZOMBIE_HORSE:
                case SKELETON_HORSE:
                case ELDER_GUARDIAN:
                case WITHER_SKELETON:
                    str = switchAndCapitalise(entityType.toString());
                    break;
                case HUSK:
                    str = "ZombieHusk";
                    break;
                case STRAY:
                    str = "SkeletonStray";
                    break;
                case PUFFERFISH:
                    str = "PufferFish";
                    break;
                case ILLUSIONER:
                    str = "IllagerIllusioner";
                    break;
                case GIANT:
                    str = "GiantZombie";
                    break;
                case DONKEY:
                case MULE:
                    str = "Horse" + capitalise(entityType.toString());
                    break;
                case PLAYER:
                    str = "Human";
                    break;
                default:
                    str = capitalise(entityType.toString());
                    break;
            }
            try {
                entityClass = Class.forName("net.minecraft.server.v1_14_R1.Entity" + str);
                Constructor constructor = entityClass.getConstructor(EntityTypes.class, World.class);
                EntityTypes type = IRegistry.ENTITY_TYPE.get(CraftNamespacedKey.toMinecraft(entityType.getKey()));
                World world = ((CraftWorld) player.getWorld()).getHandle();
                entity = (Entity) constructor.newInstance(type, world);
                entity.setPosition(location.getX(), location.getY(), location.getZ());
                entity.e(player.getEntityId());
                if (nameVisible) {
                    entity.setCustomName(new ChatMessage(player.getName()));
                    entity.setCustomNameVisible(true);
                }
                entity.yaw = location.getYaw();
                entity.pitch = location.getPitch();
                EntityInsentient insentient = (EntityInsentient) entity;
                insentient.setNoAI(true);
                if (options != null) {
                    for (Object o : options) {
                        if (o instanceof DyeColor) {
                            // colour a sheep / wolf collar
                            switch (entityType) {
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
                            }
                        }
                        if (o instanceof Rabbit.Type && entityType.equals(EntityType.RABBIT)) {
                            EntityRabbit rabbit = (EntityRabbit) entity;
                            rabbit.setRabbitType(((Rabbit.Type) o).ordinal());
                        }
                        if (o instanceof GENE && entityType.equals(EntityType.PANDA)) {
                            EntityPanda panda = (EntityPanda) entity;
                            EntityPanda.Gene gene = ((GENE) o).getNmsGene();
                            panda.setMainGene(gene);
                            panda.setHiddenGene(gene);
                        }
                        if (o instanceof PROFESSION) {
                            if (entityType.equals(EntityType.VILLAGER)) {
                                EntityVillager villager = (EntityVillager) entity;
                                villager.setVillagerData(villager.getVillagerData().withProfession(((PROFESSION) o).getNmsProfession()));
                            } else if (entityType.equals(EntityType.ZOMBIE_VILLAGER)) {
                                EntityZombieVillager zombie = (EntityZombieVillager) entity;
                                zombie.setVillagerData(zombie.getVillagerData().withProfession(((PROFESSION) o).getNmsProfession()));
                            }
                        }
                        if (o instanceof Parrot.Variant && entityType.equals(EntityType.PARROT)) {
                            EntityParrot parrot = (EntityParrot) entity;
                            parrot.setVariant(((Parrot.Variant) o).ordinal());
                        }
                        if (o instanceof MUSHROOM_COW && entityType.equals(EntityType.MUSHROOM_COW)) {
                            EntityMushroomCow cow = (EntityMushroomCow) entity;
                            cow.setVariant(((MUSHROOM_COW) o).getNmsType());
                        }
                        if (o instanceof Cat.Type && entityType.equals(EntityType.CAT)) {
                            EntityCat cat = (EntityCat) entity;
                            cat.setCatType(((Cat.Type) o).ordinal());
                        }
                        if (o instanceof FOX && entityType.equals(EntityType.FOX)) {
                            EntityFox fox = (EntityFox) entity;
                            fox.setFoxType(((FOX) o).getNmsType());
                        }
                        if (o instanceof Horse.Color && entityType.equals(EntityType.HORSE)) {
                            EntityHorse horse = (EntityHorse) entity;
                            horse.setVariant(((Horse.Color) o).ordinal());
                        }
                        if (o instanceof Llama.Color && entityType.equals(EntityType.LLAMA)) {
                            EntityLlama llama = (EntityLlama) entity;
                            llama.setVariant(((Llama.Color) o).ordinal());
                        }
                        if (o instanceof CARPET && entityType.equals(EntityType.LLAMA)) {
                            EntityLlama llama = (EntityLlama) entity;
                            org.bukkit.inventory.ItemStack bukkitItemStack = new org.bukkit.inventory.ItemStack(((CARPET) o).getCarpet());
                            ItemStack nmsitemStack = CraftItemStack.asNMSCopy(bukkitItemStack);
                            llama.inventoryChest.setItem(1, nmsitemStack);
                        }
                        if (o instanceof Boolean) {
                            // tamed fox, wolf, cat / decorated llama, chest carrying mule or donkey / trusting ocelot
                            // rainbow sheep / saddled pig / block carrying enderman / powered creeper / hanging bat / blazing blaze
                            switch (entityType) {
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
                                    pig.setSaddle((Boolean) o);
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
                                default:
                            }
                        }
                        if (o instanceof Integer) {
                            // magma cube and slime size / pufferfish state
                            switch (entityType) {
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
                            }
                        }
                        if (o instanceof TropicalFish.Pattern && entityType.equals(EntityType.TROPICAL_FISH)) {
                            EntityTropicalFish fish = (EntityTropicalFish) entity;
                            Random random = new Random();
                            int var5 = random.nextInt(2); // shape
                            int var6 = ((TropicalFish.Pattern) o).ordinal(); // pattern
                            int var7 = random.nextInt(15); // base colour
                            int var8 = random.nextInt(15); // pattern colour
                            fish.setVariant(var5 | var6 << 8 | var7 << 16 | var8 << 24);
                        }
                        if (o instanceof AGE && EntityAgeable.class.isAssignableFrom(entityClass)) {
                            // adult or baby
                            EntityAgeable ageable = (EntityAgeable) entity;
                            ageable.setAgeRaw(((AGE) o).getAge());
                        }
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                System.err.println("[TARDISDisguise] " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String switchAndCapitalise(String s) {
        String[] split = s.split("_");
        return uppercaseFirst(split[1]) + uppercaseFirst(split[0]);
    }

    private String capitalise(String s) {
        String[] split = s.split("_");
        return (split.length > 1) ? uppercaseFirst(split[0]) + uppercaseFirst(split[1]) : uppercaseFirst(split[0]);
    }

    private String uppercaseFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    public void removeDisguise() {
        entityType = null;
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(player.getEntityId());
        PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(((CraftPlayer) player).getHandle());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player) {
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutNamedEntitySpawn);
            }
        }
    }

    public void changeDisguise(EntityType entityType) {
        removeDisguise();
        this.entityType = entityType;
        TARDISDisguise dis = new TARDISDisguise(entityType, player, null);
        dis.disguiseToAll();
    }

    public void disguiseToAll() {
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(player.getEntityId());
        PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving = new PacketPlayOutSpawnEntityLiving((EntityLiving) entity);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player) {
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutSpawnEntityLiving);
            }
        }
    }
}
