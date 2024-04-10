package mc.pnternn.epicheist.listeners;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.events.RegionEnterEvent;
import mc.pnternn.epicheist.events.RegionEnteredEvent;
import mc.pnternn.epicheist.events.RegionLeaveEvent;
import mc.pnternn.epicheist.events.RegionLeftEvent;
import mc.pnternn.epicheist.util.MovementWay;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.*;

public class RegionEventsListener implements Listener {
    private Map<Player, Set<ProtectedRegion>> playerRegions;

    public RegionEventsListener() {
        this.playerRegions = new HashMap<>();
    }
    public void onPlayerKick(PlayerKickEvent e) {
        Set<ProtectedRegion> regions = this.playerRegions.remove(e.getPlayer());
        if (regions != null)
            for (ProtectedRegion region : regions) {
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, (PlayerEvent)e);
                EpicHeist.getInstance().getServer().getPluginManager().callEvent((Event)leaveEvent);
            }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Set<ProtectedRegion> regions = this.playerRegions.remove(e.getPlayer());
        if (regions != null)
            for (ProtectedRegion region : regions) {
                RegionLeaveEvent leaveEvent = new RegionLeaveEvent(region, e.getPlayer(), MovementWay.DISCONNECT, (PlayerEvent)e);
                EpicHeist.getInstance().getServer().getPluginManager().callEvent((Event)leaveEvent);
            }
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        e.setCancelled(updateRegions(e.getPlayer(), MovementWay.MOVE, e.getTo(), (PlayerEvent)e));
    }

    public void onPlayerTeleport(PlayerTeleportEvent e) {
        e.setCancelled(updateRegions(e.getPlayer(), MovementWay.TELEPORT, e.getTo(), (PlayerEvent)e));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getPlayer().getLocation(), (PlayerEvent)e);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        updateRegions(e.getPlayer(), MovementWay.SPAWN, e.getRespawnLocation(), (PlayerEvent)e);
    }

    private synchronized boolean updateRegions(final Player player, final MovementWay movement, Location to, final PlayerEvent event) {
        Set<ProtectedRegion> regions;
        if (this.playerRegions.get(player) == null) {
            regions = new HashSet<>();
        } else {
            regions = new HashSet<>(this.playerRegions.get(player));
        }
        Set<ProtectedRegion> oldRegions = new HashSet<>(regions);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager rm = container.get(BukkitAdapter.adapt(to.getWorld()));
        if (rm == null)
            return false;
        ApplicableRegionSet appRegions = rm.getApplicableRegions(BukkitAdapter.asBlockVector(to));
        for (ProtectedRegion region : appRegions) {
            if (!regions.contains(region)) {
                RegionEnterEvent e = new RegionEnterEvent(region, player, movement, event);
                EpicHeist.getInstance().getServer().getPluginManager().callEvent((Event)e);
                if (e.isCancelled()) {
                    regions.clear();
                    regions.addAll(oldRegions);
                    return true;
                }
                Bukkit.getScheduler().runTaskLater(EpicHeist.getInstance(), new Runnable() {
                    public void run() {
                        RegionEnteredEvent e = new RegionEnteredEvent(region, player, movement, event);
                        EpicHeist.getInstance().getServer().getPluginManager().callEvent((Event)e);
                    }
                },1L);
                regions.add(region);
            }
        }
        Collection<ProtectedRegion> app = appRegions.getRegions();
        Iterator<ProtectedRegion> itr = regions.iterator();
        while (itr.hasNext()) {
            final ProtectedRegion region = itr.next();
            if (!app.contains(region)) {
                if (rm.getRegion(region.getId()) != region) {
                    itr.remove();
                    continue;
                }
                RegionLeaveEvent e = new RegionLeaveEvent(region, player, movement, event);
                EpicHeist.getInstance().getServer().getPluginManager().callEvent((Event)e);
                if (e.isCancelled()) {
                    regions.clear();
                    regions.addAll(oldRegions);
                    return true;
                }
                Bukkit.getScheduler().runTaskLater(EpicHeist.getInstance(), new Runnable() {
                    public void run() {
                        RegionLeftEvent e = new RegionLeftEvent(region, player, movement, event);
                        EpicHeist.getInstance().getServer().getPluginManager().callEvent((Event)e);
                    }
                },1L);
                itr.remove();
            }
        }
        this.playerRegions.put(player, regions);
        return false;
    }
}
