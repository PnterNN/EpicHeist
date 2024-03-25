package mc.pnternn.epicheist.listeners;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.util.ColorUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class onCommand implements Listener {
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(new BukkitWorld(event.getPlayer().getWorld()));
        ProtectedRegion region = regions.getRegion(ConfigurationHandler.getValue("regions.vault-name"));
        if (region.contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())){
            if(!player.hasPermission("epicheist.admin")){
                event.setCancelled(true);
                player.sendMessage(ColorUtil.colorize(ConfigurationHandler.getValue("prefix") + "&cyou can't use commands inside the vault!"));
            }
        }
    }
}
