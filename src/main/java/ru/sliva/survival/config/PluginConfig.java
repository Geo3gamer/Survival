package ru.sliva.survival.config;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class PluginConfig extends Config {

    public PluginConfig(@NotNull Plugin plugin) {
        super(plugin, "config.yml");
    }
}
