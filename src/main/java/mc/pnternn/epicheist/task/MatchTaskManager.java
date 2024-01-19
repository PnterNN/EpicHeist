package mc.pnternn.epicheist.task;


import java.util.HashMap;
import java.util.Map;

public class MatchTaskManager {

    private final Map<String, MatchTask> tasks = new HashMap<>();

    private void registerTask(MatchTask task) {
        tasks.put(task.getTaskName(), task);
    }

    public void repeatTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long delayInTicks, long periodInTicks) {
        registerTask(new MatchTask(taskName, mainRunnable, cancelRunnable, delayInTicks, periodInTicks));
    }

    public void repeatTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long periodInTicks) {
        repeatTask(taskName, mainRunnable, cancelRunnable, 0, periodInTicks);
    }

    public void repeatTask(String taskName, Runnable mainRunnable, long periodInTicks) {
        repeatTask(taskName, mainRunnable, () -> {
        }, 0, periodInTicks);
    }

    public void delayedTask(String taskName, Runnable mainRunnable, Runnable cancelRunnable, long delayInTicks) {
        registerTask(new MatchTask(taskName, mainRunnable, cancelRunnable, delayInTicks));
    }

    public void delayedTask(String taskName, Runnable mainRunnable, long delayInTicks) {
        delayedTask(taskName, mainRunnable, () -> {
        }, delayInTicks);
    }

    public void cancelTask(String prefix) {
        tasks.forEach((taskName, task) -> {
            if (taskName.startsWith(prefix)) {
                task.cancel();
            }
        });
    }

    public void cancelTasks() {
        tasks.values().forEach(MatchTask::cancel);
    }
}
