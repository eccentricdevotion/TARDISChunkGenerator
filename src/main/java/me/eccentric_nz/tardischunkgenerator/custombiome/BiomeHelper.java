package me.eccentric_nz.tardischunkgenerator.custombiome;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_18_R2.CraftServer;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class BiomeHelper {

    DedicatedServer dedicatedServer = ((CraftServer) Bukkit.getServer()).getServer();

    /**
     * Set a chunk to a custom biome
     *
     * @param newBiomeName the name of the custom biome to set (such as tardis:skaro_lakes)
     * @param chunk        the chunk to set the biome for
     * @return true if the biome was set
     */
    public boolean setCustomBiome(String newBiomeName, Chunk chunk) {
        WritableRegistry<Biome> registryWritable = dedicatedServer.registryAccess().ownedRegistry(Registry.BIOME_REGISTRY).get();
        ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(newBiomeName.toLowerCase()));
        Biome base = registryWritable.get(key);
        if (base == null) {
            if (newBiomeName.contains(":")) {
                ResourceKey<Biome> newKey = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(newBiomeName.split(":")[0].toLowerCase(), newBiomeName.split(":")[1].toLowerCase()));
                base = registryWritable.get(newKey);
                if (base == null) {
                    return false;
                }
            } else {
                return false;
            }
        }
        Level w = ((CraftWorld) chunk.getWorld()).getHandle();
        for (int x = 0; x <= 15; x++) {
            for (int z = 0; z <= 15; z++) {
                for (int y = 0; y <= chunk.getWorld().getMaxHeight(); y++) {
                    setCustomBiome(chunk.getX() * 16 + x, y, chunk.getZ() * 16 + z, w, base);
                }
            }
        }
        refreshChunksForAll(chunk);
        return true;
    }

    /**
     * Set a location to a custom biome
     *
     * @param newBiomeName the name of the custom biome to set (such as tardis:skaro_lakes)
     * @param location     the location to set the biome for
     * @return true if the biome was set
     */
    public boolean setCustomBiome(String newBiomeName, Location location) {
        Biome base;
        WritableRegistry<Biome> registrywritable = dedicatedServer.registryAccess().ownedRegistry(Registry.BIOME_REGISTRY).get();
        ResourceKey<Biome> key = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(newBiomeName.toLowerCase()));
        base = registrywritable.get(key);
        if (base == null) {
            if (newBiomeName.contains(":")) {
                ResourceKey<Biome> newKey = ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(newBiomeName.split(":")[0].toLowerCase(), newBiomeName.split(":")[1].toLowerCase()));
                base = registrywritable.get(newKey);
                if (base == null) {
                    return false;
                }
            } else {
                return false;
            }
        }
        setCustomBiome(location.getBlockX(), location.getBlockY(), location.getBlockZ(), ((CraftWorld) location.getWorld()).getHandle(), base);
        refreshChunksForAll(location.getChunk());
        return true;
    }

    private void setCustomBiome(int x, int y, int z, Level w, Biome bb) {
        BlockPos pos = new BlockPos(x, 0, z);
        if (w.isLoaded(pos)) {
            ChunkAccess chunk = w.getChunk(pos);
            if (chunk != null) {
                chunk.setBiome(x >> 2, y >> 2, z >> 2, bb);
            }
        }
    }

    /**
     * Refreshes biome changes for players by resending the MapChunk packet
     *
     * @param chunk the chunk to refresh
     */
    private void refreshChunksForAll(Chunk chunk) {
        LevelChunk c = ((CraftChunk) chunk).getHandle();
        int viewDistance = Bukkit.getServer().getViewDistance() * 16;
        int viewDistanceSquared = viewDistance * viewDistance;
        for (Player player : chunk.getWorld().getPlayers()) {
            if (player.isOnline() && player.getLocation().distanceSquared(chunk.getBlock(0, 0, 0).getLocation()) < viewDistanceSquared) {
                ((CraftPlayer) player).getHandle().connection.connection.send(new ClientboundLevelChunkWithLightPacket(c, c.getLevel().getLightEngine(), null, null, true));
            }
        }
    }
}
