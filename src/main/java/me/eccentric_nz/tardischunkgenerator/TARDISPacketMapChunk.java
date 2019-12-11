/*
 * Copyright (C) 2018 eccentric_nz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.eccentric_nz.tardischunkgenerator;

import net.minecraft.server.v1_15_R1.PacketPlayOutMapChunk;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_15_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author eccentric_nz
 */
public class TARDISPacketMapChunk {

    private final net.minecraft.server.v1_15_R1.Chunk chunk;

    /**
     * Creates a TARDISPacketMapChunk.
     *
     * @param world The chunk's world.
     * @param x     The chunk's X.
     * @param z     The chunk's Z.
     */
    public TARDISPacketMapChunk(World world, int x, int z) {
        this(world.getChunkAt(x, z));
    }

    /**
     * Creates a TARDISPacketMapChunk.
     *
     * @param chunk The chunk.
     */
    public TARDISPacketMapChunk(org.bukkit.Chunk chunk) {
        this.chunk = ((CraftChunk) chunk).getHandle();
    }

    /**
     * Refresh a chunk.
     *
     * @param chunk The chunk.
     */
    public static final void refreshChunk(org.bukkit.Chunk chunk) {
        refreshChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Wrapper for <code>world.refreshChunk(...)</code>
     *
     * @param world The world.
     * @param x     The chunk's X.
     * @param z     The chunk's Z.
     */
    public static final void refreshChunk(World world, int x, int z) {
        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        refreshChunk(world, x, z, players.toArray(new Player[players.size()]));
    }

    /**
     * Refresh a chunk for the selected players.
     *
     * @param world   The chunk's world.
     * @param x       The chunk's X.
     * @param z       The chunk's Z.
     * @param players The players.
     */
    public static final void refreshChunk(World world, int x, int z, Player... players) {
        TARDISPacketMapChunk packet = new TARDISPacketMapChunk(world.getChunkAt(x, z));
        for (Player player : players) {
            packet.send(player);
        }
        world.refreshChunk(x, z);
    }

    /**
     * Sends this packet to a player.
     * <br>You still need to refresh it manually with
     * <code>world.refreshChunk(...)</code>.
     *
     * @param player The player.
     */
    public final void send(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, 20));
    }
}
