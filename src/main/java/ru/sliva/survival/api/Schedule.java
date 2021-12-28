package ru.sliva.survival.api;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import ru.sliva.survival.Survival;

public final class Schedule {

    private static final Plugin plugin = Survival.getInstance();
    private static final BukkitScheduler scheduler = Bukkit.getScheduler();

    @NotNull
    public static BukkitTask later(@NotNull Runnable task, long delay) {
        return scheduler.runTaskLater(plugin, task, delay);
    }

    @NotNull
    public static BukkitTask run(@NotNull Runnable task) {
        return scheduler.runTask(plugin, task);
    }

    @NotNull
    public static BukkitTask runAsync(@NotNull Runnable task) {
        return scheduler.runTaskAsynchronously(plugin, task);
    }

    @NotNull
    public static BukkitTask timer(@NotNull Runnable task, long period, long delay) {
        return scheduler.runTaskTimer(plugin, task, delay, period);
    }

    @NotNull
    public static BukkitTask timer(@NotNull Runnable task, long period) {
        return timer(task, period, 1);
    }

    @NotNull
    public static BukkitTask timerAsync(@NotNull Runnable task, long period, long delay) {
        return scheduler.runTaskTimerAsynchronously(plugin, task, delay, period);
    }

    @NotNull
    public static BukkitTask timerAsync(@NotNull Runnable task, long period) {
        return timerAsync(task, period, 1);
    }

    public static void stopTimer(int taskId) {
        scheduler.cancelTask(taskId);
    }

    public static boolean isRunning(int taskId) {
        return scheduler.isCurrentlyRunning(taskId);
    }
}
