package ru.sliva.survival.config;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class PlayersConfig extends Config {

    public PlayersConfig(@NotNull Plugin plugin) {
        super(plugin, "players.yml");
        ConfigurationSerialization.registerClass(SPlayer.class);
    }

    public SPlayer getPlayer(String path, SPlayer def) {
        return getSerializable(path, SPlayer.class, def);
    }

    public SPlayer getPlayer(String path) {
        return getSerializable(path, SPlayer.class);
    }
}
