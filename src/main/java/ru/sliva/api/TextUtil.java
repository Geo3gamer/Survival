package ru.sliva.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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

public final class TextUtil {

    public static final LegacyComponentSerializer configSerializer = LegacyComponentSerializer.legacy('%');
    public static final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();
    private static final LuckPerms luckPerms = LuckPermsProvider.get();

    public static @NotNull Component color(@NotNull Component component) {
        return ampersandSerializer.deserialize(ampersandSerializer.serialize(component));
    }

    public static @NotNull Component removeItalics(@NotNull Component component) {
        return component.decoration(TextDecoration.ITALIC, false);
    }

    public static @NotNull Component getDisplayName(@NotNull CommandSender sender) {
        if(sender instanceof Player) {
            return ((Player) sender).displayName();
        }
        return Component.text(sender.getName());
    }

    public static @Nullable Component getDisplayName(@NotNull OfflinePlayer player) {
        if(player instanceof Player) {
            return ((Player) player).displayName();
        } else if(player.getName() != null) {
            return Component.text(player.getName());
        }
        return null;
    }

    public static @NotNull Component getDisplayName(@NotNull Player player) {
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        CachedMetaData metaData = user.getCachedData().getMetaData();
        String prefix = metaData.getPrefix();
        Component formattedPrefix = Component.text("");
        if(prefix != null) {
            formattedPrefix = ampersandSerializer.deserialize(prefix);
        }
        return formattedPrefix.append(Component.text(player.getName()));
    }

    public static @NotNull Component getTabListName(@NotNull Player player) {
        User user = luckPerms.getPlayerAdapter(Player.class).getUser(player);
        CachedMetaData metaData = user.getCachedData().getMetaData();
        String prefix = metaData.getPrefix();
        Component formattedPrefix = Component.text("");
        if(prefix != null) {
            formattedPrefix = ampersandSerializer.deserialize(prefix);
        }
        String suffix = metaData.getSuffix();
        Component formattedSuffix = Component.text("");
        if(suffix != null) {
            formattedSuffix = ampersandSerializer.deserialize(suffix);
        }
        return formattedPrefix.append(Component.text(player.getName())).append(formattedSuffix);
    }

    @Contract(value = "!null -> param1", pure = true)
    public static @NotNull String fromNullable(@Nullable String string) {
        if(string != null) {
            return string;
        }
        return "object not found";
    }
}
