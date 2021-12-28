package ru.sliva.survival;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class Utils {

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
}
