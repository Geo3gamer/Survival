package ru.sliva.survival.config;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.api.TextUtil;
import ru.sliva.survival.Survival;

public enum Messages {

    join("join"),
    quit("quit"),
    motd("motd"),
    listPlayers("listPlayers"),
    chatFormat("chatFormat"),
    nobodyHeard("nobodyHeard"),
    unknownCommand("unknownCommand"),
    tabHeader("tabHeader");

    private Component component;

    Messages(String key) {
        ConfigurationNode messages = Survival.getInstance().getPluginConfig().getMessages();
        this.component = TextUtil.configSerializer.deserialize(TextUtil.fromNullable(messages.node(key).getString()));
    }

    public Component getComponent() {
        return component;
    }

    public @NotNull String getString() {
        return TextUtil.paragraphSerializer.serialize(component);
    }

    public Messages definePlayer(@NotNull Player player) {
        component = TextUtil.replaceLiteral(component, "{player}", TextUtil.getDisplayName(player));
        return this;
    }
}
