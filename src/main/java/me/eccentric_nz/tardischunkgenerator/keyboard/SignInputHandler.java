package me.eccentric_nz.tardischunkgenerator.keyboard;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import me.eccentric_nz.tardischunkgenerator.TARDISHelper;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

public class SignInputHandler {

    private static Field channelField;

    static {
        for (Field field : NetworkManager.class.getDeclaredFields()) {
            if (field.getType().isAssignableFrom(Channel.class)) {
                channelField = field;
                break;
            }
        }
    }

    public static void injectNetty(Player player, TARDISHelper plugin) {
        try {
            Channel channel = (Channel) channelField.get(((CraftPlayer) player).getHandle().b.a); // b = playerConnection, a = networkManager
            if (channel != null) {
                channel.pipeline().addAfter("decoder", "update_sign", new MessageToMessageDecoder<Packet>() {

                    @Override
                    protected void decode(ChannelHandlerContext chc, Packet packet, List<Object> out) {
                        if (packet instanceof PacketPlayInUpdateSign usePacket) {

                            Bukkit.getScheduler().runTask(plugin, () -> Bukkit.getPluginManager().callEvent(new PlayerInputEvent(usePacket, player)));
                        }
                        out.add(packet);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void ejectNetty(Player player) {
        try {
            Channel channel = (Channel) channelField.get(((CraftPlayer) player).getHandle().b.a); // b = playerConnection, a = networkManager
            if (channel != null) {
                if (channel.pipeline().get("update_sign") != null) {
                    channel.pipeline().remove("update_sign");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
