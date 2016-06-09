/*
 *  Copyright 2016 eccentric_nz.
 */
package me.eccentric_nz.tardischunkgenerator;

import java.util.Collection;
import net.minecraft.server.v1_10_R1.PacketPlayOutMapChunk;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_10_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 *
 * @author eccentric_nz
 */
public class TARDISPacketMapChunk {

    private final net.minecraft.server.v1_10_R1.Chunk chunk;

    /**
     * Creates a TARDISPacketMapChunk.
     *
     * @param world The chunk's world.
     * @param x The chunk's X.
     * @param z The chunk's Z.
     */
    public TARDISPacketMapChunk(final World world, final int x, final int z) {
        this(world.getChunkAt(x, z));
    }

    /**
     * Creates a TARDISPacketMapChunk.
     *
     * @param chunk The chunk.
     */
    public TARDISPacketMapChunk(final org.bukkit.Chunk chunk) {
        this.chunk = ((CraftChunk) chunk).getHandle();
    }

    /**
     * Sends this packet to a player.
     * <br>You still need to refresh it manually with
     * <code>world.refreshChunk(...)</code>.
     *
     * @param player The player.
     */
    public final void send(final Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, 20));
    }

    /**
     * Refresh a chunk.
     *
     * @param chunk The chunk.
     */
    public static final void refreshChunk(final org.bukkit.Chunk chunk) {
        refreshChunk(chunk.getWorld(), chunk.getX(), chunk.getZ());
    }

    /**
     * Wrapper for <code>world.refreshChunk(...)</code>
     *
     * @param world The world.
     * @param x The chunk's X.
     * @param z The chunk's Z.
     */
    public static final void refreshChunk(final World world, final int x, final int z) {
        final Collection<? extends Player> players = Bukkit.getOnlinePlayers();
        refreshChunk(world, x, z, players.toArray(new Player[players.size()]));
    }

    /**
     * Refresh a chunk for the selected players.
     *
     * @param world The chunk's world.
     * @param x The chunk's X.
     * @param z The chunk's Z.
     * @param players The players.
     */
    @SuppressWarnings("deprecation")
    public static final void refreshChunk(final World world, final int x, final int z, final Player... players) {
        final TARDISPacketMapChunk packet = new TARDISPacketMapChunk(world.getChunkAt(x, z));
        for (final Player player : players) {
            packet.send(player);
        }
        world.refreshChunk(x, z);
    }
}
