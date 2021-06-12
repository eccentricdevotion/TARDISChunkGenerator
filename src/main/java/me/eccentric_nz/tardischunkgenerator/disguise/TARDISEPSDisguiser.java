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

import net.minecraft.network.protocol.game.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumItemSlot;
import net.minecraft.world.item.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class TARDISEPSDisguiser {

    private final Player player;
    private final Location location;
    private EntityPlayer npc;

    public TARDISEPSDisguiser(Player player, Location location) {
        this.player = player;
        this.location = location;
        disguiseStand();
    }

    public static void disguiseToPlayer(Player player, World world) {
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();
        for (Map.Entry<Integer, UUID> map : TARDISDisguiseTracker.DISGUISED_NPCS.entrySet()) {
            Entity stand = nmsWorld.getEntity(map.getKey());
            if (stand != null && stand.getWorld() == world) {
                EntityPlayer entityPlayer = ((CraftPlayer) Bukkit.getOfflinePlayer(map.getValue())).getHandle();
                EntityPlayer npc = new EntityPlayer(server, nmsWorld, entityPlayer.getProfile());
                // set location
                setEntityLocation(npc, new Location(world, stand.locX(), stand.locY(), stand.locZ()));
                // send packets
                PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc); // a = ADD_PLAYER
                PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(npc);
                PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotation(npc, (byte) npc.getYRot());
                PacketPlayOutEntity.PacketPlayOutEntityLook packetPlayOutEntityLook = new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getId(), (byte) npc.getYRot(), (byte) npc.getXRot(), true);
                PlayerConnection connection = ((CraftPlayer) player).getHandle().b; // b = playerConnection
                connection.sendPacket(packetPlayOutPlayerInfo);
                connection.sendPacket(packetPlayOutNamedEntitySpawn);
                connection.sendPacket(packetPlayOutEntityHeadRotation);
                connection.sendPacket(packetPlayOutEntityLook);
            }
        }
    }

    private static float fixYaw(float yaw) {
        return yaw * 256.0f / 360.0f;
    }

    private static void setEntityLocation(Entity entity, Location location) {
        entity.setPosition(location.getX(), location.getY(), location.getZ());
        float fixed = fixYaw(location.getYaw());
        entity.setHeadRotation(fixed);
        entity.h(fixed);
        entity.setYRot(fixed);
        entity.setXRot(location.getPitch());
    }

    public static void removeNPC(int id, World world) {
        TARDISDisguiseTracker.DISGUISED_NPCS.remove(id);
        PacketPlayOutEntityDestroy packetPlayOutEntityDestroy = new PacketPlayOutEntityDestroy(id);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (world == p.getWorld()) {
                PlayerConnection connection = ((CraftPlayer) p).getHandle().b; // b = playerConnection
                connection.sendPacket(packetPlayOutEntityDestroy);
            }
        }
    }

    public void disguiseStand() {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        // set skin
        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        npc = new EntityPlayer(server, world, entityPlayer.getProfile());
        // set location
        setEntityLocation(npc, location);
        // get Player equipment
        ItemStack feet = CraftItemStack.asNMSCopy(player.getInventory().getBoots());
        ItemStack legs = CraftItemStack.asNMSCopy(player.getInventory().getLeggings());
        ItemStack chest = CraftItemStack.asNMSCopy(player.getInventory().getChestplate());
        ItemStack head = CraftItemStack.asNMSCopy(player.getInventory().getHelmet());
        ItemStack mainHand = CraftItemStack.asNMSCopy(player.getInventory().getItemInMainHand());
        ItemStack offHand = CraftItemStack.asNMSCopy(player.getInventory().getItemInOffHand());
        // set NPC equipment
        npc.setSlot(EnumItemSlot.c, feet);
        npc.setSlot(EnumItemSlot.d, legs);
        npc.setSlot(EnumItemSlot.e, chest);
        npc.setSlot(EnumItemSlot.f, head);
        npc.setSlot(EnumItemSlot.a, mainHand);
        npc.setSlot(EnumItemSlot.b, offHand);
    }

    public int showToAll() {
        TARDISDisguiseTracker.DISGUISED_NPCS.put(npc.getId(), player.getUniqueId());
        PacketPlayOutPlayerInfo packetPlayOutPlayerInfo = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.a, npc); // a = ADD_PLAYER
        PacketPlayOutNamedEntitySpawn packetPlayOutNamedEntitySpawn = new PacketPlayOutNamedEntitySpawn(npc);
        PacketPlayOutEntityHeadRotation packetPlayOutEntityHeadRotation = new PacketPlayOutEntityHeadRotation(npc, (byte) npc.getYRot());
        PacketPlayOutEntity.PacketPlayOutEntityLook packetPlayOutEntityLook = new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getId(), (byte) npc.getYRot(), (byte) npc.getXRot(), true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld() == location.getWorld()) {
                PlayerConnection connection = ((CraftPlayer) p).getHandle().b; // b = playerConnection
                connection.sendPacket(packetPlayOutPlayerInfo);
                connection.sendPacket(packetPlayOutNamedEntitySpawn);
                connection.sendPacket(packetPlayOutEntityHeadRotation);
                connection.sendPacket(packetPlayOutEntityLook);
            }
        }
        return npc.getId();
    }
}
