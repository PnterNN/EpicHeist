package mc.pnternn.epicheist.game.state;

import mc.pnternn.epicheist.game.GameState;
import mc.pnternn.epicheist.game.Match;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

public class UninstallState extends GameState {
    public UninstallState(Match match) {
        super(match);
    }



    @Override
    protected void onStart() {
        PlayingState playingState = new PlayingState(getMatch());
        end();
    }

}