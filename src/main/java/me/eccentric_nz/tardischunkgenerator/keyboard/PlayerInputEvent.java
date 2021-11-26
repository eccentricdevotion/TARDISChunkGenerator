package me.eccentric_nz.tardischunkgenerator.keyboard;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerEvent;

public class PlayerInputEvent extends PlayerEvent {

    public static HandlerList handlerList = new HandlerList();

    public PlayerInputEvent(ServerboundSignUpdatePacket packet, Player p) {
        super(p);
        // This is were your code goes
        updateSign(p, packet);
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public void updateSign(Player p, ServerboundSignUpdatePacket packet) {
        ServerPlayer player = ((CraftPlayer) p.getPlayer()).getHandle();
        player.reset();
        ServerLevel worldserver = player.getLevel();
        BlockPos blockposition = packet.getPos();
        if (worldserver.isLoaded(blockposition)) {
            BlockState iblockdata = worldserver.getBlockState(blockposition);
            BlockEntity tileentity = worldserver.getBlockEntity(blockposition);
            if (tileentity instanceof SignBlockEntity tileentitysign) {
                tileentitysign.setEditable(true); // f = isEditable
                String[] lines = packet.getLines();
                for (int i = 0; i < lines.length; ++i) {
                    tileentitysign.setMessage(i, new TextComponent(lines[i]));
                }
                tileentitysign.setChanged();
                worldserver.sendBlockUpdated(blockposition, iblockdata, iblockdata, 3);
                SignChangeEvent event = new SignChangeEvent(p.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()), p, lines);
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }
}
