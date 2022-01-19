package ru.sliva.survival.config;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.api.XMLConfig;

public final class PluginConfig extends XMLConfig {

    private final ConfigurationNode messages;
    private final ConfigurationNode hoverEvents;
    private final ConfigurationNode commands;

    public PluginConfig(@NotNull Plugin plugin) {
        super(plugin, "config.xml");

        ConfigurationNode root = getRoot();
        this.messages = root.node("messages");
        this.hoverEvents = root.node("hoverEvents");
        this.commands = root.node("commands");
    }

    public ConfigurationNode getMessages() {
        return messages;
    }

    public ConfigurationNode getHoverEvents() {
        return hoverEvents;
    }

    public ConfigurationNode getCommands() {
        return commands;
    }

    public ConfigurationNode getCommand(String name) {
        return commands.node(name);
    }
}
