package ru.sliva.api;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.sliva.api.legacy.Audiences;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Slezhka {

    public static final List<UUID> spies = new ArrayList<>();

    private static final Component spying = Component.text("[Слежка] ").color(NamedTextColor.DARK_GRAY);

    public static void send(@NotNull Component component) {
        for(UUID uuid : spies) {
            Player player = Bukkit.getPlayer(uuid);
            if(player != null) {
                Audiences.player(player).sendMessage(component);
            }
        }
    }
}
