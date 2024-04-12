package mc.pnternn.epicheist.game.state;

import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.util.ColorUtil;
import mc.pnternn.epicheist.util.RegionBlockIteration;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.Random;

public class PlayingState extends GameState {
    public PlayingState(Match match) {
        super(match);
    }
    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(Integer.parseInt(ConfigurationHandler.getValue("timer.playing-state.seconds")))
                .plusMinutes(Integer.parseInt(ConfigurationHandler.getValue("timer.playing-state.minutes")))
                .plusHours(Integer.parseInt(ConfigurationHandler.getValue("timer.playing-state.hours")))
                .plusDays(Integer.parseInt(ConfigurationHandler.getValue("timer.playing-state.days")));
    }

    @Override
    public void onUpdate() {
        getMatch().getDataHolder().day = ((int) getRemainingDuration().toDaysPart());
        getMatch().getDataHolder().hour = (getRemainingDuration().toHoursPart());
        getMatch().getDataHolder().minute = (getRemainingDuration().toMinutesPart());
        getMatch().getDataHolder().second = (getRemainingDuration().toSecondsPart());

        if (getRemainingDuration().toSeconds() == 0) {
            getMatch().getStateseries().skip();
        }
    }

    @Override
    public void onEnd(){
    }
    @Override
    protected void onStart() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), ConfigurationHandler.getValue("musics.playing-state"), 1, 1);
            ColorUtil.showTitle(player,
                    ConfigurationHandler.getValue("animated-titles.playing-state.background-color"),
                    ConfigurationHandler.getValue("animated-titles.playing-state.title-color"),
                    ConfigurationHandler.getValue("animated-titles.playing-state.title"),
                    ConfigurationHandler.getValue("animated-titles.playing-state.subtitle"));
        }
        getMatch().getDataHolder().state = this;
        if(ConfigurationHandler.getValue("main-server").equals("true")){
            EpicHeist.getInstance().getProtocolManager().addPacketListener(getMatch().getDataHolder().goldBreakEvent);
            RegionBlockIteration regionBlockIteration = new RegionBlockIteration();
            for (Location location : regionBlockIteration.getRegionBlocks(ConfigurationHandler.getValue("regions.world-name"), ConfigurationHandler.getValue("regions.vault-name"))) {
                if(location.getBlock().getType() == Material.getMaterial(ConfigurationHandler.getValue("gold.small-gold.block"))){
                    Random random = new Random();
                    if(random.nextInt(0,100)<Integer.parseInt(ConfigurationHandler.getValue("gold.big-gold.chance"))){
                        getMatch().getDataHolder().specialBlocks.add(location.getBlock().getState());
                        getMatch().getDataHolder().goldBlocks.add(location.getBlock().getState());
                    }else{
                        getMatch().getDataHolder().goldBlocks.add(location.getBlock().getState());
                    }
                    location.getBlock().setType(Material.AIR);
                }
            }
            for (Location location : regionBlockIteration.getRegionBlocks(ConfigurationHandler.getValue("regions.world-name"), ConfigurationHandler.getValue("regions.entrance-door-name"))) {;
                getMatch().getDataHolder().entranceDoorBlocks.add(location.getBlock().getState());
                location.getBlock().setType(Material.AIR);
                Bukkit.getWorld(ConfigurationHandler.getValue("regions.world-name")).createExplosion(location, 0F, false, false);
            }
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(EpicHeist.getInstance(), () -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    for(BlockState block : getMatch().getDataHolder().goldBlocks){
                        player.sendBlockChange(block.getLocation(), Material.getMaterial(ConfigurationHandler.getValue("gold.small-gold.block")).createBlockData());
                    }
                    for(BlockState block : getMatch().getDataHolder().specialBlocks){
                        player.sendBlockChange(block.getLocation(), Material.getMaterial(ConfigurationHandler.getValue("gold.big-gold.block")).createBlockData());
                    }
                }
            }, 5L);
        }
    }
}
