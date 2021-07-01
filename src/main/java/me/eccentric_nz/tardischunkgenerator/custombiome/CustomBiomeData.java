package me.eccentric_nz.tardischunkgenerator.custombiome;

public class CustomBiomeData {

    private final String minecraftName;
    private final String customName;
    private float depth;
    private float scale;
    private float temperature;
    private float downfall;
    private int fogColour;
    private int waterColour;
    private int waterFogColour;
    private int skyColour;
    private int foliageColour;
    private int grassColour;
    private boolean frozen;

    public CustomBiomeData(String minecraftName, String customName) {
        this.minecraftName = minecraftName;
        this.customName = customName;
    }

    public CustomBiomeData(String minecraftName, String customName, float depth, float scale, float temperature, float downfall, int fogColour, int waterColour, int waterFogColour, int skyColour, int foliageColour, int grassColour, boolean frozen) {
        this.minecraftName = minecraftName;
        this.customName = customName;
        this.depth = depth;
        this.scale = scale;
        this.temperature = temperature;
        this.downfall = downfall;
        this.fogColour = fogColour;
        this.waterColour = waterColour;
        this.waterFogColour = waterFogColour;
        this.skyColour = skyColour;
        this.foliageColour = foliageColour;
        this.grassColour = grassColour;
        this.frozen = frozen;
    }

    public static int fromHex(String hex) {
        return Integer.parseInt(hex, 16);
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public String getCustomName() {
        return customName;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public float getDownfall() {
        return downfall;
    }

    public void setDownfall(float downfall) {
        this.downfall = downfall;
    }

    public int getFogColour() {
        return fogColour;
    }

    public void setFogColour(String fogColour) {
        this.fogColour = fromHex(fogColour);
    }

    public int getWaterColour() {
        return waterColour;
    }

    public void setWaterColour(String waterColour) {
        this.waterColour = fromHex(waterColour);
    }

    public int getWaterFogColour() {
        return waterFogColour;
    }

    public void setWaterFogColour(String waterFogColour) {
        this.waterFogColour = fromHex(waterFogColour);
    }

    public int getSkyColour() {
        return skyColour;
    }

    public void setSkyColour(String skyColour) {
        this.skyColour = fromHex(skyColour);
    }

    public int getFoliageColour() {
        return foliageColour;
    }

    public void setFoliageColour(String foliageColour) {
        this.foliageColour = fromHex(foliageColour);
    }

    public int getGrassColour() {
        return grassColour;
    }

    public void setGrassColour(String grassColour) {
        this.grassColour = fromHex(grassColour);
    }

    public boolean isFrozen() {
        return frozen;
    }

    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }
}
