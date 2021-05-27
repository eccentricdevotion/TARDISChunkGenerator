/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020 Qveshn
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package me.eccentric_nz.tardishelper.light;

import me.eccentric_nz.tardishelper.TARDISHelperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class Light {

	// To synchronize nms create/delete light methods to avoid conflicts in multi-threaded calls. Got a better idea?
	private static final Object lock = new Object();
	private static final NMSHandler nmsHandler = new NMSHandler();

	public static boolean createLight(World world, int x, int y, int z, LightType lightType, int lightlevel, boolean async) {
		CreateLightEvent event = new CreateLightEvent(world, x, y, z, lightType, lightlevel, async);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			Runnable request = () -> {
				synchronized (lock) {
					nmsHandler.createLight(event.getWorld(), event.getX(), event.getY(), event.getZ(), event.getLightType(), event.getLightLevel());
				}
			};
			if (event.isAsync()) {
				TARDISHelperPlugin.machine.addToQueue(request);
			} else {
				request.run();
			}
			return true;
		}
		return false;
	}

	public static boolean deleteLight(World world, int x, int y, int z, LightType lightType, boolean async) {
		DeleteLightEvent event = new DeleteLightEvent(world, x, y, z, lightType, async);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			Runnable request = () -> nmsHandler.deleteLight(event.getWorld(), event.getX(), event.getY(), event.getZ(), event.getLightType());
			if (event.isAsync()) {
				TARDISHelperPlugin.machine.addToQueue(request);
			} else {
				request.run();
			}
			return true;
		}
		return false;
	}

	public static List<ChunkInfo> collectChunks(World world, int x, int y, int z, LightType lightType, int lightLevel) {
		return nmsHandler.collectChunks(world, x, y, z, lightType, lightLevel);
	}

	public static boolean updateChunk(ChunkInfo info, LightType lightType, Collection<? extends Player> players) {
		UpdateChunkEvent event = new UpdateChunkEvent(info, lightType);
		Bukkit.getPluginManager().callEvent(event);
		if (!event.isCancelled()) {
			TARDISHelperPlugin.machine.addChunkToUpdate(info, lightType, players);
			return true;
		}
		return false;
	}
}
