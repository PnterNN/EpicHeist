package mc.pnternn.epicheist.game;

import mc.pnternn.epicheist.game.state.PlayingState;
import mc.pnternn.epicheist.game.state.StartingState;
import mc.pnternn.epicheist.game.state.SwatState;
import mc.pnternn.epicheist.game.state.WaitingState;
import mc.pnternn.epicheist.task.MatchTaskManager;
import net.minikloon.fsmgasm.StateSeries;
public class Match {
    private final MatchTaskManager matchTaskManager = new MatchTaskManager();
    private final StateSeries stateseries;
    private int day,hour,minute,second;
    private GameState state;
    public Match() {
        stateseries = new StateSeries(
                new WaitingState(this),
                new StartingState(this),
                new PlayingState(this),
                new SwatState(this)
        );
    }
    public void start(){
        stateseries.start();
        matchTaskManager.repeatTask("heart-beat", stateseries::update,20);
    }
    public MatchTaskManager getMatchTaskManager() {
        return matchTaskManager;
    }
    public StateSeries getStateseries() {
        return stateseries;
    }
    public GameState getState() {
        return state;
    }
    public void setState(GameState state) {
        this.state = state;
    }
    public int getDay() {
        return day;
    }
    public void setDay(int day) {
        this.day = day;
    }
    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public int getMinute() {
        return minute;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }
    public int getSecond() {
        return second;
    }
    public void setSecond(int second) {
        this.second = second;
    }
}
