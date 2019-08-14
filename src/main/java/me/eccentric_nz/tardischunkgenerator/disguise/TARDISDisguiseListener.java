package me.eccentric_nz.tardischunkgenerator.disguise;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class TARDISDisguiseListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        World world = event.getPlayer().getWorld();
        // show disguise to newly joined players
        TARDISDisguiser.disguiseToPlayer(uuid, world);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        // stop tracking the disguise
        if (TARDISDisguiseTracker.DISGUISED_IN_WORLD.containsKey(uuid)) {
            TARDISDisguiseTracker.DISGUISED_IN_WORLD.remove(uuid);
        }
    }
}
