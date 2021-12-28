package ru.sliva.survival;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.sliva.survival.api.Schedule;
import ru.sliva.survival.config.PluginConfig;

public class TabList implements Runnable {

    private final PluginConfig config;
    private final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();

    public TabList(@NotNull Survival plugin) {
        this.config = plugin.getConfig();
        Schedule.timer(this, 20);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerTab(player);
        }
    }

    public void updatePlayerTab(@NotNull Player player) {
        int ping = player.getPing();
        int online = Utils.getOnlinePlayers().size();
        Component header = ampersandSerializer.deserialize(config.getString("messages.tabHeader"));
        header = header.replaceText(builder -> builder.match("<online>").replacement(String.valueOf(online)));
        header = header.replaceText(builder -> builder.match("<ping>").replacement(String.valueOf(ping)));
        player.sendPlayerListHeaderAndFooter(header, Component.text(" "));
    }
}