package ru.sliva.survival.config;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ru.sliva.api.TextUtil;
import ru.sliva.survival.Survival;

public enum Cmds {

    tell_fromSender("tell", "fromSender"),
    tell_toTarget("tell", "toTarget"),
    slezhka_enabled("slezhka", "enabled"),
    slezhka_disabled("slezhka", "disabled"),
    slezhka_kick("slezhka", "kick"),
    slezhka_command("slezhka", "command"),
    sudo_executed("sudo", "executed");

    private Component component;

    Cmds(String node, String key) {
        component = TextUtil.configSerializer.deserialize(TextUtil.fromNullable(Survival.getInstance().getPluginConfig().getCommand(node).node(key).getString()));
    }

    public Component getComponent() {
        return component;
    }

    public @NotNull String getString() {
        return TextUtil.paragraphSerializer.serialize(component);
    }

    public Cmds defineSender(@NotNull CommandSender sender) {
        component = TextUtil.replaceLiteral(component, "{sender}", TextUtil.getDisplayNameSender(sender));
        return this;
    }

    public Cmds defineTarget(@NotNull CommandSender target) {
        component = TextUtil.replaceLiteral(component, "{target}", TextUtil.getDisplayNameSender(target));
        return this;
    }

    public Cmds defineMessage(@NotNull Component message) {
        component = TextUtil.replaceLiteral(component, "{message}", message);
        return this;
    }

    public Cmds defineCommand(@NotNull String command) {
        component = TextUtil.replaceLiteral(component, "{command}", command);
        return this;
    }
}
