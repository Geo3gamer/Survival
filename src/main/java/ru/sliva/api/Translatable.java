package ru.sliva.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

public enum Translatable {

    PLAYER_NOT_FOUND("argument.entity.notfound.player", NamedTextColor.RED);

    final TranslatableComponent component;

    Translatable(String key) {
        this.component = Component.translatable(key);
    }

    Translatable(String key, TextColor color) {
        this.component = Component.translatable(key, color);
    }

    public TranslatableComponent getComponent() {
        return component;
    }

    public String getString() {
        return TextUtil.paragraphSerializer.serialize(component);
    }
}
