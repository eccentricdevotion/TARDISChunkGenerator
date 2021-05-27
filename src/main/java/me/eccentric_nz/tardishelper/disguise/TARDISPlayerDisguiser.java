/*
 * Copyright (C) 2020 eccentric_nz
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
package me.eccentric_nz.tardishelper.disguise;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.util.UUIDTypeAdapter;
import me.eccentric_nz.tardishelper.TARDISHelperPlugin;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.UUID;
import java.util.logging.Level;

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
				Bukkit.getLogger().log(Level.INFO, TARDISHelperPlugin.messagePrefix + "Connection could not be opened (Response code " + connection.getResponseCode() + ", " + connection.getResponseMessage() + ")");
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
