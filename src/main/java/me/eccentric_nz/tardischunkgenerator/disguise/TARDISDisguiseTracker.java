package me.eccentric_nz.tardischunkgenerator.disguise;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TARDISDisguiseTracker {

    public static final List<UUID> DISGUISED_AS_PLAYER = new ArrayList<>();
    public static final HashMap<UUID, TARDISDisguise> DISGUISED_IN_WORLD = new HashMap<>();
}
