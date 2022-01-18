package ru.sliva.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Slezhka {

    public static final List<UUID> spies = new ArrayList<>();

    private static final Component spying = Component.text("[Слежка] ").color(NamedTextColor.DARK_GRAY);

    public static void send(@NotNull Component component) {
        for(UUID uuid : spies) {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null) {
                p.sendMessage(spying.append(component));
            }
        }
    }
}
