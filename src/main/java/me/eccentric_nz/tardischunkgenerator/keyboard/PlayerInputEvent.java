package me.eccentric_nz.tardischunkgenerator.keyboard;

import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.ChatComponentText;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.level.block.entity.TileEntity;
import net.minecraft.world.level.block.entity.TileEntitySign;
import net.minecraft.world.level.block.state.IBlockData;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerEvent;

public class PlayerInputEvent extends PlayerEvent {

    public static HandlerList handlerList = new HandlerList();

    public PlayerInputEvent(PacketPlayInUpdateSign packet, Player p) {
        super(p);
        // This is were your code goes
        updateSign(p, packet);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public void updateSign(Player p, PacketPlayInUpdateSign packet) {
        EntityPlayer player = ((CraftPlayer) p.getPlayer()).getHandle();
        player.resetIdleTimer();
        WorldServer worldserver = player.getWorldServer();
        BlockPosition blockposition = packet.b();
        if (worldserver.isLoaded(blockposition)) {
            IBlockData iblockdata = worldserver.getType(blockposition);
            TileEntity tileentity = worldserver.getTileEntity(blockposition);
            if (!(tileentity instanceof TileEntitySign)) {
                return;
            }
            TileEntitySign tileentitysign = (TileEntitySign) tileentity;
            tileentitysign.f = true; // f = isEditable
            String[] lines = packet.c();
            for (int i = 0; i < lines.length; ++i) {
                tileentitysign.a(i, new ChatComponentText(lines[i]));
            }
            tileentitysign.update();
            worldserver.notify(blockposition, iblockdata, iblockdata, 3);
            SignChangeEvent event = new SignChangeEvent(p.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), p, lines);
            Bukkit.getPluginManager().callEvent(event);
        }
    }
}
