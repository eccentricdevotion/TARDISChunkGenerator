package me.eccentric_nz.tardischunkgenerator;

import org.bukkit.Bukkit;

public class TARDISReflector {

    public String MINECRAFT_SERVER_VERSION;

    public TARDISReflector() {
        MINECRAFT_SERVER_VERSION = getMinecraftServerVersion();
    }

    private static String getMinecraftServerVersion() {
        String bukkitPackageName = Bukkit.getServer().getClass().getPackage().getName();
        return bukkitPackageName.substring(bukkitPackageName.lastIndexOf('.') + 1);
    }

    public Class<?> getMinecraftServerClass(String className) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + MINECRAFT_SERVER_VERSION + "." + className);
    }
}
