package me.eccentric_nz.tardischunkgenerator;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_14_R1.util.CraftNamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TARDISDisguise {

    private final Player player;
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
                    str = "Horse" + WordUtils.capitalize(entityType.toString());
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
                entity.setCustomName(new ChatMessage(ChatColor.YELLOW + player.getName()));
                entity.setCustomNameVisible(true);
                entity.locX = location.getX();
                entity.locY = location.getY();
                entity.locZ = location.getZ();
                entity.yaw = location.getYaw();
                entity.pitch = location.getPitch();
                if (options != null) {
                    for (Object o : options) {
                        if (o instanceof DyeColor) {
                            // colour a sheep / wolf collar / cat collar / tropical fish?
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
