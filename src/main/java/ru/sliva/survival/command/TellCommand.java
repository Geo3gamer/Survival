package ru.sliva.survival.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.sliva.survival.Survival;
import ru.sliva.survival.Utils;
import ru.sliva.survival.api.Slezhka;
import ru.sliva.survival.config.PluginConfig;

import java.util.Arrays;
import java.util.List;

public class TellCommand extends AbstractCommand {

    private final PluginConfig config;

    private final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();
    private final LegacyComponentSerializer paragraphSerializer = LegacyComponentSerializer.legacySection();

    public TellCommand(@NotNull Survival plugin) {
        super(plugin, "tell", "Отправить игроку личное сообщение", "/tell <игрок> <сообщение>", Arrays.asList("w", "msg"));
        this.config = plugin.getConfig();
    }

    @Override
    public boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if(args.length > 1) {
            Player p = Bukkit.getPlayerExact(args[0]);
            if(p == null) {
                sender.sendMessage(Utils.constructPlayerIsOffline(args[0]));
                return true;
            }
            TextComponent message = paragraphSerializer.deserialize(Utils.stringFromArray(args, 1, " ")).color(NamedTextColor.GRAY);
            if(sender.hasPermission("survival.color")) {
                message = ampersandSerializer.deserialize(ampersandSerializer.serialize(message));
            }
            message = message.hoverEvent(HoverEvent.showText(ampersandSerializer.deserialize(config.getString("hoverEvents.copyPrivateMessage"))));
            message = message.clickEvent(ClickEvent.copyToClipboard(message.content()));
            final TextComponent finalMessage = message;

            Component fromSender = ampersandSerializer.deserialize(config.getString("commands.tell.fromSender"));
            fromSender = fromSender.replaceText(builder -> builder.matchLiteral("<s>").replacement(Utils.getDisplayName(sender).color(NamedTextColor.WHITE)));
            fromSender = fromSender.replaceText(builder -> builder.matchLiteral("<t>").replacement(p.displayName().color(NamedTextColor.WHITE)));
            fromSender = fromSender.replaceText(builder -> builder.matchLiteral("<msg>").replacement(finalMessage));

            Component onTarget = ampersandSerializer.deserialize(config.getString("commands.tell.onTarget"));
            onTarget = onTarget.hoverEvent(HoverEvent.showText(ampersandSerializer.deserialize(config.getString("hoverEvents.reply"))));
            onTarget = onTarget.clickEvent(ClickEvent.suggestCommand("/tell " + sender.getName()));
            onTarget = onTarget.replaceText(builder -> builder.matchLiteral("<s>").replacement(Utils.getDisplayName(sender).color(NamedTextColor.WHITE)));
            onTarget = onTarget.replaceText(builder -> builder.matchLiteral("<t>").replacement(p.displayName().color(NamedTextColor.WHITE)));
            onTarget = onTarget.replaceText(builder -> builder.matchLiteral("<msg>").replacement(finalMessage));

            sender.sendMessage(fromSender);
            p.sendMessage(onTarget);

            Slezhka.send(fromSender);
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
