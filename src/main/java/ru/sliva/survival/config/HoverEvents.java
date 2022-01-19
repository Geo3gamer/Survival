package ru.sliva.survival.config;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.api.TextUtil;
import ru.sliva.survival.Survival;

public enum HoverEvents {

    copyChatMessage("copyChatMessage"),
    sendMessage("sendMessage"),
    copyPrivateMessage("copyChatMessage"),
    reply("reply");

    private Component component;

    HoverEvents(String key) {
        ConfigurationNode hoverEvents = Survival.getInstance().getPluginConfig().getHoverEvents();
        this.component = TextUtil.configSerializer.deserialize(TextUtil.fromNullable(hoverEvents.node(key).getString()));
    }

    public Component getComponent() {
        return component;
    }

    public @NotNull String getString() {
        return TextUtil.paragraphSerializer.serialize(component);
    }

    public HoverEvents definePlayer(@NotNull Player player) {
        component = component.replaceText(builder -> builder.matchLiteral("{player}").replacement(TextUtil.getDisplayName(player)));
        return this;
    }
}
