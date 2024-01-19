package mc.pnternn.epicheist.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PacketUtils {

    private static final ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();

    public static void sendPacket(Player player, PacketContainer packet) {
        try {
            protocolManager.sendServerPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
            sendPacket(player, packet);
        }
    }

    public static int generateRandomEntityId() {
        return Integer.parseInt(RandomStringUtils.random(8, false, true));
    }

    public static PacketContainer spawnEntityPacket(EntityType entityType, Location location, int entityId) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SPAWN_ENTITY);
        packet.getIntegers().write(0, Integer.valueOf(entityId));
        try {
            packet.getEntityTypeModifier().write(0, entityType);
        } catch (Exception e) {
            if (entityType.equals(EntityType.ARMOR_STAND)) {
                packet.getIntegers().write(6, Integer.valueOf(78));
            } else {
                packet.getIntegers().write(6, Integer.valueOf(entityType.getTypeId()));
            }
            packet.getIntegers().write(1, Integer.valueOf(0));
            packet.getIntegers().write(2, Integer.valueOf(0));
            packet.getIntegers().write(3, Integer.valueOf(0));
            packet.getIntegers().write(4, Integer.valueOf(0));
            packet.getIntegers().write(5, Integer.valueOf(0));
            packet.getIntegers().write(7, Integer.valueOf(0));
        }
        packet.getDoubles().write(0, Double.valueOf(location.getX()));
        packet.getDoubles().write(1, Double.valueOf(location.getY()));
        packet.getDoubles().write(2, Double.valueOf(location.getZ()));
        try {
            packet.getBytes().write(0, Byte.valueOf((byte)(int)location.getPitch()));
            packet.getBytes().write(1, Byte.valueOf((byte)(int)location.getYaw()));
        } catch (Exception exception) {}
        packet.getUUIDs().write(0, UUID.randomUUID());
        return packet;
    }


    public static WrappedDataWatcher getDataWatcher() {
        return new WrappedDataWatcher();
    }

    public static PacketContainer applyMetadata(int entityId, WrappedDataWatcher watcher) {
        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
            packet.getIntegers().write(0, Integer.valueOf(entityId));
            try {
                List<WrappedDataValue> wrappedDataValueList = Lists.newArrayList();
                watcher.getWatchableObjects().stream().filter(Objects::nonNull).forEach(entry -> {
                    WrappedDataWatcher.WrappedDataWatcherObject dataWatcherObject = entry.getWatcherObject();
                    wrappedDataValueList.add(new WrappedDataValue(dataWatcherObject.getIndex(), dataWatcherObject.getSerializer(), entry.getRawValue()));
                });
                packet.getDataValueCollectionModifier().write(0, wrappedDataValueList);
            } catch (Exception e) {
                packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
            }
            return packet;
        } catch (Exception e) {
            return applyMetadata(entityId, watcher);
        }
    }

    public static WrappedDataWatcher setMetadata(WrappedDataWatcher watcher, int index, Class c, Object value) {
        try {
            watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(index, WrappedDataWatcher.Registry.get(c)), value);
        } catch (Exception e) {
            return setMetadata(watcher, index, c, value);
        }
        return watcher;
    }

    public static WrappedDataWatcher setMetadata(WrappedDataWatcher watcher, int index, WrappedDataWatcher.Serializer serializer, Object value) {
        try {
            watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(index, serializer), value);
        } catch (Exception e) {
            return setMetadata(watcher, index, serializer, value);
        }
        return watcher;
    }

    public static PacketContainer getEquipmentPacket(int entityId, Pair<EnumWrappers.ItemSlot, ItemStack>... items) {
        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_EQUIPMENT);
            packet.getIntegers().write(0, Integer.valueOf(entityId));
            List<Pair<EnumWrappers.ItemSlot, ItemStack>> list = Arrays.asList(items);
            packet.getSlotStackPairLists().write(0, list);
            return packet;
        } catch (Exception e) {
            return getEquipmentPacket(entityId, items);
        }
    }

    public static PacketContainer teleportEntityPacket(int entityID, Location location) {
        try {
            PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);
            packet.getIntegers().write(0, Integer.valueOf(entityID));
            packet.getDoubles().write(0, Double.valueOf(location.getX()))
                    .write(1, Double.valueOf(location.getY()))
                    .write(2, Double.valueOf(location.getZ()));
            packet.getBytes().write(0, Byte.valueOf((byte)(int)(location.getYaw() * 256.0F / 360.0F)));
            packet.getBooleans().write(0, Boolean.valueOf(false));
            return packet;
        } catch (Exception e) {
            return teleportEntityPacket(entityID, location);
        }
    }

    public static PacketContainer destroyEntityPacket(int entityID) {
        try {
            List<Integer> entityIDList = new ArrayList<>();
            entityIDList.add(Integer.valueOf(entityID));
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
            packet.getModifier().writeDefaults();
            try {
                packet.getIntLists().write(0, entityIDList);
            } catch (Exception e) {
                packet.getIntegerArrays().write(0, entityIDList.stream().mapToInt(i -> i.intValue()).toArray());
            }
            return packet;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static PacketContainer getHeadRotatePacket(int entityId, Location location) {
        try {
            PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
            pc.getModifier().writeDefaults();
            pc.getIntegers().write(0, Integer.valueOf(entityId));
            pc.getBytes().write(0, Byte.valueOf((byte)getCompressedAngle(location.getYaw())));
            return pc;
        } catch (Exception e) {
            return getHeadRotatePacket(entityId, location);
        }
    }

    public static PacketContainer getHeadLookPacket(int entityId, Location location) {
        try {
            PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.REL_ENTITY_MOVE_LOOK);
            pc.getModifier().writeDefaults();
            pc.getIntegers().write(0, Integer.valueOf(entityId));
            pc.getBytes().write(0, Byte.valueOf((byte)(int)location.getYaw()));
            pc.getBooleans().write(0, Boolean.valueOf(false));
            return pc;
        } catch (Exception e) {
            return getHeadLookPacket(entityId, location);
        }
    }

    public static PacketContainer getPassengerPacket(int vehicleId, int passengerCount, int... passengers) {
        try {
            PacketContainer pc = protocolManager.createPacket(PacketType.Play.Server.MOUNT);
            pc.getIntegers().write(0, Integer.valueOf(vehicleId));
            pc.getIntegerArrays().write(0, passengers);
            return pc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getCompressedAngle(float value) {
        return (int)(value * 256.0F / 360.0F);
    }
}
