package me.eccentric_nz.tardischunkgenerator.disguise;

import org.bukkit.World;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class TARDISDisguise {

    private final World world;
    private final EntityType entityType;
    private final UUID uuid;
    private final Object[] options;

    public TARDISDisguise(World world, EntityType entityType, UUID uuid, Object[] options) {
        this.world = world;
        this.entityType = entityType;
        this.uuid = uuid;
        this.options = options;
    }

    public World getWorld() {
        return world;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Object[] getOptions() {
        return options;
    }
}
