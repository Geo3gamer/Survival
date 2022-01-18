package ru.sliva.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.cacheddata.CachedMetaData;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;

public final class LP {

    static {
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
    }

    private static LuckPerms luckPerms;
    private static final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();

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
}
