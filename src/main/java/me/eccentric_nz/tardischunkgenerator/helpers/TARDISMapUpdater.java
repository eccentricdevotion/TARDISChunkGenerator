package me.eccentric_nz.tardischunkgenerator.helpers;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.map.CraftMapView;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;
import java.util.Objects;
import java.util.UUID;

public final class TARDISMapUpdater extends EntityHuman {

	public static final UUID ID = UUID.randomUUID();
	public static final String NAME = "_____MapUpdater_____";

	public TARDISMapUpdater(World world, int x, int z) {
		super(((CraftWorld) world).getHandle(), new BlockPosition(x, 64, z), 1.0f, new GameProfile(ID, NAME));
	}

	@Override
	public boolean isSpectator() {
		return false;
	}

	@Override
	public boolean isCreative() {
		return false;
	}

	public void update(MapView mapView) {
		if (((CraftWorld) Objects.requireNonNull(mapView.getWorld())).getHandle() != world) {
			throw new IllegalArgumentException("world of mapView cannot be different");
		}
		try {
			Field field = CraftMapView.class.getDeclaredField("worldMap");
			field.setAccessible(true);
			WorldMap worldMap = (WorldMap) field.get(mapView);
			int size = 128 << worldMap.scale;
			for (int x = worldMap.centerX - size / 2; x <= worldMap.centerX + size / 2; x += 64) {
				for (int z = worldMap.centerZ - size / 2; z <= worldMap.centerZ + size / 2; z += 64) {
					setPositionRaw(x, 0.0, z);
					((ItemWorldMap) Items.FILLED_MAP).a(world, this, worldMap);
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
