package mc.pnternn.epicheist.listeners;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.events.*;
import mc.pnternn.epicheist.game.state.DataHolder;
import mc.pnternn.epicheist.game.state.PlayingState;
import mc.pnternn.epicheist.game.state.SwatState;
import mc.pnternn.epicheist.managers.Crew;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VaultRegionListener implements Listener {
    @EventHandler
    public void onVaultEnter(RegionEnteredEvent event)
    {
        Player player = event.getPlayer();
        String regionName = event.getRegion().getId();
        if(regionName.contains(ConfigurationHandler.getValue("regions.vault-name")))
        {
            if(!EpicHeist.getInstance().getCrewManager().isInCrew(player)){
                EpicHeist.getInstance().getCrewManager().addCrew(new Crew(player, new ArrayList()));
            }
            for(Player p : Bukkit.getOnlinePlayers())
            {
                if(!EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getMembers().contains(p)){
                    player.hidePlayer(p);
                    p.hidePlayer(player);
                }
            }
        }
    }
    @EventHandler
    public void onVaultLeave(RegionLeftEvent event)
    {
        Player player = event.getPlayer();
        String regionName = event.getRegion().getId();
        if(regionName.contains(ConfigurationHandler.getValue("regions.vault-name")))
        {
            for(Player p : Bukkit.getOnlinePlayers())
            {
                player.showPlayer(p);
                p.showPlayer(player);
            }
            if(EpicHeist.getMatch().getDataHolder().getState().getClass().equals(PlayingState.class) ||
                    EpicHeist.getMatch().getDataHolder().getState().getClass().equals(SwatState.class)){
                List<BlockState> doorBlocks = EpicHeist.getMatch().getDataHolder().getDoorBlocks();
                for (BlockState block : doorBlocks) {
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                }
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        File playerFile = new File(EpicHeist.getInstance().getDataFolder()+File.separator+"players"+File.separator+event.getPlayer().getUniqueId()+".yml");
        if(!playerFile.exists()) playerFile.createNewFile();

        Player player = event.getPlayer();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
        if(regions.getRegion(ConfigurationHandler.getValue("regions.vault-name")).
                contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())){
            player.teleport(ConfigurationHandler.getBankLocation());
        }
        if(EpicHeist.getMatch().getDataHolder().getState().getClass().equals(PlayingState.class) ||
                EpicHeist.getMatch().getDataHolder().getState().getClass().equals(SwatState.class)){
            List<BlockState> doorBlocks = EpicHeist.getMatch().getDataHolder().getDoorBlocks();
            for (BlockState block : doorBlocks) {
                player.sendBlockChange(block.getLocation(), block.getBlockData());
            }
        }
    }
}
