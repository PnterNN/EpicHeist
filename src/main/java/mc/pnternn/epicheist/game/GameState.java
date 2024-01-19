package mc.pnternn.epicheist.game;

import mc.pnternn.epicheist.EpicHeist;
import net.minikloon.fsmgasm.StateSeries;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public abstract class GameState extends StateSeries implements Listener {
    protected final Map<String, Listener> listeners = new HashMap<>();
    private final Match match;

    protected GameState(Match match) {
        super();
        this.match = match;
    }

    @Override
    public final void start() {
        super.start();
        register("self", this);
    }

    @Override
    public final void end() {
        super.end();
        if (!super.getEnded()) return;

        new HashMap<>(listeners).keySet().forEach(this::unregister);
        match.getMatchTaskManager().cancelTask("state-" + hashCode());
    }

    public void delayedTask(String id, Runnable mainRunnable, Runnable cancelRunnable, int delayInTicks) {
        this.match.getMatchTaskManager().delayedTask("state-" + hashCode() + "-" + id, mainRunnable, cancelRunnable, delayInTicks);
    }

    public void delayedTask(String id, Runnable mainRunnable, int delayInTicks) {
        this.match.getMatchTaskManager().delayedTask("state-" + hashCode() + "-" + id, mainRunnable, delayInTicks);
    }

    public void repeatTask(String id, Runnable mainRunnable, Runnable cancelRunnable, int delayInTicks) {
        this.match.getMatchTaskManager().repeatTask("state-" + hashCode() + "-" + id, mainRunnable, cancelRunnable, delayInTicks);
    }

    public void repeatTask(String id, Runnable mainRunnable, int periodInTicks) {
        this.match.getMatchTaskManager().repeatTask("state-" + hashCode() + "-" + id, mainRunnable, periodInTicks);
    }

    public void repeatTask(String id, Runnable mainRunnable, Runnable cancelRunnable, long delayInTicks, long periodInTicks) {
        this.match.getMatchTaskManager().repeatTask("state-" + hashCode() + "-" + id, mainRunnable, cancelRunnable, delayInTicks, periodInTicks);
    }

    public void register(String id, Listener listener) {
        listeners.put(id, listener);
        EpicHeist.getInstance().getServer().getPluginManager().registerEvents(listener, EpicHeist.getInstance());
    }

    public void unregister(String id) {
        Listener listener = listeners.get(id);
        if (listener == null) return;
        listeners.remove(id);
        HandlerList.unregisterAll(listener);
    }

    public Match getMatch() {
        return match;
    }

    public Map<String, Listener> getListeners() {
        return listeners;
    }
}