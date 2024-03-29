/*
 * Copyright (C) 2023 eccentric_nz
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
package me.eccentric_nz.tardischunkgenerator.helpers;

import io.netty.channel.*;
import java.lang.reflect.Field;
import java.util.UUID;
import java.util.logging.Level;
import me.eccentric_nz.tardischunkgenerator.TARDISHelper;
import me.eccentric_nz.tardischunkgenerator.disguise.TARDISDisguiseTracker;
import me.eccentric_nz.tardischunkgenerator.disguise.TARDISDisguiser;
import net.minecraft.core.Holder;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.ticks.LevelChunkTicks;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class TARDISPacketListener {

    private static Field connectionField;

    public static void removePlayer(Player player) {
        Connection connection = getConnection(((CraftPlayer) player).getHandle().connection);
        Channel channel = connection.channel;
        channel.eventLoop().submit(() -> {
            channel.pipeline().remove(player.getName());
            return null;
        });
    }

    private static Connection getConnection(final ServerGamePacketListenerImpl playerConnection) {
        try {
            if (connectionField == null) {
                connectionField = ServerGamePacketListenerImpl.class.getDeclaredField("h");
                connectionField.setAccessible(true);
            }
            return (Connection) connectionField.get(playerConnection);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void injectPlayer(Player player) {
        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {

            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {
                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise channelPromise) throws Exception {
                if (packet instanceof ClientboundAddEntityPacket namedEntitySpawn && !TARDISHelper.tardisHelper.getServer().getPluginManager().isPluginEnabled("LibsDisguises")) {
                    UUID uuid = namedEntitySpawn.getUUID();
                    if (TARDISDisguiseTracker.DISGUISED_AS_MOB.containsKey(uuid)) {
                        Entity entity = Bukkit.getEntity(uuid);
                        if (entity.getType().equals(EntityType.PLAYER)) {
                            Player player = (Player) entity;
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(TARDISHelper.getTardisHelper(), () -> {
                                TARDISDisguiser.redisguise(player, entity.getWorld());
                            }, 5L);
                        }
                    }
                }
                if (TARDISHelper.colourSkies && packet instanceof ClientboundLevelChunkWithLightPacket chunkPacket) {
                    String world = player.getWorld().getName();
                    if (world.equals("gallifrey") || world.equals("skaro")) {
                        LevelChunk levelChunk = cloneChunk((LevelChunk) ((CraftChunk) player.getWorld().getChunkAt(chunkPacket.getX(), chunkPacket.getZ())).getHandle(ChunkStatus.BIOMES));
                        String key = (world.endsWith("gallifrey")) ? "gallifrey_badlands" : "skaro_desert";
                        Biome biome = TARDISHelper.biomeMap.get(key);
                        if (biome != null) {
                            for (LevelChunkSection section : levelChunk.getSections()) {
                                for (int x = 0; x < 4; ++x) {
                                    for (int z = 0; z < 4; ++z) {
                                        for (int y = 0; y < 4; ++y) {
                                            section.setBiome(x, y, z, Holder.direct(biome));
                                        }
                                    }
                                }
                            }
                            packet = new ClientboundLevelChunkWithLightPacket(levelChunk, levelChunk.getLevel().getLightEngine(), null, null, true);
                        } else {
                            Bukkit.getLogger().log(Level.INFO, "biome was null");
                        }
                    }
                }
                super.write(channelHandlerContext, packet, channelPromise);
            }
        };
        Connection connection = getConnection(((CraftPlayer) player).getHandle().connection);
        ChannelPipeline pipeline = connection.channel.pipeline();
        pipeline.addBefore("packet_handler", player.getName() + "_tcg", channelDuplexHandler);
    }

    private static LevelChunk cloneChunk(LevelChunk chunk) {
        return new LevelChunk(
                chunk.getLevel(),
                chunk.getPos(),
                chunk.getUpgradeData(),
                (LevelChunkTicks<Block>) chunk.getBlockTicks(),
                (LevelChunkTicks<Fluid>) chunk.getFluidTicks(),
                chunk.getInhabitedTime(),
                chunk.getSections(),
                null,
                chunk.getBlendingData()
        );
    }
}
