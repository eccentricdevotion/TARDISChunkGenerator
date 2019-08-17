package me.eccentric_nz.tardischunkgenerator.disguise;

import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collection;

public class TARDISDalekDisguiser {

    private final org.bukkit.entity.Entity dalek;
    private final Entity entity;

    public TARDISDalekDisguiser(org.bukkit.entity.Entity dalek) {
        this.dalek = dalek;
        entity = TARDISDalekDisguise.createDalekDisguise(this.dalek.getWorld());
        setEntityLocationAndId(entity, this.dalek.getLocation(), this.dalek);
    }

    public static void disguiseToPlayer(Player to, org.bukkit.World world) {
        Collection<Skeleton> daleks = world.getEntitiesByClass(Skeleton.class);
        NamespacedKey DALEK = new NamespacedKey(Bukkit.getServer().getPluginManager().getPlugin("TARDISWeepingAngels"), "dalek");
        daleks.forEach((d) -> {
            if (d.getPersistentDataContainer().has(DALEK, PersistentDataType.INTEGER)) {
                Entity mob = TARDISDalekDisguise.createDalekDisguise(world);
                if (mob != null) {
                    // set location
                    setEntityLocationAndId(mob, d.getLocation(), d);
                    PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(d.getEntityId());
                    PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving = new PacketPlayOutSpawnEntityLiving((EntityLiving) mob);
                    ((CraftPlayer) to).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
                    ((CraftPlayer) to).getHandle().playerConnection.sendPacket(packetPlayOutSpawnEntityLiving);
                }
            }
        });
    }

    public static void redisguise(org.bukkit.entity.Entity dalek, org.bukkit.World world) {
        Entity mob = TARDISDalekDisguise.createDalekDisguise(world);
        if (mob != null) {
            // set location
            setEntityLocationAndId(mob, dalek.getLocation(), dalek);
            if (!TARDISDisguiseTracker.DALEKS.contains(dalek.getUniqueId())) {
                TARDISDisguiseTracker.DALEKS.add(dalek.getUniqueId());
            }
            PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(dalek.getEntityId());
            PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving = new PacketPlayOutSpawnEntityLiving((EntityLiving) mob);
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p != dalek && dalek.getWorld() == p.getWorld()) {
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutSpawnEntityLiving);
                }
            }
        }
    }

    private static void setEntityLocationAndId(Entity disguise, Location location, org.bukkit.entity.Entity dalek) {
        disguise.e(dalek.getEntityId());
        disguise.setPosition(location.getX(), location.getY(), location.getZ());
        disguise.yaw = location.getYaw();
        disguise.pitch = location.getPitch();
        EntityInsentient insentient = (EntityInsentient) disguise;
        insentient.setNoAI(true);
        insentient.setPersistent();
    }

    public void disguiseToAll() {
        TARDISDisguiseTracker.DALEKS.add(dalek.getUniqueId());
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(dalek.getEntityId());
        PacketPlayOutSpawnEntityLiving packetPlayOutSpawnEntityLiving = new PacketPlayOutSpawnEntityLiving((EntityLiving) entity);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (dalek.getWorld() == p.getWorld()) {
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutEntityDestroy);
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutSpawnEntityLiving);
            }
        }
    }
}
