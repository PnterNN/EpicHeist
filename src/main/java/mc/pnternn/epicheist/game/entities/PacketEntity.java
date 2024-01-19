package mc.pnternn.epicheist.game.entities;

import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.Pair;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import mc.pnternn.epicheist.util.PacketUtils;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

public class PacketEntity {
    public int getEntityId() {
        return this.entityId;
    }

    public EntityType getEntityType() {
        return this.entityType;
    }

    public boolean isCrouching() {
        return this.crouching;
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public boolean isGlowing() {
        return this.glowing;
    }

    public boolean isElytraFlying() {
        return this.elytraFlying;
    }

    public boolean isShowName() {
        return this.showName;
    }

    public boolean isSilent() {
        return this.silent;
    }

    public boolean isNoGravity() {
        return this.noGravity;
    }

    public String getName() {
        return this.name;
    }

    public Location getCoreLocation() {
        return this.coreLocation;
    }

    public EulerAngle getHeadRotation() {
        return this.headRotation;
    }

    public EulerAngle getBodyRotation() {
        return this.bodyRotation;
    }

    public EulerAngle getLeftArmRotation() {
        return this.leftArmRotation;
    }

    public EulerAngle getRightArmRotation() {
        return this.rightArmRotation;
    }

    public EulerAngle getLeftLegRotation() {
        return this.leftLegRotation;
    }

    public EulerAngle getRightLegRotation() {
        return this.rightLegRotation;
    }

    public List<Pair<EnumWrappers.ItemSlot, ItemStack>> getEquipment() {
        return this.equipment;
    }

    private final int entityId = PacketUtils.generateRandomEntityId();

    private EntityType entityType = EntityType.PIG;

    private boolean invisible = false;

    private boolean crouching = false;

    private boolean glowing = false;

    private boolean elytraFlying = false;

    private boolean showName = false;

    private boolean noGravity = true;

    private boolean silent = true;

    private String name = "";

    private List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment = new ArrayList<>();

    private Location coreLocation;

    private Location location;

    private EulerAngle headRotation;

    private EulerAngle bodyRotation;

    private EulerAngle leftArmRotation;

    private EulerAngle rightArmRotation;

    private EulerAngle leftLegRotation;

    private EulerAngle rightLegRotation;

    public void spawn(Player player) {
        PacketContainer packet = PacketUtils.spawnEntityPacket(this.entityType, this.location, this.entityId);
        PacketUtils.sendPacket(player, packet);
        WrappedDataWatcher dataWatcher = PacketUtils.getDataWatcher();
        byte flags = 0;
        if (isCrouching())
            flags = (byte)(flags + 2);
        if (isInvisible())
            flags = (byte)(flags + 32);
        if (isGlowing())
            flags = (byte)(flags + 64);
        if (isElytraFlying())
            flags = (byte)(flags - 128);
        PacketUtils.setMetadata(dataWatcher, 0, Byte.class, Byte.valueOf(flags));
        if (!Objects.equals(this.name, "")) {
            Optional<?> opt = Optional.of(WrappedChatComponent.fromChatMessage(this.name)[0].getHandle());
            PacketUtils.setMetadata(dataWatcher, 2, WrappedDataWatcher.Registry.getChatComponentSerializer(true), opt);
        }
        if (isShowName())
            PacketUtils.setMetadata(dataWatcher, 3, Boolean.class, Boolean.valueOf(isShowName()));
        if (isNoGravity())
            PacketUtils.setMetadata(dataWatcher, 5, Boolean.class, Boolean.valueOf(isNoGravity()));
        if (isSilent())
            PacketUtils.setMetadata(dataWatcher, 4, Boolean.class, Boolean.valueOf(isSilent()));
        PacketContainer packet1 = PacketUtils.applyMetadata(this.entityId, dataWatcher);
        PacketUtils.sendPacket(player, packet1);
        for (Pair<EnumWrappers.ItemSlot, ItemStack> itemSlotItemStackPair : this.equipment) {
            PacketContainer packet2 = PacketUtils.getEquipmentPacket(this.entityId, (Pair<EnumWrappers.ItemSlot, ItemStack>[])new Pair[] { itemSlotItemStackPair });
            PacketUtils.sendPacket(player, packet2);
        }
    }

    public void delete(Player player) {
        PacketContainer destroyPacket = PacketUtils.destroyEntityPacket(this.entityId);
        PacketUtils.sendPacket(player, destroyPacket);
    }

    public void teleport(Player player, Location location) {
        this.location = location;
        PacketContainer teleportPacket = PacketUtils.teleportEntityPacket(this.entityId, location);
        PacketContainer headRotatePacket = PacketUtils.getHeadRotatePacket(this.entityId, location);
        PacketContainer bodyRotatePacket = PacketUtils.getHeadLookPacket(this.entityId, location);
        PacketUtils.sendPacket(player, teleportPacket);
        PacketUtils.sendPacket(player, bodyRotatePacket);
        PacketUtils.sendPacket(player, headRotatePacket);
    }


    public PacketEntity addEquipment(Pair<EnumWrappers.ItemSlot, ItemStack>... equipment) {
        this.equipment.addAll(Arrays.asList(equipment));
        return this;
    }

    public PacketEntity addEquipment(Pair<EnumWrappers.ItemSlot, ItemStack> equipment) {
        this.equipment.add(equipment);
        return this;
    }

    public PacketEntity setEquipment(List<Pair<EnumWrappers.ItemSlot, ItemStack>> equipment) {
        this.equipment = equipment;
        return this;
    }

    public PacketEntity setInvisible(boolean invisible) {
        this.invisible = invisible;
        return this;
    }

    public PacketEntity setShowName(boolean showName) {
        this.showName = showName;
        return this;
    }

    public PacketEntity setName(String name) {
        this.name = name;
        return this;
    }

    public PacketEntity setNoGravity(boolean hasNoGravity) {
        this.noGravity = hasNoGravity;
        return this;
    }

    public PacketEntity setSilent(boolean silent) {
        this.silent = silent;
        return this;
    }

    public PacketEntity setLocation(Location location) {
        this.location = location;
        this.coreLocation = location;
        return this;
    }

    public PacketEntity setCoreLocation(Location coreLocation) {
        this.coreLocation = coreLocation;
        return this;
    }

    public PacketEntity setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public Location getLocation() {
        return this.location;
    }

    public PacketEntity setHeadRotation(EulerAngle headRotation) {
        this.headRotation = headRotation;
        return this;
    }

    public PacketEntity setBodyRotation(EulerAngle bodyRotation) {
        this.bodyRotation = bodyRotation;
        return this;
    }

    public PacketEntity setLeftArmRotation(EulerAngle leftArmRotation) {
        this.leftArmRotation = leftArmRotation;
        return this;
    }

    public PacketEntity setLeftLegRotation(EulerAngle leftLegRotation) {
        this.leftLegRotation = leftLegRotation;
        return this;
    }

    public PacketEntity setRightArmRotation(EulerAngle rightArmRotation) {
        this.rightArmRotation = rightArmRotation;
        return this;
    }

    public PacketEntity setRightLegRotation(EulerAngle rightLegRotation) {
        this.rightLegRotation = rightLegRotation;
        return this;
    }

    public PacketEntity setCrouching(boolean crouching) {
        this.crouching = crouching;
        return this;
    }

    public PacketEntity setElytraFlying(boolean elytraFlying) {
        this.elytraFlying = elytraFlying;
        return this;
    }

    public PacketEntity setEntityType(EntityType entityType) {
        this.entityType = entityType;
        return this;
    }
}
