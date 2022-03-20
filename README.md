# TARDISChunkGenerator

An add-on plugin for [TARDIS](https://github.com/eccentricdevotion/TARDIS) that generates empty space for the TARDIS
time vortex, and is used to access `net.minecraft.server` and `org.bukkit.craftbukkit` packages unavailable in the
Spigot API.

> __REQUIRED__ by the TARDIS plugin.

Also includes world generators for:

### Void

`TARDISChunkGenerator:void`

An empty world.

![Void world](https://dl.dropboxusercontent.com/s/xsfz2yrvfmd60tk/void_world.jpg?dl=1)

### Gallifrey

`TARDISChunkGenerator:gallifrey`

Badlands biomes with custom sky colour, trees and Time Lord structures.

![Gallifrey world](https://dl.dropboxusercontent.com/s/dkkpgbikptlewdz/gallifrey_world.jpg?dl=1)

### Siluria

`TARDISChunkGenerator:siluria`

Bamboo & sparse jungle biomes with custom Silurian structures.

![Siluria world](https://dl.dropboxusercontent.com/s/xzsg0fdh2sdr5u8/siluria_world.jpg?dl=1)

### Skaro

`TARDISChunkGenerator:skaro`

Desert biome with custom sky colour, trees and Dalek structures.

![Skaro world](https://dl.dropboxusercontent.com/s/pz81stm32y1vd1w/skaro_world.jpg?dl=1)

### Configurable flat world

`TARDISChunkGenerator:flat`

Set block types in _config.yml_

```yaml
## Blocks to use in the Flat World Generator
# Use Spigot Material names
# bottom has 1 layer
bottom: BEDROCK
# rock has ~60 layers
rock: STONE
# middle has 3 layers
middle: DIRT
# surface has 1 layer
surface: GRASS_BLOCK
```

![Flat world](https://dl.dropboxusercontent.com/s/nf22vr65nn2dlkd/flat_world.jpg?dl=1)

### Water world

`TARDISChunkGenerator:water`

Ocean biomes and a small island at spawn, plus a 1/1000 chance an island will generate in a chunk.

![Water world](https://dl.dropboxusercontent.com/s/13j24de5q6b9llw/water_world_1.png?dl=1)

![Water world](https://dl.dropboxusercontent.com/s/vxs3c5d2gxxhlnd/water_world_2.jpg?dl=1)
