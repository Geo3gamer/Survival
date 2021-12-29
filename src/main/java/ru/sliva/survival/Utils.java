package ru.sliva.survival;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.sliva.survival.config.PluginConfig;

import java.util.ArrayList;
import java.util.List;

public final class Utils {

    private static final PluginConfig config = Survival.getInstance().getConfig();
    private static final LuckPerms luckPerms = Survival.getInstance().luckPerms;
    private static final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();

    public static boolean isVanished(Player p) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (!pl.canSee(p)) {
                return true;
            }
        }
        return false;
    }

    public static @NotNull List<Player> getOnlinePlayers() {
        List<Player> list = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!isVanished(p)) {
                list.add(p);
            }
        }
        return list;
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

    public static @NotNull Component getDisplayName(@NotNull CommandSender sender) {
        if(sender instanceof Player) {
            return ((Player) sender).displayName();
        }
        return Component.text(sender.getName());
    }

    public static @NotNull Component constructPlayerIsOffline(@NotNull String name) {
        Component playerIsOffline = ampersandSerializer.deserialize(config.getString("messages.playerIsOffline"));
        playerIsOffline = playerIsOffline.replaceText(builder -> builder.matchLiteral("<player>").replacement(name));
        return playerIsOffline;
    }

    public static @NotNull String stringFromArray(String @NotNull [] array, int start, @NotNull String split) {
        StringBuilder builder = new StringBuilder();
        for(int i = start; i < array.length; i++) {
            builder.append(array[i]);
            builder.append(i < array.length - 1 ? split : "");
        }
        return builder.toString();
    }
}
