package ru.sliva.survival;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.sliva.api.LP;
import ru.sliva.api.Schedule;
import ru.sliva.api.TextUtil;
import ru.sliva.api.Utils;
import ru.sliva.api.legacy.Audiences;
import ru.sliva.survival.config.Messages;

public final class TabList implements Runnable {


    public TabList() {
        Schedule.timer(this, 20);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            updatePlayerTab(player);
            player.setDisplayName(TextUtil.paragraphSerializer.serialize(LP.getDisplayName(player)));
            player.setPlayerListName(TextUtil.paragraphSerializer.serialize(LP.getTabListName(player)));
        }
    }

    public void updatePlayerTab(@NotNull Player player) {
        int ping = player.spigot().getPing();
        int online = Utils.getOnlinePlayers().size();
        Component header = TextUtil.replaceLiteral(
                TextUtil.replaceLiteral(Messages.tabHeader.getComponent(), "{online}", String.valueOf(online)),
                "{ping}", String.valueOf(ping));

        Audiences.player(player).sendPlayerListHeaderAndFooter(header, Component.text(" "));
    }
}