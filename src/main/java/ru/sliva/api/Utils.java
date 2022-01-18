package ru.sliva.api;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class Utils {

    public static boolean isRgb(int protocolVersion) {
        return protocolVersion > 734;
    }

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

    public static @NotNull String stringFromArray(String @NotNull [] array, int start, @NotNull String split) {
        StringBuilder builder = new StringBuilder();
        for(int i = start; i < array.length; i++) {
            builder.append(array[i]);
            builder.append(i < array.length - 1 ? split : "");
        }
        return builder.toString();
    }

    public static @Nullable OfflinePlayer getOfflinePlayerIfCached(@NotNull UUID uuid) {
        OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
        if(player.hasPlayedBefore() && player.getName() != null) {
            return player;
        }
        return null;
    }

    @Contract("_ -> new")
    public static <T> @NotNull List<T> setToList(Set<T> set) {
        return new ArrayList<>(set);
    }
}
