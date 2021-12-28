package ru.sliva.survival;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.sliva.survival.api.Schedule;
import ru.sliva.survival.config.PluginConfig;

public class TabList implements Runnable {

    private final PluginConfig config;

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
        String headerStr = config.getColorizedString("messages.tabHeader").replace("<online>", String.valueOf(online)).replace("<ping>", String.valueOf(ping));
        player.sendPlayerListHeaderAndFooter(Component.text(headerStr), Component.text(" "));
    }
}