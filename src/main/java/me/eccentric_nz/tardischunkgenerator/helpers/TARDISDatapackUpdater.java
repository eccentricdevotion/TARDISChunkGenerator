package me.eccentric_nz.tardischunkgenerator.helpers;

import me.eccentric_nz.tardischunkgenerator.TARDISHelper;
import me.eccentric_nz.tardischunkgenerator.custombiome.BiomeUtilities;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.logging.Level;

public class TARDISDatapackUpdater {

    private final TARDISHelper plugin;

    public TARDISDatapackUpdater(TARDISHelper plugin) {
        this.plugin = plugin;
    }

    public void updateDimension(String dimension) {
        File container = plugin.getServer().getWorldContainer();
        // read server-properties
        String dataPacksRoot = container.getAbsolutePath() + File.separator + BiomeUtilities.getLevelName() + File.separator + "datapacks" + File.separator;
        // check if directories exist
        String dimensionRoot = dataPacksRoot + dimension + File.separator + "data" + File.separator + "tardis" + File.separator;
        File worldGenDir = new File(dimensionRoot + "worldgen");
        if (worldGenDir.exists()) {
            deleteDirectoryAndContents(worldGenDir.toPath());
        } else {
            // either datapack not installed or already updated
            return;
        }
        File dimensionDir = new File(dimensionRoot + "dimension");
        if (dimensionDir.exists()) {
            File dimensionTypeDir = new File(dimensionRoot + "dimension_type");
            // overwrite files
            File dimFile = new File(dimensionDir, dimension + ".json");
            copy(dimension + "_d.json", dimFile);
            File dimTypeFile = new File(dimensionTypeDir, dimension + ".json");
            copy(dimension + "_dt.json", dimTypeFile);
            File metaFile = new File(dataPacksRoot + dimension, "pack.mcmeta");
            copy("pack_" + dimension + ".mcmeta", metaFile);
        }
    }

    private void deleteDirectoryAndContents(Path path) {
        try {
            Files.walkFileTree(path, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            plugin.getLogger().log(Level.WARNING, "[TARDISChunkGenerator] Could not delete datapack worldgen directory! " + e.getMessage());
        }
    }

    private void copy(String filename, File file) {
        InputStream in = null;
        try {
            in = plugin.getResource(filename);
            OutputStream out = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            try {
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } catch (IOException io) {
                plugin.getLogger().log(Level.WARNING, "[TARDISChunkGenerator] Checker: Could not save the file (" + file + ").");
            } finally {
                try {
                    out.close();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "[TARDISChunkGenerator] Checker: Could not close the output stream.");
                }
            }
        } catch (FileNotFoundException e) {
            plugin.getLogger().log(Level.WARNING, "[TARDISChunkGenerator] Checker: File not found: " + filename);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    plugin.getLogger().log(Level.WARNING, "[TARDISChunkGenerator] Checker: Could not close the input stream.");
                }
            }
        }
    }
}
