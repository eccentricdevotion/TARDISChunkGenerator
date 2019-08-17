package me.eccentric_nz.tardischunkgenerator.disguise;

import me.eccentric_nz.tardischunkgenerator.TARDISHelper;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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
                TARDISDalekDisguiser.disguiseToPlayer(player, world);
            }
        }, 5L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // show disguise to newly joined players
        disguiseToPlayer(event.getPlayer(), event.getPlayer().getWorld());
        TARDISPacketListener.injectPlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        // show disguise to newly joined players
        disguiseToPlayer(event.getPlayer(), event.getPlayer().getWorld());
    }

    private void disguiseToPlayer(Player player, World world) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            TARDISDisguiser.disguiseToPlayer(player, world);
            TARDISDalekDisguiser.disguiseToPlayer(player, world);
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
        TARDISPacketListener.removePlayer(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDalekClick(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity != null && entity.getType().equals(EntityType.SKELETON)) {
            PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
            NamespacedKey DALEK = new NamespacedKey(plugin.getServer().getPluginManager().getPlugin("TARDISWeepingAngels"), "dalek");
            if (dataContainer.has(DALEK, PersistentDataType.INTEGER)) {
                // is it disguised?
                if (TARDISDisguiseTracker.DALEKS.contains(entity.getUniqueId())) {
                    TARDISDalekDisguiser.redisguise(entity, entity.getWorld());
                }
            }
        }
    }
}
