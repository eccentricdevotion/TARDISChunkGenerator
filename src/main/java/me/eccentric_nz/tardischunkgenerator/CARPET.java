package me.eccentric_nz.tardischunkgenerator;

import org.bukkit.Material;

public enum CARPET {

    WHITE(Material.WHITE_CARPET),
    ORANGE(Material.ORANGE_CARPET),
    MAGENTA(Material.MAGENTA_CARPET),
    LIGHT_BLUE(Material.LIGHT_BLUE_CARPET),
    YELLOW(Material.YELLOW_CARPET),
    LIME(Material.LIME_CARPET),
    PINK(Material.PINK_CARPET),
    GRAY(Material.GRAY_CARPET),
    LIGHT_GRAY(Material.LIGHT_GRAY_CARPET),
    CYAN(Material.CYAN_CARPET),
    PURPLE(Material.PURPLE_CARPET),
    BLUE(Material.BLUE_CARPET),
    BROWN(Material.BROWN_CARPET),
    GREEN(Material.GREEN_CARPET),
    RED(Material.RED_CARPET),
    BLACK(Material.BLACK_CARPET);

    private final Material carpet;

    CARPET(Material carpet) {
        this.carpet = carpet;
    }

    public Material getCarpet() {
        return carpet;
    }
}
