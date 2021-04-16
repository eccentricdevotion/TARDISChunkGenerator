package me.eccentric_nz.tardischunkgenerator.dimensions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Lifecycle;
import me.eccentric_nz.tardischunkgenerator.TARDISHelper;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.CraftServer;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.event.world.WorldLoadEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

public class TARDISDimensions {

    public static org.bukkit.World load(String dimension) throws Exception {
        org.bukkit.World loaded = null;
        DedicatedServer console = ((CraftServer) Bukkit.getServer()).getServer();
        Field field = console.getClass().getSuperclass().getDeclaredField("saveData");
        field.setAccessible(true);
        SaveData saveData = (SaveData) field.get(console);
        GeneratorSettings mainGenSettings = saveData.getGeneratorSettings();
        RegistryMaterials<WorldDimension> dimensionRegistry = mainGenSettings.d();
        Iterator<Map.Entry<ResourceKey<WorldDimension>, WorldDimension>> dimIterator = dimensionRegistry.d().iterator();
        org.bukkit.World mainWorld = Bukkit.getWorlds().get(0);
        Convertable convertable = Convertable.a(Bukkit.getWorldContainer().toPath());
        // iterate through possible dimensions
        while (dimIterator.hasNext()) {
            Map.Entry<ResourceKey<WorldDimension>, WorldDimension> dimEntry = dimIterator.next();
            ResourceKey<WorldDimension> dimKey = dimEntry.getKey();
            // the default dimensions are already loaded
            if (dimKey != WorldDimension.OVERWORLD && dimKey != WorldDimension.THE_NETHER && dimKey != WorldDimension.THE_END) {
                ResourceKey<World> worldKey = ResourceKey.a(IRegistry.L, dimKey.a());
                DimensionManager dimensionmanager = dimEntry.getValue().b();
                ChunkGenerator chunkgenerator = dimEntry.getValue().c();
                String name = dimKey.a().getKey();
                // this is the dimension we want to load
                if (name.equalsIgnoreCase(dimension)) {
                    Bukkit.getLogger().log(Level.INFO, TARDISHelper.messagePrefix + "Loading " + name);
                    Convertable.ConversionSession session = convertable.new ConversionSession(name, dimKey) {
                        // the original session isn't prepared for custom dimensions
                        @Override
                        public File a(ResourceKey<World> resourceKey) {
                            return new File(folder.toFile(), "custom");
                        }
                    };
                    MinecraftServer.convertWorld(session);
                    // load world settings or create default values
                    RegistryReadOps<NBTBase> registryReadOps = RegistryReadOps.a(DynamicOpsNBT.a, console.dataPackResources.h(), console.customRegistry);
                    WorldDataServer worldData = (WorldDataServer) session.a(registryReadOps, console.datapackconfiguration);
                    if (worldData == null) {
                        Properties properties = new Properties();
                        properties.put("level-seed", Objects.toString(mainWorld.getSeed()));
                        properties.put("generate-structures", "true");
                        properties.put("level-type", "DEFAULT");
                        GeneratorSettings dimGenSettings = GeneratorSettings.a(console.getCustomRegistry(), properties);
                        WorldSettings worldSettings = new WorldSettings(name, EnumGamemode.getById(Bukkit.getDefaultGameMode().getValue()), false, // hardcore
                                EnumDifficulty.EASY, false, new GameRules(), console.datapackconfiguration);
                        worldData = new WorldDataServer(worldSettings, dimGenSettings, Lifecycle.stable());
                    }
                    worldData.checkName(name);
                    worldData.a(console.getServerModName(), console.getModded().isPresent());
                    if (console.options.has("forceUpgrade")) {
                        net.minecraft.server.v1_16_R3.Main.convertWorld(session, DataConverterRegistry.a(), console.options.has("eraseCache"), () -> true, worldData.getGeneratorSettings().d().d().stream().map((entry2) -> ResourceKey.a(IRegistry.K, entry2.getKey().a())).collect(ImmutableSet.toImmutableSet()));
                    }
                    List<MobSpawner> spawners = ImmutableList.of(new MobSpawnerPhantom(), new MobSpawnerPatrol(), new MobSpawnerCat(), new VillageSiege(), new MobSpawnerTrader(worldData));
                    ResourceKey<DimensionManager> dimManResKey = ResourceKey.a(IRegistry.K, dimKey.a());
                    RegistryMaterials<DimensionManager> dimRegistry = ((RegistryMaterials<DimensionManager>) console.customRegistry.a());
                    MinecraftKey key = dimRegistry.getKey(dimensionmanager);
                    if (key != null) {
                        // the loaded manager is the same
                        Bukkit.getLogger().log(Level.SEVERE, TARDISHelper.messagePrefix + "Dimension manager already loaded with key " + key + "! Skipping");
                        continue;
                    }
                    // replace existing dimension manager, correctly setting the ID up (which is -1 for default worlds...)
                    dimRegistry.a(OptionalInt.empty(), dimManResKey, dimensionmanager, Lifecycle.stable());
                    WorldLoadListener worldloadlistener = console.worldLoadListenerFactory.create(11);
                    WorldServer worldserver = new WorldServer(console, console.executorService, session, worldData, worldKey, dimensionmanager, worldloadlistener, chunkgenerator, false, // isDebugWorld
                            BiomeManager.a(worldData.getGeneratorSettings().getSeed()), // biome seed
                            spawners, true, // tickTime
                            org.bukkit.World.Environment.NORMAL, null // another chunk generator
                    );
                    loaded = Bukkit.getWorld(name.toLowerCase(Locale.ENGLISH));
                    if (loaded == null) {
                        Bukkit.getLogger().log(Level.INFO, TARDISHelper.messagePrefix + "Failed to load custom dimension " + name);
                    } else {
                        console.initWorld(worldserver, worldData, worldData, worldData.getGeneratorSettings());
                        worldserver.setSpawnFlags(true, true);
                        console.worldServer.put(worldserver.getDimensionKey(), worldserver);
                        Bukkit.getPluginManager().callEvent(new WorldInitEvent(worldserver.getWorld()));
                        console.loadSpawn(worldserver.getChunkProvider().playerChunkMap.worldLoadListener, worldserver);
                        Bukkit.getPluginManager().callEvent(new WorldLoadEvent(worldserver.getWorld()));
                    }
                }
            }
        }
        return loaded;
    }
}
