package ru.sliva.api;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.sliva.api.command.Commands;
import ru.sliva.api.legacy.Audiences;

public final class API {

    public static void setup(@NotNull Plugin plugin) {
        Schedule.setup(plugin);
        Events.setup(plugin);
        Commands.setup(plugin);
        Audiences.setup(plugin);
    }
}
