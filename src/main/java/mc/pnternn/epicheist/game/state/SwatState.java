package mc.pnternn.epicheist.game.state;

import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class SwatState extends GameState {
    public SwatState(Match match) {
        super(match);
    }
    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(Integer.parseInt(ConfigurationHandler.getValue("timer.swat-state.seconds")))
                .plusMinutes(Integer.parseInt(ConfigurationHandler.getValue("timer.swat-state.minutes")))
                .plusHours(Integer.parseInt(ConfigurationHandler.getValue("timer.swat-state.hours")))
                .plusDays(Integer.parseInt(ConfigurationHandler.getValue("timer.swat-state.days")));
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
    protected void onStart() {
        getMatch().setState(this);
    }
    @Override
    public void onEnd() {
    }

}