package ru.sliva.api;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.jetbrains.annotations.NotNull;

public final class Events {

    private static Plugin plugin;
    private static final SimplePluginManager pluginManager = (SimplePluginManager) Bukkit.getPluginManager();

    public static void setup(@NotNull Plugin plugin) {
        if(Events.plugin == null) {
            Events.plugin = plugin;
        }
    }

    public static void registerListener(@NotNull Listener listener) {
        pluginManager.registerEvents(listener, plugin);
    }

    public static void unregisterListener(@NotNull Listener listener) {
        HandlerList.unregisterAll(listener);
    }
}
