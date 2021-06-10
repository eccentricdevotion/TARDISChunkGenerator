package me.eccentric_nz.tardischunkgenerator.keyboard;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PlayerInputEvent extends PlayerEvent {

    public static HandlerList handlerList = new HandlerList();

    public PlayerInputEvent(PacketPlayInUpdateSign packet, Player p) {
        super(p);
        // This is were your code goes
        updateSign(p, packet);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public void updateSign(Player p, PacketPlayInUpdateSign packet) {
        EntityPlayer player = ((CraftPlayer) Objects.requireNonNull(p.getPlayer())).getHandle();
        player.resetIdleTimer();
        WorldServer worldserver = player.getWorldServer();
        BlockPosition blockposition = packet.b();
        if (worldserver.isLoaded(blockposition)) {
            IBlockData iblockdata = worldserver.getType(blockposition);
            TileEntity tileentity = worldserver.getTileEntity(blockposition);
            if (!(tileentity instanceof TileEntitySign tileentitysign)) {
                return;
            }
            tileentitysign.isEditable = true;
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
