package mc.pnternn.epicheist.game.state;

import mc.pnternn.epicheist.EpicHeist;
import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.time.*;
import java.time.temporal.TemporalAdjusters;

public class WaitingState extends GameState {
    LocalDateTime startingTime = LocalDateTime.now();
    public WaitingState(Match match) {
        super(match);
    }
    @NotNull
    @Override
    public Duration getDuration() {
        switch (ConfigurationHandler.getValue("timer.waiting-state.loop")){
            case "EVERYDAY" ->{
                LocalDateTime timePeriod = LocalDateTime.of(LocalDate.now(), LocalTime.parse(ConfigurationHandler.getValue("timer.waiting-state.time")));
                if(LocalDateTime.now().isAfter(timePeriod)){
                    timePeriod = timePeriod.plusDays(1);
                }
                return Duration.between(startingTime, timePeriod);
            }case "EVERYWEEK" -> {
                LocalDate week = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.of(Integer.parseInt(ConfigurationHandler.getValue("timer.waiting-state.week-day")))));
                LocalDateTime timePeriod = LocalDateTime.of(week, LocalTime.parse(ConfigurationHandler.getValue("timer.waiting-state.time")));
                if (timePeriod.isBefore(LocalDateTime.now())) {
                    timePeriod = timePeriod.plusWeeks(1);
                }
                return Duration.between(startingTime, timePeriod);
            }case "EVERYMONTH" ->{
                LocalDate month = LocalDate.now().withDayOfMonth(Integer.parseInt(ConfigurationHandler.getValue("timer.waiting-state.month-day")));
                LocalDateTime timePeriod = LocalDateTime.of(month, LocalTime.parse(ConfigurationHandler.getValue("timer.waiting-state.time")));
                if (timePeriod.isBefore(LocalDateTime.now())) {
                    timePeriod = timePeriod.plusMonths(1);
                }
                return Duration.between(startingTime, timePeriod);
            }case "SPECIFIC" ->{
                return Duration.ofSeconds(Integer.parseInt(ConfigurationHandler.getValue("timer.waiting-state.specific.seconds")))
                        .plusMinutes(Integer.parseInt(ConfigurationHandler.getValue("timer.waiting-state.specific.minutes")))
                        .plusHours(Integer.parseInt(ConfigurationHandler.getValue("timer.waiting-state.specific.hours")))
                        .plusDays(Integer.parseInt(ConfigurationHandler.getValue("timer.waiting-state.specific.days")));
            }
        }
        return Duration.ZERO;
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
    }
}
