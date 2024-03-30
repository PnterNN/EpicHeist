package mc.pnternn.epicheist.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import mc.pnternn.epicheist.util.MovementWay;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class RegionEvent extends PlayerEvent{
    private static final HandlerList handlerList = new HandlerList();
    static RegionContainer container;
    private ProtectedRegion region;
    private MovementWay movement;
    public PlayerEvent parentEvent;
    public RegionEvent(ProtectedRegion region, Player player, MovementWay movement, PlayerEvent parent) {
        super(player);
        this.region = region;
        this.movement = movement;
        this.parentEvent = parent;
    }
    public HandlerList getHandlers() {
        return handlerList;
    }
    public ProtectedRegion getRegion() {
        return this.region;
    }
    public static HandlerList getHandlerList() {
        return handlerList;
    }
    public MovementWay getMovementWay() {
        return this.movement;
    }
    public PlayerEvent getParentEvent() {
        return this.parentEvent;
    }
}
