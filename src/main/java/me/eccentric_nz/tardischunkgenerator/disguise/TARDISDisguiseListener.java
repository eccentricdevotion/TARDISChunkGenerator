package me.eccentric_nz.tardischunkgenerator.disguise;

import me.eccentric_nz.tardischunkgenerator.TARDISHelper;
import org.bukkit.Bukkit;
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

    private final TARDISHelper plugin;

    public TARDISDisguiseListener(TARDISHelper plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            // redisguise disguised players
            if (TARDISDisguiseTracker.DISGUISED_AS_MOB.containsKey(player.getUniqueId())) {
                TARDISDisguiser.redisguise(player, world);
            } else {
                // show other disguises to player
                TARDISDisguiser.disguiseToPlayer(player, world);
            }
        }, 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        World world = event.getPlayer().getWorld();
        // show disguise to newly joined players
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            TARDISDisguiser.disguiseToPlayer(event.getPlayer(), world);
        }, 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        // stop tracking the disguise
        if (TARDISDisguiseTracker.DISGUISED_AS_MOB.containsKey(uuid)) {
            TARDISDisguiseTracker.DISGUISED_AS_MOB.remove(uuid);
        }
        if (TARDISDisguiseTracker.DISGUISED_AS_PLAYER.contains(uuid)) {
            TARDISDisguiseTracker.DISGUISED_AS_PLAYER.remove(uuid);
        }
    }
}
