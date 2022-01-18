package ru.sliva.survival;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.api.Slezhka;
import ru.sliva.api.TextUtil;
import ru.sliva.survival.config.PluginConfig;

import java.util.*;

public class PlayerListener implements Listener {

    private final ConfigurationNode messages;
    private final ConfigurationNode hoverEvents;
    private final ConfigurationNode slezhka;
    private final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();
    private final LegacyComponentSerializer paragraphSerializer = LegacyComponentSerializer.legacySection();
    private final LegacyComponentSerializer configSerializer = TextUtil.configSerializer;

    public PlayerListener(@NotNull Survival plugin) {
        PluginConfig config = plugin.getPluginConfig();
        this.messages = config.getMessages();
        this.hoverEvents = config.getHoverEvents();
        this.slezhka = config.getCommand("slezhka");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        e.joinMessage(null);
        Player p = e.getPlayer();
        Component join = configSerializer.deserialize(TextUtil.fromNullable(messages.node("join").getString()));
        join = join.replaceText(builder -> builder.matchLiteral("{target}").replacement(p.displayName()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(join);
        }
        Bukkit.getConsoleSender().sendMessage(join);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        e.quitMessage(null);
        Player p = e.getPlayer();
        Component quit = configSerializer.deserialize(TextUtil.fromNullable(messages.node("quit").getString()));
        quit = quit.replaceText(builder -> builder.match("{target}").replacement(p.displayName()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(quit);
        }
        Bukkit.getConsoleSender().sendMessage(quit);
    }

    @EventHandler
    public void onPing(@NotNull PaperServerListPingEvent event) {
        event.motd(configSerializer.deserialize(TextUtil.fromNullable(messages.node("motd").getString())));
        event.setMaxPlayers(event.getNumPlayers() + 1);
        event.setProtocolVersion(-1);
        event.setVersion(ChatColor.translateAlternateColorCodes('%', TextUtil.fromNullable(messages.node("listPlayers").getString()).replace("{min}", String.valueOf(event.getNumPlayers())).replace("{max}", String.valueOf(event.getMaxPlayers()))));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final @NotNull AsyncChatEvent e) {
        Player p = e.getPlayer();

        Component message = e.message();
        if(p.hasPermission("survival.color")) {
            message = ampersandSerializer.deserialize(ampersandSerializer.serialize(message));
        }
        message = message.replaceText(builder -> builder.match("(?iu)\\b((у|[нз]а|(хитро|не)?вз?[ыьъ]|с[ьъ]|(и|ра)[зс]ъ?|(о[тб]|под)[ьъ]?|(.\\B)+?[оаеи])?-?([её]б(?!о[рй])|и[пб][ае][тц]).*?|(н[иеа]|([дп]|верт)о|ра[зс]|з?а|с(ме)?|о(т|дно)?|апч)?-?ху([яйиеёю]|ли(?!ган)).*?|(в[зы]|(три|два|четыре)жды|(н|сук)а)?-?бл(я(?!(х|ш[кн]|мб)[ауеыио]).*?|[еэ][дт]ь?)|(ра[сз]|[зн]а|[со]|вы?|п(ере|р[оие]|од)|и[зс]ъ?|[ао]т)?п[иеё]зд.*?|(за)?п[ие]д[аое]?р(ну.*?|[оа]м|(ас)?(и(ли)?[нщктл]ь?)?|(о(ч[еи])?|ас)?к(ой)|юг)[ауеы]?|манд([ауеыи](л(и[сзщ])?[ауеиы])?|ой|[ао]вошь?(е?к[ауе])?|юк(ов|[ауи])?)|муд([яаио].*?|е?н([ьюия]|ей))|мля([тд]ь)?|лять|([нз]а|по)х|м[ао]л[ао]фь([яию]|[еёо]й))\\b").replacement(""));
        message = message.replaceText(builder -> builder.match("^[ \\t]+|[ \\t]+(?=\\s)").replacement(""));

        String literalMessage = paragraphSerializer.serialize(message);

        e.message(message);

        boolean global = false;

        if(literalMessage.startsWith("!")) {
            message = message.replaceText(builder -> builder.matchLiteral("!").once().replacement(""));
            e.message(message);
            literalMessage = paragraphSerializer.serialize(message);
            global = true;
        } else {
            Set<Audience> viewers = e.viewers();
            for(Audience audience : new HashSet<>(viewers)) {
                if(audience instanceof Player) {
                    Player player = (Player) audience;
                    if(outOfRange(p.getLocation(), player.getLocation())) {
                        viewers.remove(audience);
                    }
                }
            }
            if(viewers.size() == 2) {
                p.sendMessage(ampersandSerializer.deserialize(TextUtil.fromNullable(messages.node("nobodyHeard").getString())));
            }
        }

        if(Objects.equals(literalMessage, "")) {
            e.setCancelled(true);
            return;
        }

        e.renderer(constructChatRenderer(global));
    }

    @Contract(value = "_ -> new", pure = true)
    private @NotNull ChatRenderer constructChatRenderer(boolean global) {
        return ChatRenderer.viewerUnaware((source, sourceDisplayName, message) -> {
            Component chat;
            if(global) {
                chat = Component.text("Ⓖ ").color(NamedTextColor.GREEN);
            } else {
                chat = Component.text("Ⓛ ").color(NamedTextColor.YELLOW);
            }

            TextComponent textMessage = (TextComponent) message.color(NamedTextColor.GRAY);
            textMessage = textMessage.hoverEvent(HoverEvent.showText(ampersandSerializer.deserialize(TextUtil.fromNullable(hoverEvents.node("copyChatMessage").getString()))));
            textMessage = textMessage.clickEvent(ClickEvent.copyToClipboard(textMessage.content()));
            final TextComponent finalMessage = textMessage;

            TextComponent textDisplayName = (TextComponent) sourceDisplayName.color(NamedTextColor.WHITE);
            Component sendMessage = ampersandSerializer.deserialize(TextUtil.fromNullable(hoverEvents.node("sendMessage").getString()));
            sendMessage = sendMessage.replaceText(builder -> builder.matchLiteral("{player}").replacement(sourceDisplayName));
            textDisplayName = textDisplayName.hoverEvent(HoverEvent.showText(sendMessage));
            textDisplayName = textDisplayName.clickEvent(ClickEvent.suggestCommand("/tell " + source.getName() + " "));
            final TextComponent finalName = textDisplayName;

            Component rendered = ampersandSerializer.deserialize(TextUtil.fromNullable(messages.node("chatFormat").getString()));
            rendered = rendered.replaceText(builder -> builder.matchLiteral("{player}").replacement(finalName));
            rendered = rendered.replaceText(builder -> builder.matchLiteral("{message}").replacement(finalMessage));
            return chat.append(rendered);
        });
    }

    private boolean outOfRange(@NotNull Location l, Location ll) {
        if (l.equals(ll)) {
            return false;
        } else if (l.getWorld() != ll.getWorld()) {
            return true;
        }
        return l.distanceSquared(ll) > 10000;
    }

    @EventHandler
    public void signChange(@NotNull SignChangeEvent e) {
        TextComponent empty = Component.text("");
        TextComponent line0 = (TextComponent) e.line(0);
        if(line0 == null) line0 = empty;
        TextComponent line1 = (TextComponent) e.line(1);
        if(line1 == null) line1 = empty;
        TextComponent line2 = (TextComponent) e.line(2);
        if(line2 == null) line2 = empty;
        TextComponent line3 = (TextComponent) e.line(3);
        if(line3 == null) line3 = empty;
        if (e.getPlayer().hasPermission("survival.color")) {
            line0 = ampersandSerializer.deserialize(ampersandSerializer.serialize(line0));
            line1 = ampersandSerializer.deserialize(ampersandSerializer.serialize(line1));
            line2 = ampersandSerializer.deserialize(ampersandSerializer.serialize(line2));
            line3 = ampersandSerializer.deserialize(ampersandSerializer.serialize(line3));
        } else {
            line0 = paragraphSerializer.deserialize(line0.content());
            line1 = paragraphSerializer.deserialize(line1.content());
            line2 = paragraphSerializer.deserialize(line2.content());
            line3 = paragraphSerializer.deserialize(line3.content());
        }
        e.line(0, line0);
        e.line(1, line1);
        e.line(2, line2);
        e.line(3, line3);
    }

    @EventHandler
    public void unknownCommand(@NotNull UnknownCommandEvent e) {
        e.message(ampersandSerializer.deserialize(TextUtil.fromNullable(messages.node("unknownCommand").getString())));
    }

    @EventHandler
    public void bookChange(@NotNull PlayerEditBookEvent e) {
        Player p = e.getPlayer();
        BookMeta newMeta = e.getNewBookMeta();
        List<Component> oldPages = newMeta.pages();
        List<Component> updatedPages = new ArrayList<>();
        for (Component component : oldPages) {
            TextComponent page = (TextComponent) component;
            if (p.hasPermission("survival.color")) {
                page = ampersandSerializer.deserialize(ampersandSerializer.serialize(page));
            } else {
                page = paragraphSerializer.deserialize(page.content());
            }
            updatedPages.add(page);
        }
        newMeta.pages(updatedPages);
        e.setNewBookMeta(newMeta);
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent e) {
        Player p = e.getPlayer();
        Component format = ampersandSerializer.deserialize(TextUtil.fromNullable(slezhka.node("kick").getString()));
        format = format.replaceText(builder -> builder.matchLiteral("{player}").replacement(p.displayName().color(NamedTextColor.WHITE)));
        format = format.replaceText(builder -> builder.matchLiteral("{cause}").replacement(e.getCause().name().toLowerCase()));
        format = format.replaceText(builder -> builder.matchLiteral("{message}").replacement(e.reason().color(NamedTextColor.GRAY)));
        Slezhka.send(format);
    }

    @EventHandler
    public void onCommandPreprocess(@NotNull PlayerCommandPreprocessEvent e) {
        Player p = e.getPlayer();
        Component format = ampersandSerializer.deserialize(TextUtil.fromNullable(slezhka.node("command").getString()));
        format = format.replaceText(builder -> builder.matchLiteral("{player}").replacement(p.displayName().color(NamedTextColor.WHITE)));
        format = format.replaceText(builder -> builder.matchLiteral("{command}").replacement(e.getMessage()));
        Slezhka.send(format);
    }
}
