package mc.pnternn.epicheist.game.state;

import mc.pnternn.epicheist.config.ConfigurationHandler;
import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import net.minikloon.fsmgasm.StateSwitch;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class StartingState extends GameState {
    public StartingState(Match match) {
        super(match);
    }

    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(5);
    }

    @Override
    public void onUpdate(){
        Bukkit.broadcastMessage("The heist is starting in " + getRemainingDuration().toSeconds() + "seconds");
        if(getRemainingDuration().toSeconds() == 0){
            end();
            StateSwitch stateSwitch = new StateSwitch();
            stateSwitch.changeState(new PlayingState(getMatch()));
        }

    }
    @Override
    public void onEnd() {
    }

    @Override
    protected void onStart() {
    }


}