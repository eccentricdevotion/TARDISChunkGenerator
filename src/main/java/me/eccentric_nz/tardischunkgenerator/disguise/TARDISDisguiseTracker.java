package me.eccentric_nz.tardischunkgenerator.disguise;

import com.mojang.authlib.properties.PropertyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TARDISDisguiseTracker {

    public static final List<UUID> DISGUISED_AS_PLAYER = new ArrayList<>();
    public static final HashMap<UUID, TARDISDisguise> DISGUISED_AS_MOB = new HashMap<>();
    public static final HashMap<UUID, ProfileData> ARCHED = new HashMap<>();

    public static class ProfileData {

        PropertyMap properties;
        String name;

        public ProfileData(PropertyMap properties, String name) {
            this.properties = properties;
            this.name = name;
        }

        public PropertyMap getProperties() {
            return properties;
        }

        public String getName() {
            return name;
        }
    }
}
