package ru.sliva.survival.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sliva.api.TextUtil;
import ru.sliva.api.Translatable;
import ru.sliva.api.Utils;
import ru.sliva.api.command.AbstractCommand;
import ru.sliva.api.legacy.Audiences;
import ru.sliva.survival.Survival;
import ru.sliva.survival.config.PluginConfig;

import java.util.List;

public class SudoCommand extends AbstractCommand {

    private final PluginConfig config;

    private final LegacyComponentSerializer configSerializer = TextUtil.configSerializer;

    public SudoCommand(@NotNull Survival plugin) {
        super(plugin, "sudo", "Выполнить команду от имени игрока", "/sudo <игрок> </команда>", "survival.sudo");
        this.config = plugin.getPluginConfig();
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

            Component executed = configSerializer.deserialize(TextUtil.fromNullable(config.getCommand("sudo").node("executed").getString()));
            executed = executed.replaceText(builder -> builder.matchLiteral("{player}").replacement(TextUtil.getDisplayName(player)));
            executed = executed.replaceText(builder -> builder.matchLiteral("{command}").replacement(Component.text(command).color(NamedTextColor.GRAY)));

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
