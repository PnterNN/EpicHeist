package mc.pnternn.epicheist.game;

import mc.pnternn.epicheist.game.state.PlayingState;
import mc.pnternn.epicheist.game.state.StartingState;
import mc.pnternn.epicheist.game.state.UninstallState;
import mc.pnternn.epicheist.task.MatchTaskManager;
import net.minikloon.fsmgasm.StateSeries;


public class Match {
    private final MatchTaskManager matchTaskManager = new MatchTaskManager();

    private final StateSeries stateseries;

    public Match() {
        stateseries = new StateSeries(
                new StateSeries(
                        new StartingState(this),
                        new PlayingState(this)),
                new UninstallState(this)
        );
    }
    public void start(){
        stateseries.start();
        matchTaskManager.repeatTask("heart-beat", stateseries::update,20);
    }

    public MatchTaskManager getMatchTaskManager() {
        return matchTaskManager;

    }

}
