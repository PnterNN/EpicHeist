package mc.pnternn.epicheist.events;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import mc.pnternn.epicheist.util.MovementWay;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public class RegionEnteredEvent extends RegionEvent {
    public RegionEnteredEvent(ProtectedRegion region, Player player, MovementWay movement, PlayerEvent parent) {
        super(region, player, movement, parent);
    }
}