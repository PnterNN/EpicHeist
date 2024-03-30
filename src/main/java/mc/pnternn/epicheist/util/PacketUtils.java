package mc.pnternn.epicheist.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.google.common.collect.Lists;

import java.lang.reflect.InvocationTargetException;
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
    public static void hideEntity(Player player,Integer entityId) {
        List<Integer> entityIDList = new ArrayList<>();
        entityIDList.add(entityId);
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
        packet.getIntLists().write(0, entityIDList);
        protocolManager.sendServerPacket(player, packet);
    }
}
