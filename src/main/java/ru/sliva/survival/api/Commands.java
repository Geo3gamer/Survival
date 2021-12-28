package ru.sliva.survival.api;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import ru.sliva.survival.command.AbstractCommand;

import java.util.Map;
import java.util.Objects;

public final class Commands implements Listener {

    private static final SimpleCommandMap commandMap = (SimpleCommandMap) Bukkit.getCommandMap();

    public static void unregisterCommand(@NotNull AbstractCommand cmd) {
        Map<String, Command> knownCommands = commandMap.getKnownCommands();
        knownCommands.remove(cmd.getName());
        for (String alias : cmd.getAliases()) {
            Command command = knownCommands.get(alias);
            if (command instanceof AbstractCommand) {
                AbstractCommand abstractCommand = (AbstractCommand) command;
                if (Objects.equals(abstractCommand.getPlugin(), cmd.getPlugin())) {
                    knownCommands.remove(alias);
                }
            }
        }
        updateCommands();
    }

    public static void registerCommand(AbstractCommand @NotNull ... commands) {
        for(AbstractCommand command : commands) {
            commandMap.register(command.getPlugin().getName(), command);
        }
    }

    public static void unregisterCommands(@NotNull Plugin plugin) {
        for(Command cmd : commandMap.getCommands()) {
            if(cmd instanceof AbstractCommand) {
                AbstractCommand abstractCommand = (AbstractCommand) cmd;
                if(Objects.equals(abstractCommand.getPlugin(), plugin)) {
                    unregisterCommand(abstractCommand);
                }
            }
        }
    }

    private static void updateCommands() {
        Bukkit.getOnlinePlayers().forEach(Player::updateCommands);
    }
}