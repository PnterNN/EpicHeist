package mc.pnternn.epicheist.game.state;

import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class WaitingState extends GameState {
    public WaitingState(Match match) {
        super(match);
    }
    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(Integer.parseInt(ConfigurationHandler.getValue("timer.waiting-state.seconds")))
                .plusMinutes(Integer.parseInt(ConfigurationHandler.getValue("timer.waiting-state.minutes")))
                .plusHours(Integer.parseInt(ConfigurationHandler.getValue("timer.waiting-state.hours")))
                .plusDays(Integer.parseInt(ConfigurationHandler.getValue("timer.waiting-state.days")));
    }

    @Override
    public void onUpdate(){
        getMatch().setDay((int) getRemainingDuration().toDaysPart());
        getMatch().setHour(getRemainingDuration().toHoursPart());
        getMatch().setMinute(getRemainingDuration().toMinutesPart());
        getMatch().setSecond(getRemainingDuration().toSecondsPart());
        if(getRemainingDuration().toSeconds() == 0){
            getMatch().getStateseries().skip();
        }
    }
    @Override
    public void onEnd() {
    }

    @Override
    protected void onStart() {
        getMatch().setState(this);
    }
}
