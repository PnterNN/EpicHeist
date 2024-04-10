package mc.pnternn.epicheist.game.state;

import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import mc.pnternn.epicheist.util.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class StartingState extends GameState {
    public StartingState(Match match) {
        super(match);
    }
    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(Integer.parseInt(ConfigurationHandler.getValue("timer.starting-state.seconds")))
                .plusMinutes(Integer.parseInt(ConfigurationHandler.getValue("timer.starting-state.minutes")))
                .plusHours(Integer.parseInt(ConfigurationHandler.getValue("timer.starting-state.hours")))
                .plusDays(Integer.parseInt(ConfigurationHandler.getValue("timer.starting-state.days")));
    }

    @Override
    public void onUpdate(){
        getMatch().getDataHolder().day = ((int) getRemainingDuration().toDaysPart());
        getMatch().getDataHolder().hour = (getRemainingDuration().toHoursPart());
        getMatch().getDataHolder().minute = (getRemainingDuration().toMinutesPart());
        getMatch().getDataHolder().second = (getRemainingDuration().toSecondsPart());
        if(getRemainingDuration().toSeconds() == 0){
            getMatch().getStateseries().skip();
        }
    }
    @Override
    public void onEnd() {
    }

    @Override
    protected void onStart() {
        getMatch().getDataHolder().state = this;
        if(Bukkit.getOnlinePlayers().size() < 1){
            Bukkit.broadcastMessage(ConfigurationHandler.getValue("prefix") + ConfigurationHandler.getValue("messages.not-enough-players"));
            EpicHeist.setMatch(new Match());
            EpicHeist.getMatch().start();
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), ConfigurationHandler.getValue("musics.starting-state"), 1, 1);
            ColorUtil.showTitle(player,
                    ConfigurationHandler.getValue("animated-titles.starting-state.background-color"),
                    ConfigurationHandler.getValue("animated-titles.starting-state.title-color"),
                    ConfigurationHandler.getValue("animated-titles.starting-state.title"),
                    ConfigurationHandler.getValue("animated-titles.starting-state.subtitle"));
        }
    }


}