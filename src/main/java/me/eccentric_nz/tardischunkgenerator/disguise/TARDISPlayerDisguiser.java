package me.eccentric_nz.tardischunkgenerator.disguise;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import net.minecraft.server.v1_14_R1.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;

public class TARDISPlayerDisguiser {

    private final Player player;
    private final UUID uuid;

    public TARDISPlayerDisguiser(Player player, UUID uuid) {
        this.player = player;
        this.uuid = uuid;
        disguisePlayer();
    }

    public static void disguiseToPlayer(Player disguised, Player to) {
        to.hidePlayer(disguised);
        to.showPlayer(disguised);
    }

    public void disguisePlayer() {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        // set skin
        if (setSkin(entityPlayer.getProfile(), uuid) && !TARDISDisguiseTracker.DISGUISED_AS_PLAYER.contains(player.getUniqueId())) {
            TARDISDisguiseTracker.DISGUISED_AS_PLAYER.add(player.getUniqueId());
        }
    }

    private boolean setSkin(GameProfile profile, UUID uuid) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false", UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                String reply = new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
                String skin = reply.split("\"value\":\"")[1].split("\"")[0];
                String signature = reply.split("\"signature\":\"")[1].split("\"")[0];
                profile.getProperties().removeAll("textures");
                return profile.getProperties().put("textures", new Property("textures", skin, signature));
            } else {
                System.out.println("Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void disguiseToAll() {
        TARDISDisguiseTracker.DISGUISED_AS_PLAYER.add(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player && player.getWorld() == p.getWorld()) {
                p.hidePlayer(player);
                p.showPlayer(player);
            }
        }
    }
}
