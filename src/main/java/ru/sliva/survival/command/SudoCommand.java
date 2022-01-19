package ru.sliva.survival.command;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sliva.api.Translatable;
import ru.sliva.api.Utils;
import ru.sliva.api.command.AbstractCommand;
import ru.sliva.api.legacy.Audiences;
import ru.sliva.survival.Survival;
import ru.sliva.survival.config.Cmds;

import java.util.List;

public final class SudoCommand extends AbstractCommand {

    public SudoCommand(@NotNull Survival plugin) {
        super(plugin, "sudo", "Выполнить команду от имени игрока", "/sudo <игрок> </команда>", "survival.sudo");
    }

    @Override
    public boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if(args.length > 1) {
            Player player = Bukkit.getPlayerExact(args[0]);
            if(player == null) {
                sender.sendMessage(Translatable.PLAYER_NOT_FOUND.getString());
                return true;
            }
            String command = Utils.stringFromArray(args, 1, " ");

            Component executed = Cmds.sudo_executed.defineTarget(player).defineCommand(command).getComponent();

            player.chat(command);

            Audiences.sender(sender).sendMessage(executed);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> complete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String @NotNull [] args) {
        if(args.length == 1) {
            return playerComplete(sender, alias, args);
        }
        return null;
    }
}
