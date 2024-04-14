package mc.pnternn.epicheist.game;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.state.*;
import mc.pnternn.epicheist.task.MatchTaskManager;
import net.minikloon.fsmgasm.StateSeries;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Match {
    private final MatchTaskManager matchTaskManager = new MatchTaskManager();
    private final StateSeries stateseries;
    private DataHolder dataHolder = new DataHolder();
    public Match() {
        stateseries = new StateSeries(
                new WaitingState(this),
                new StartingState(this),
                new PlayingState(this),
                new SwatState(this),
                new EscapingState(this)
        );
    }
    public void start(){
        stateseries.start();
        matchTaskManager.repeatTask("heart-beat", stateseries::update,19);
        if(ConfigurationHandler.getValue("redis.enabled").equals("true")){
            if(ConfigurationHandler.getValue("main-server").equals("true")){
                Bukkit.getScheduler().scheduleSyncDelayedTask(EpicHeist.getInstance(), () -> {
                    JSONObject timerObj = new JSONObject();
                    timerObj.put("type", "TIMER_START");
                    timerObj.put("days", dataHolder.getDay());
                    timerObj.put("hours", dataHolder.getHour());
                    timerObj.put("minutes", dataHolder.getMinute());
                    timerObj.put("seconds", dataHolder.getSecond());
                    EpicHeist.getInstance().getRedisManager().publish(ConfigurationHandler.getValue("redis.channel"), timerObj);
                }, 20);
            }
        }
    }
    public void stop(){
        stateseries.end();
        matchTaskManager.cancelTask("heart-beat");
        if(ConfigurationHandler.getValue("main-server").equals("true")){
            dataHolder.uninstall();
        }
    }

    public List<Player> getVaultPlayers(){
        List<Player> players = new ArrayList<>();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(Objects.requireNonNull(Bukkit.getWorld(ConfigurationHandler.getValue("regions.world-name")))));
        for (Player player: Bukkit.getOnlinePlayers()){
            if(Objects.requireNonNull(Objects.requireNonNull(regions).getRegion(ConfigurationHandler.getValue("regions.vault-name"))).
                    contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())){
                players.add(player);
            }
        }
        return players;
    }

    public MatchTaskManager getMatchTaskManager() {
        return matchTaskManager;
    }
    public StateSeries getStateseries() {
        return stateseries;
    }
    public DataHolder getDataHolder() {
        return dataHolder;
    }

}
