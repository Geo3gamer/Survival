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
import ru.sliva.api.Slezhka;
import ru.sliva.api.TextUtil;
import ru.sliva.api.Translatable;
import ru.sliva.api.Utils;
import ru.sliva.api.command.AbstractCommand;
import ru.sliva.api.legacy.Audiences;
import ru.sliva.survival.Survival;
import ru.sliva.survival.config.Cmds;
import ru.sliva.survival.config.HoverEvents;

import java.util.Arrays;
import java.util.List;

public final class TellCommand extends AbstractCommand {

    private final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();
    private final LegacyComponentSerializer paragraphSerializer = LegacyComponentSerializer.legacySection();

    public TellCommand(@NotNull Survival plugin) {
        super(plugin, "tell", "Отправить игроку личное сообщение", "/tell <игрок> <сообщение>", Arrays.asList("w", "msg"));
    }

    @Override
    public boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if(args.length > 1) {
            Player player = Bukkit.getPlayerExact(args[0]);
            if(player == null) {
                sender.sendMessage(Translatable.PLAYER_NOT_FOUND.getString());
                return true;
            }

            TextComponent message = paragraphSerializer.deserialize(Utils.stringFromArray(args, 1, " ")).color(NamedTextColor.GRAY);
            if(sender.hasPermission("survival.color")) {
                message = ampersandSerializer.deserialize(ampersandSerializer.serialize(message));
            }
            message = (TextComponent) TextUtil.removeBadWords(message);
            message = message.hoverEvent(HoverEvent.showText(HoverEvents.copyPrivateMessage.getComponent()));
            message = message.clickEvent(ClickEvent.copyToClipboard(message.content()));
            final TextComponent finalMessage = message;

            Component fromSender = Cmds.tell_fromSender.defineSender(sender).defineTarget(player).defineMessage(finalMessage).getComponent();

            Component onTarget = Cmds.tell_onTarget.defineSender(sender).defineTarget(player).defineMessage(finalMessage).getComponent();
            onTarget = onTarget.hoverEvent(HoverEvent.showText(HoverEvents.reply.getComponent()));
            onTarget = onTarget.clickEvent(ClickEvent.suggestCommand("/tell " + sender.getName()));

            Audiences.sender(sender).sendMessage(fromSender);
            Audiences.player(player).sendMessage(onTarget);

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
