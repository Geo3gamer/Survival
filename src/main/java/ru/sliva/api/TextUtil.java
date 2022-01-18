package ru.sliva.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static sun.audio.AudioPlayer.player;

public final class TextUtil {

    public static final LegacyComponentSerializer configSerializer = LegacyComponentSerializer.legacy('%');
    public static final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();
    public static final BungeeComponentSerializer bungeeSerializer = BungeeComponentSerializer.legacy();
    public static final LegacyComponentSerializer paragraphSerializer = LegacyComponentSerializer.legacySection();

    private static final LuckPerms luckPerms = LuckPermsProvider.get();

    public static @NotNull Component color(@NotNull Component component) {
        return ampersandSerializer.deserialize(ampersandSerializer.serialize(component));
    }

    public static @NotNull String color(@NotNull String string) {
        return paragraphSerializer.serialize(ampersandSerializer.deserialize(string));
    }

    public static @NotNull String colorConfig(@NotNull String string) {
        return paragraphSerializer.serialize(configSerializer.deserialize(string));
    }

    public static @NotNull Component removeItalics(@NotNull Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static @NotNull Component getDisplayNameSender(@NotNull CommandSender sender) {
        if(sender instanceof Player) {
            return getDisplayName((Player) sender);
        }
        return Component.text(sender.getName());
    }

    public static @Nullable Component getDisplayNameIfOffline(@NotNull OfflinePlayer player) {
        if(player instanceof Player) {
            return getDisplayName((Player) player);
        } else if(player.getName() != null) {
            return Component.text(player.getName());
        }
        return null;
    }

    public static @NotNull Component getDisplayName(@NotNull Player player) {
        return paragraphSerializer.deserialize(player.getDisplayName());
    }

    @Contract(value = "!null -> param1", pure = true)
    public static @NotNull String fromNullable(@Nullable String string) {
        if(string != null) {
            return string;
        }
        return "object not found";
    }
}
