package ru.sliva.api.legacy;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public final class Audiences {

    private static BukkitAudiences audiences;

    public static void setup(@NotNull Plugin plugin) {
        if(audiences == null) {
            audiences = BukkitAudiences.create(plugin);
        }
    }

    public static void close() {
        audiences.close();
    }

    public static @NotNull Audience player(@NotNull Player player) {
        return audiences.player(player);
    }

    public static @NotNull Audience sender(@NotNull CommandSender sender) {
        return audiences.sender(sender);
    }
}
