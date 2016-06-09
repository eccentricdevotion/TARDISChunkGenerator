package me.eccentric_nz.tardischunkgenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.server.v1_10_R1.AttributeInstance;
import net.minecraft.server.v1_10_R1.BlockPosition;
import net.minecraft.server.v1_10_R1.EntityInsentient;
import net.minecraft.server.v1_10_R1.EntityVillager;
import net.minecraft.server.v1_10_R1.GenericAttributes;
import net.minecraft.server.v1_10_R1.NBTBase;
import net.minecraft.server.v1_10_R1.NBTTagCompound;
import net.minecraft.server.v1_10_R1.TileEntity;
import net.minecraft.server.v1_10_R1.TileEntityFurnace;
import net.minecraft.server.v1_10_R1.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_10_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_10_R1.entity.CraftVillager;
import org.bukkit.craftbukkit.v1_10_R1.inventory.CraftItemStack;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.Villager;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TARDISHelper extends JavaPlugin implements TARDISHelperAPI {

    public String pluginName;
    private static String versionPrefix = null;
    private transient Method getEntityHandle;
    private transient Field isInvulnerable;
    private TARDISHelper tardisHelper;

    @Override
    public void onEnable() {
        // nms reflection stuff for flagging invulnerable drops
        try {
            // get the field for flagging an entity invulnerable in the NMS class, and set it to accessible for later
            isInvulnerable = getVersionedClass("net.minecraft.server.Entity").getDeclaredField("invulnerable");
            isInvulnerable.setAccessible(true);
            // get the method for getting underlying NMS entity object handle from a craftbukkit entity object
            getEntityHandle = getVersionedClass("org.bukkit.craftbukkit.entity.CraftEntity").getMethod("getHandle");

        } catch (ClassNotFoundException ex) {
            System.err.println("[TARDISHelper] Failed to access invulnerability methods: " + ex.getMessage());
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to access invulnerability methods: " + ex.getMessage());
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to access invulnerability methods: " + ex.getMessage());
        } catch (NoSuchMethodException ex) {
            System.err.println("[TARDISHelper] Failed to access invulnerability methods: " + ex.getMessage());
        }
        tardisHelper = this;
    }

    public TARDISHelper getTardisHelper() {
        return tardisHelper;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String worldName, String id) {
        return new TARDISChunkGenerator();
    }

    @Override
    public Double getHorseSpeed(Horse h) {
        AttributeInstance attributes = ((EntityInsentient) ((CraftLivingEntity) h).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        return attributes.getValue();
    }

    @Override
    public void setHorseSpeed(Horse h, double speed) {
        // use about  2.25 for normalish speed
        AttributeInstance attributes = ((EntityInsentient) ((CraftLivingEntity) h).getHandle()).getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        attributes.setValue(speed);
    }

    // finds the version specific NMS class to match the general name
    public static Class<?> getVersionedClass(String className) throws ClassNotFoundException {
        if (versionPrefix == null) {
            String serverClassName = Bukkit.getServer().getClass().getName();
            String[] packages = serverClassName.split("\\.");
            if (packages.length == 5) {
                versionPrefix = packages[3] + ".";
            }
        }
        className = className.replace("org.bukkit.craftbukkit.", "org.bukkit.craftbukkit." + versionPrefix);
        className = className.replace("net.minecraft.server.", "net.minecraft.server." + versionPrefix);
        return TARDISHelper.class.getClassLoader().loadClass(className);
    }

    @Override
    public void protect(Item item) {
        try {
            isInvulnerable.set(getEntityHandle.invoke(item), true);
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to protect TARDIS Siege Cube: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to protect TARDIS Siege Cube: " + ex.getMessage());
        } catch (InvocationTargetException ex) {
            System.err.println("[TARDISHelper] Failed to protect TARDIS Siege Cube: " + ex.getMessage());
        }
    }

    @Override
    public void nameFurnaceGUI(Block block, String name) {
        WorldServer ws = ((CraftWorld) block.getWorld()).getHandle();
        BlockPosition bp = new BlockPosition(block.getX(), block.getY(), block.getZ());
        TileEntity tile = ws.getTileEntity(bp);
        if (tile == null || !(tile instanceof TileEntityFurnace)) {
            return;
        }
        TileEntityFurnace furnace = (TileEntityFurnace) tile;
        furnace.a(name);
    }

    @Override
    public int getVillagerCareer(Villager v) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field careerField = EntityVillager.class.getDeclaredField("bJ");
            careerField.setAccessible(true);
//            String career = "";
//            switch (villager.getProfession()) {
//                case 0:
//                    if (careerField.getInt(villager) == 1) {
//                        career = "farmer";
//                    } else if (careerField.getInt(villager) == 2) {
//                        career = "fisherman";
//                    } else if (careerField.getInt(villager) == 3) {
//                        career = "shepherd";
//                    } else if (careerField.getInt(villager) == 4) {
//                        career = "fletcher";
//                    }
//                    break;
//                case 1:
//                    career = "librarian";
//                    break;
//                case 2:
//                    career = "cleric";
//                    break;
//                case 3:
//                    if (careerField.getInt(villager) == 1) {
//                        career = "armor";
//                    } else if (careerField.getInt(villager) == 2) {
//                        career = "weapon";
//                    } else if (careerField.getInt(villager) == 3) {
//                        career = "tool";
//                    }
//                    break;
//                case 4:
//                    if (careerField.getInt(villager) == 1) {
//                        career = "butcher";
//                    } else if (careerField.getInt(villager) == 2) {
//                        career = "leather";
//                    }
//            }
//            System.out.println("[TARDISHelper] Villager career: " + career);
            return careerField.getInt(villager);
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career: " + ex.getMessage());
            return 0;
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career: " + ex.getMessage());
            return 0;
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career: " + ex.getMessage());
            return 0;
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career: " + ex.getMessage());
            return 0;
        }
    }

    @Override
    public void setVillagerCareer(Villager v, int c) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field careerField = EntityVillager.class.getDeclaredField("bJ");
            careerField.setAccessible(true);
            careerField.set(villager, c);
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career: " + ex.getMessage());
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career: " + ex.getMessage());
        }
    }

    @Override
    public int getVillagerCareerLevel(Villager v) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field careerLevelField = EntityVillager.class.getDeclaredField("bK");
            careerLevelField.setAccessible(true);
            return careerLevelField.getInt(villager);
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career level: " + ex.getMessage());
            return 0;
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career level: " + ex.getMessage());
            return 0;
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career level: " + ex.getMessage());
            return 0;
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to get villager career level: " + ex.getMessage());
            return 0;
        }
    }

    @Override
    public void setVillagerCareerLevel(Villager v, int l) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field careerField = EntityVillager.class.getDeclaredField("bK");
            careerField.setAccessible(true);
            careerField.set(villager, l);
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career level: " + ex.getMessage());
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career level: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career level: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to set villager career level: " + ex.getMessage());
        }
    }

    @Override
    public boolean getVillagerWilling(Villager v) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field willingField = EntityVillager.class.getDeclaredField("bG");
            willingField.setAccessible(true);
            return willingField.getBoolean(villager);
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to get villager willingness: " + ex.getMessage());
            return false;
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to get villager willingness: " + ex.getMessage());
            return false;
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to get villager willingness: " + ex.getMessage());
            return false;
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to get villager willingness: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public void setVillagerWilling(Villager v, boolean w) {
        try {
            EntityVillager villager = ((CraftVillager) v).getHandle();
            Field willingField = EntityVillager.class.getDeclaredField("bG");
            willingField.setAccessible(true);
            willingField.set(villager, w);
        } catch (NoSuchFieldException ex) {
            System.err.println("[TARDISHelper] Failed to set villager willingness: " + ex.getMessage());
        } catch (SecurityException ex) {
            System.err.println("[TARDISHelper] Failed to set villager willingness: " + ex.getMessage());
        } catch (IllegalArgumentException ex) {
            System.err.println("[TARDISHelper] Failed to set villager willingness: " + ex.getMessage());
        } catch (IllegalAccessException ex) {
            System.err.println("[TARDISHelper] Failed to set villager willingness: " + ex.getMessage());
        }
    }

    @Override
    public void setCookTimeTotal(Block f, int c) {
        try {
            TileEntityFurnace furnace = (TileEntityFurnace) ((CraftWorld) f.getWorld()).getTileEntityAt(f.getX(), f.getY(), f.getZ());
            Field cttField = TileEntityFurnace.class.getDeclaredField("cookTimeTotal");
            cttField.setAccessible(true);
            cttField.set(furnace, c);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(TARDISHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(TARDISHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(TARDISHelper.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(TARDISHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void refreshChunk(Chunk c) {
        TARDISPacketMapChunk.refreshChunk(c);
    }

    @Override
    public ItemStack setSpawnEggType(ItemStack is, EntityType et) {
        ItemStack result = null;
        try {
            Object nmsStack = CraftItemStack.class.getMethod("asNMSCopy", ItemStack.class).invoke(null, is);
            Object nmsCompound = nmsStack.getClass().getMethod("getTag").invoke(nmsStack);

            if (nmsCompound == null) {
                nmsCompound = NBTTagCompound.class.getConstructor().newInstance();
            }

            Object nmsTag = nmsCompound.getClass().getConstructor().newInstance();

            nmsTag.getClass().getMethod("setString", String.class, String.class).invoke(nmsTag, "id", et.getName());
            nmsCompound.getClass().getMethod("set", String.class, NBTBase.class).invoke(nmsCompound, "EntityTag", nmsTag);
            nmsStack.getClass().getMethod("setTag", nmsCompound.getClass()).invoke(nmsStack, nmsCompound);

            result = ((ItemStack) CraftItemStack.class.getMethod("asBukkitCopy", nmsStack.getClass()).invoke(null, nmsStack));
        } catch (NoSuchMethodException exception) {
        } catch (SecurityException exception) {
        } catch (IllegalAccessException exception) {
        } catch (IllegalArgumentException exception) {
        } catch (InvocationTargetException exception) {
        } catch (InstantiationException exception) {
        }
        return result;
    }
}
