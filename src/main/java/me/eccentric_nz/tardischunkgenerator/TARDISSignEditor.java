package me.eccentric_nz.tardischunkgenerator;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

public class TARDISSignEditor {

    private final TARDISReflector reflector;
    private Sign sign;

    public TARDISSignEditor(TARDISReflector reflector) {
        this.reflector = reflector;
    }

    public void cleanup() {
        formatSignForSave(sign);
    }

    public void commit(Player player, Sign sign) {
        this.sign = sign;
        formatSignForEdit(sign);
        try {
            openSignEditor(player, sign);
        } catch (Exception e) {
            formatSignForSave(sign);
        }
    }

    private void openSignEditor(Player player, Sign sign) throws Exception {
        Object entityPlayer = getEntityPlayer(player);
        attachEntityPlayerToSign(entityPlayer, sign);
        Object position = getBlockPosition(sign.getBlock());
        Object packet = createPositionalPacket(position, "PacketPlayOutOpenSignEditor");
        sendPacketToEntityPlayer(packet, entityPlayer);
    }

    private Object getEntityPlayer(Player player) throws Exception {
        Field entityPlayerField = getFirstFieldOfType(player, reflector.getMinecraftServerClass("Entity"));
        return entityPlayerField.get(player);
    }

    private void sendPacketToEntityPlayer(Object packet, Object entityPlayer) throws Exception {
        Object connection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);
        connection.getClass().getDeclaredMethod("sendPacket", reflector.getMinecraftServerClass("Packet")).invoke(connection, packet);
    }

    private Object createPositionalPacket(Object position, String typeOfPacket) throws Exception {
        return createPositionalPacket(position, reflector.getMinecraftServerClass(typeOfPacket));
    }

    private Object createPositionalPacket(Object position, Class<?> typeOfPacket) throws Exception {
        return typeOfPacket.getConstructor(reflector.getMinecraftServerClass("BlockPosition")).newInstance(position);
    }

    private Object getBlockPosition(Block block) throws Exception {
        return reflector.getMinecraftServerClass("BlockPosition").getConstructor(int.class, int.class, int.class).newInstance(block.getX(), block.getY(), block.getZ());
    }

    private void attachEntityPlayerToSign(Object entityPlayer, Sign sign) throws Exception {
        Object tileEntitySign = getTileEntitySign(sign);
        maketileEntitySignEditable(tileEntitySign);
        Field signEntityHumanField = getFirstFieldOfType(tileEntitySign, reflector.getMinecraftServerClass("EntityHuman"));
        signEntityHumanField.set(tileEntitySign, entityPlayer);
    }

    private Object getTileEntitySign(Sign sign) throws Exception {
        Field tileEntityField = getFirstFieldOfType(sign, reflector.getMinecraftServerClass("TileEntity"));
        return tileEntityField.get(sign);
    }

    private void maketileEntitySignEditable(Object tileEntitySign) throws Exception {
        Field signIsEditable = tileEntitySign.getClass().getDeclaredField("isEditable");
        signIsEditable.setAccessible(true);
        signIsEditable.set(tileEntitySign, true);
    }

    private Field getFirstFieldOfType(Object source, Class<?> desiredType) throws NoSuchFieldException {
        return getFirstFieldOfType(source.getClass(), desiredType);
    }

    private Field getFirstFieldOfType(Class<?> source, Class<?> desiredType) throws NoSuchFieldException {
        Class<?> ancestor = source;
        while (ancestor != null) {
            Field[] fields = ancestor.getDeclaredFields();
            for (Field field : fields) {
                Class<?> candidateType = field.getType();
                if (desiredType.isAssignableFrom(candidateType)) {
                    field.setAccessible(true);
                    return field;
                }
            }
            ancestor = ancestor.getSuperclass();
        }
        throw new NoSuchFieldException("Cannot match " + desiredType.getName() + " in ancestry of " + source.getName());
    }

    private void formatSignForEdit(Sign sign) {
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, sign.getLine(i).replace('§', '&'));
        }
        sign.update();
    }

    private void formatSignForSave(Sign sign) {
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, sign.getLine(i).replace('&', '§'));
        }
        sign.update();
    }
}
