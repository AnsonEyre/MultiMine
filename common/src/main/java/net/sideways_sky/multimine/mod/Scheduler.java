package net.sideways_sky.multimine.mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;

public class Scheduler {
    private static long ticks = 0L;
    private static final HashMap<Long, List<Task>> tasksMap = new HashMap<>();

    public static Task delay(long delay, @NotNull Task task) {
        Scheduler.addTask(ticks + delay, task);
        return task;
    }

    public static Task delay(long delay, @NotNull Consumer<Task> action) {
        return Scheduler.delay(delay, new Task() {
            @Override
            public void run() {
                action.accept(this);
            }
        });
    }

    public static Task repeat(long delay, long period, @NotNull Task task) {
        task.repeatingPeriod = period;
        Scheduler.addTask(ticks + delay, task);
        return task;
    }

    public static Task repeat(long delay, long period, @NotNull Consumer<Task> action) {
        return Scheduler.repeat(delay, period, new Task() {
            @Override
            public void run() {
                action.accept(this);
            }
        });
    }

    public static void tick() {
        List<Task> tasks = tasksMap.remove(ticks);
        if (tasks != null) {
            for (Task task : tasks) {
                if (task.isCancelled) {
                    continue;
                }
                task.run();
                if (task.isCancelled || task.repeatingPeriod < 1L) {
                    continue;
                }
                Scheduler.addTask(ticks + task.repeatingPeriod, task);
            }
        }
        ++ticks;
    }

    private static void addTask(long time, Task task) {
        List<Task> tasks = tasksMap.get(time);
        if (tasks == null) {
            tasks = new ArrayList<>(1);
            tasks.add(task);
            tasksMap.put(time, tasks);
        } else {
            tasks.add(task);
        }
    }

    public abstract static class Task implements Runnable {
        boolean isCancelled = false;
        long repeatingPeriod = -1L;

        public void cancel() {
            this.isCancelled = true;
        }
    }
}