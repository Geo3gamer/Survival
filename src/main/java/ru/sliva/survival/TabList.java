package ru.sliva.survival;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.sliva.api.Schedule;
import ru.sliva.api.TextUtil;
import ru.sliva.api.Utils;
import ru.sliva.survival.config.PluginConfig;

public class TabList implements Runnable {

    private final PluginConfig config;
    private final LegacyComponentSerializer configSerializer = TextUtil.configSerializer;

    public TabList(@NotNull Survival plugin) {
        this.config = plugin.getPluginConfig();
        Schedule.timer(this, 20);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerTab(player);
            player.displayName(TextUtil.getDisplayName(player));
            player.playerListName(TextUtil.getTabListName(player));
        }
    }

    public void updatePlayerTab(@NotNull Player player) {
        int ping = player.getPing();
        int online = Utils.getOnlinePlayers().size();
        Component header = configSerializer.deserialize(TextUtil.fromNullable(config.getMessages().node("tabHeader").getString()));
        header = header.replaceText(builder -> builder.matchLiteral("{online}").replacement(String.valueOf(online)));
        header = header.replaceText(builder -> builder.matchLiteral("{ping}").replacement(String.valueOf(ping)));
        player.sendPlayerListHeaderAndFooter(header, Component.text(" "));
    }
}