package mc.pnternn.epicheist.listeners;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import mc.obliviate.util.string.StringUtil;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.events.*;
import mc.pnternn.epicheist.game.state.EscapingState;
import mc.pnternn.epicheist.game.state.PlayingState;
import mc.pnternn.epicheist.game.state.SwatState;
import mc.pnternn.epicheist.managers.Crew;
import mc.pnternn.epicheist.util.ColorUtil;
import mc.pnternn.epicheist.util.RegionBlockIteration;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HeistListeners implements Listener {
    @EventHandler
    public void onDeath(EntityDeathEvent e){
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(e.getEntity().getWorld()));
        if(e.getEntity().getCustomName() !=null){
            if(regions != null){
                if(regions.getRegion(ConfigurationHandler.getValue("regions.vault-name")).
                        contains(e.getEntity().getLocation().getBlockX(), e.getEntity().getLocation().getBlockY(), e.getEntity().getLocation().getBlockZ())
                        && e.getEntity().getType().equals(EntityType.ZOMBIE) && e.getEntity().getCustomName().equals(StringUtil.parseColor(ConfigurationHandler.getValue("swat.name")))){
                    e.getDrops().clear();
                }
            }
        }
    }
    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e){
        if(e.getEntity().getType() == EntityType.PLAYER){
            Player player = (Player) e.getEntity();
            RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
            RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
            if(regions != null){
                if(regions.getRegion(ConfigurationHandler.getValue("regions.vault-name")).
                        contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())
                        && e.getDamager().getType().equals(EntityType.ZOMBIE) && e.getDamager().getCustomName().equals(StringUtil.parseColor(ConfigurationHandler.getValue("swat.name")))){
                    e.setCancelled(true);
                    player.teleport(ConfigurationHandler.getJailLocation());
                    player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 99, 2));
                    double gold = EpicHeist.getMatch().getDataHolder().getCollectedGold().getOrDefault(player.getUniqueId(), 0.0)/2;
                    double money = EpicHeist.getMatch().getDataHolder().getCollectedMoney().getOrDefault(player.getUniqueId(), 0.0)/2;
                    EpicHeist.getMatch().getDataHolder().getCollectedMoney().put(player.getUniqueId(), EpicHeist.getMatch().getDataHolder().getCollectedMoney().getOrDefault(player.getUniqueId(), 0.0) -money);
                    EpicHeist.getMatch().getDataHolder().getCollectedGold().put(player.getUniqueId(), EpicHeist.getMatch().getDataHolder().getCollectedGold().getOrDefault(player.getUniqueId(), 0.0) -gold);
                    EpicHeist.getMatch().getDataHolder().getCrewCollectedGold().put(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getId(), EpicHeist.getMatch().getDataHolder().getCrewCollectedGold().getOrDefault(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getId(), 0.0)- gold);
                    EpicHeist.getMatch().getDataHolder().getCrewCollectedMoney().put(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getId(), EpicHeist.getMatch().getDataHolder().getCrewCollectedMoney().getOrDefault(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getId(), 0.0)- money);
                    EpicHeist.getEconomy().bankWithdraw("vault", money);
                    ColorUtil.showTitle(player,
                            ConfigurationHandler.getValue("animated-titles.catch-title.background-color"),
                            ConfigurationHandler.getValue("animated-titles.catch-title.title-color"),
                            ConfigurationHandler.getValue("animated-titles.catch-title.title"),
                            ConfigurationHandler.getValue("animated-titles.catch-title.subtitle"));
                    Zombie swat = (Zombie) e.getDamager();
                    swat.setHealth(0);
                }
            }
        }
    }

    @EventHandler
    public void onTeleportRegionEnter(RegionEnteredEvent event){
        Player player = event.getPlayer();
        String regionName = event.getRegion().getId();
        if(regionName.contains(ConfigurationHandler.getValue("regions.teleport-region")))
        {
            if(EpicHeist.getMatch().getDataHolder().getState().getClass().equals(PlayingState.class) ||
               EpicHeist.getMatch().getDataHolder().getState().getClass().equals(SwatState.class) ||
               EpicHeist.getMatch().getDataHolder().getState().getClass().equals(EscapingState.class)){
                    player.teleport(ConfigurationHandler.getBankLocation());
                    ColorUtil.showTitle(player,
                        ConfigurationHandler.getValue("animated-titles.successfully-escaped.background-color"),
                        ConfigurationHandler.getValue("animated-titles.successfully-escaped.title-color"),
                        ConfigurationHandler.getValue("animated-titles.successfully-escaped.title"),
                        ConfigurationHandler.getValue("animated-titles.successfully-escaped.subtitle"));
                    if(EpicHeist.getMatch().getDataHolder().getPlayerSwats().get(player.getUniqueId()) != null){
                        for(Zombie swat : EpicHeist.getMatch().getDataHolder().getPlayerSwats().get(player.getUniqueId())){
                            swat.setHealth(0);
                        }
                    }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
        if(regions != null){
            if(EpicHeist.getMatch().getDataHolder().getState().getClass().equals(PlayingState.class) ||
               EpicHeist.getMatch().getDataHolder().getState().getClass().equals(SwatState.class) ||
               EpicHeist.getMatch().getDataHolder().getState().getClass().equals(EscapingState.class)){
                if(regions.getRegion(ConfigurationHandler.getValue("regions.vault-name")).
                        contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())){
                    if(player.getLocation().getBlockY() < RegionBlockIteration.getVaultRegionMinimumY() + 1){
                        player.teleport(ConfigurationHandler.getBankLocation());
                        ColorUtil.showTitle(player,
                                ConfigurationHandler.getValue("animated-titles.catch-title.background-color"),
                                ConfigurationHandler.getValue("animated-titles.catch-title.title-color"),
                                ConfigurationHandler.getValue("animated-titles.catch-title.title"),
                                ConfigurationHandler.getValue("animated-titles.catch-title.subtitle"));
                    }
                }
            }
        }
    }


    @EventHandler
    public void onVaultEnter(RegionEnteredEvent event)
    {
        Player player = event.getPlayer();
        String regionName = event.getRegion().getId();
        if(regionName.contains(ConfigurationHandler.getValue("regions.vault-name")))
        {
            if(!EpicHeist.getInstance().getCrewManager().isInCrew(player)){
                player.sendMessage(StringUtil.parseColor("&cSince there is no crew, a new crew was created."));
                EpicHeist.getInstance().getCrewManager().addCrew(new Crew(player, new ArrayList()));
            }
            boolean isInvisible;
            for(Player p : Bukkit.getOnlinePlayers())
            {
                isInvisible = true;
                if(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getLeader().equals(p)){
                    isInvisible = false;
                }
                if(EpicHeist.getInstance().getCrewManager().getCrewByPlayer(player).getMembers().contains(p)){
                    isInvisible = false;
                }
                if(isInvisible){
                    p.hidePlayer(player);

                    if(EpicHeist.getMatch().getVaultPlayers().contains(p)){
                        player.hidePlayer(p);
                    }
                }else{
                    if(EpicHeist.getMatch().getVaultPlayers().contains(p)) {
                        player.setGlowing(true);
                    }
                    p.setGlowing(true);
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
                p.showPlayer(player);
                if(!EpicHeist.getMatch().getVaultPlayers().contains(p)){
                    player.showPlayer(p);
                }
                if(!EpicHeist.getMatch().getVaultPlayers().contains(p)) {
                    p.setGlowing(false);
                }
                player.setGlowing(false);
            }
            if(EpicHeist.getMatch().getDataHolder().getState().getClass().equals(PlayingState.class) ||
                    EpicHeist.getMatch().getDataHolder().getState().getClass().equals(SwatState.class) ||
                    EpicHeist.getMatch().getDataHolder().getState().getClass().equals(EscapingState.class)){
                List<BlockState> doorBlocks = EpicHeist.getMatch().getDataHolder().getEntranceDoorBlocks();
                for (BlockState block : doorBlocks) {
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                }
                if(EpicHeist.getMatch().getDataHolder().getPlayerSwats().get(player.getUniqueId()) != null){
                    for(Zombie swat : EpicHeist.getMatch().getDataHolder().getPlayerSwats().get(player.getUniqueId())){
                        swat.setHealth(0);

                    }

                }
            }
        }
    }
    @EventHandler
    public void onPlayerLeft(PlayerQuitEvent event){
        if(EpicHeist.getMatch().getDataHolder().getPlayerSwats().get(event.getPlayer().getUniqueId()) != null){
            for(Zombie swat : EpicHeist.getMatch().getDataHolder().getPlayerSwats().get(event.getPlayer().getUniqueId())){
                swat.setHealth(0);
            }
        }
    }
    @EventHandler
    public void onSwatPresureplate(EntityInteractEvent event){
        if(event.getEntity().getType().equals(EntityType.ZOMBIE)){
            Zombie swat = (Zombie) event.getEntity();
            if(swat.getCustomName().equals(StringUtil.parseColor(ConfigurationHandler.getValue("swat.name")))){
                event.getEntity().setVelocity(event.getEntity().getLocation().getDirection().multiply(1).setY(2));
            }
        }
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        Player player = event.getPlayer();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
        if(regions != null){
            if(regions.getRegion(ConfigurationHandler.getValue("regions.vault-name")).
                    contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())){
                player.teleport(ConfigurationHandler.getBankLocation());
            }
            if(EpicHeist.getMatch().getDataHolder().getState().getClass().equals(PlayingState.class) ||
                    EpicHeist.getMatch().getDataHolder().getState().getClass().equals(SwatState.class) ||
                    EpicHeist.getMatch().getDataHolder().getState().getClass().equals(EscapingState.class)){
                List<BlockState> doorBlocks = EpicHeist.getMatch().getDataHolder().getEntranceDoorBlocks();
                for (BlockState block : doorBlocks) {
                    player.sendBlockChange(block.getLocation(), block.getBlockData());
                }
            }
        }
    }
}
