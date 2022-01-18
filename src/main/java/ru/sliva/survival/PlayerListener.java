package ru.sliva.survival;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
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
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.api.Events;
import ru.sliva.api.Slezhka;
import ru.sliva.api.TextUtil;
import ru.sliva.api.legacy.Audiences;
import ru.sliva.survival.config.PluginConfig;

import java.util.*;

public class PlayerListener implements Listener {

    private final ConfigurationNode messages;
    private final ConfigurationNode hoverEvents;
    private final ConfigurationNode slezhka;
    private final LegacyComponentSerializer ampersandSerializer = TextUtil.ampersandSerializer;
    private final LegacyComponentSerializer paragraphSerializer = TextUtil.paragraphSerializer;
    private final LegacyComponentSerializer configSerializer = TextUtil.configSerializer;

    public PlayerListener(@NotNull Survival plugin) {
        PluginConfig config = plugin.getPluginConfig();
        this.messages = config.getMessages();
        this.hoverEvents = config.getHoverEvents();
        this.slezhka = config.getCommand("slezhka");
        Events.registerListener(this);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        Component join = configSerializer.deserialize(TextUtil.fromNullable(messages.node("join").getString()));
        join = join.replaceText(builder -> builder.matchLiteral("{target}").replacement(TextUtil.getDisplayName(player)));
        for (Player p : Bukkit.getOnlinePlayers()) {
            Audiences.player(p).sendActionBar(join);
        }
        Audiences.sender(Bukkit.getConsoleSender()).sendMessage(join);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        Component quit = configSerializer.deserialize(TextUtil.fromNullable(messages.node("quit").getString()));
        quit = quit.replaceText(builder -> builder.matchLiteral("{target}").replacement(player.getDisplayName()));
        for (Player p : Bukkit.getOnlinePlayers()) {
            Audiences.player(p).sendActionBar(quit);
        }
        Audiences.sender(Bukkit.getConsoleSender()).sendMessage(quit);
    }

    @EventHandler
    public void onPing(@NotNull PaperServerListPingEvent event) {
        event.setMotd(TextUtil.colorConfig(TextUtil.fromNullable(messages.node("motd").getString())));
        event.setMaxPlayers(event.getNumPlayers() + 1);
        event.setProtocolVersion(-1);
        event.setVersion(TextUtil.colorConfig(TextUtil.fromNullable(messages.node("listPlayers").getString()).replace("{min}", String.valueOf(event.getNumPlayers())).replace("{max}", String.valueOf(event.getMaxPlayers()))));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final @NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        String message = event.getMessage();
        if (player.hasPermission("survival.color")) {
            message = TextUtil.color(message);
        }
        message = message.replaceAll("(?iu)\\b((у|[нз]а|(хитро|не)?вз?[ыьъ]|с[ьъ]|(и|ра)[зс]ъ?|(о[тб]|под)[ьъ]?|(.\\B)+?[оаеи])?-?([её]б(?!о[рй])|и[пб][ае][тц]).*?|(н[иеа]|([дп]|верт)о|ра[зс]|з?а|с(ме)?|о(т|дно)?|апч)?-?ху([яйиеёю]|ли(?!ган)).*?|(в[зы]|(три|два|четыре)жды|(н|сук)а)?-?бл(я(?!(х|ш[кн]|мб)[ауеыио]).*?|[еэ][дт]ь?)|(ра[сз]|[зн]а|[со]|вы?|п(ере|р[оие]|од)|и[зс]ъ?|[ао]т)?п[иеё]зд.*?|(за)?п[ие]д[аое]?р(ну.*?|[оа]м|(ас)?(и(ли)?[нщктл]ь?)?|(о(ч[еи])?|ас)?к(ой)|юг)[ауеы]?|манд([ауеыи](л(и[сзщ])?[ауеиы])?|ой|[ао]вошь?(е?к[ауе])?|юк(ов|[ауи])?)|муд([яаио].*?|е?н([ьюия]|ей))|мля([тд]ь)?|лять|([нз]а|по)х|м[ао]л[ао]фь([яию]|[еёо]й))\\b", "");
        message = message.replaceAll("^[ \\t]+|[ \\t]+(?=\\s)", "");

        event.setMessage(message);

        boolean global = false;

        if (message.startsWith("!")) {
            message = message.substring(1);
            global = true;
        } else {
            Set<Player> viewers = event.getRecipients();
            for (Player viewer : new HashSet<>(viewers)) {
                if (outOfRange(viewer.getLocation(), player.getLocation())) {
                    viewers.remove(player);
                }
            }
            if (viewers.size() == 1) {
                player.sendMessage(TextUtil.colorConfig(TextUtil.fromNullable(messages.node("nobodyHeard").getString())));
            }
        }

        if (Objects.equals(message, "")) {
            event.setCancelled(true);
            return;
        }

        event.setMessage(message);
        if(!event.isCancelled()) {
            event.setCancelled(true);
            Component rendered = render(event, global);
            List<Player> viewers = new ArrayList<>(event.getRecipients());
            if(!viewers.contains(player)) viewers.add(player);
            for(Player viewer : new HashSet<>(event.getRecipients())) {
                Audiences.player(viewer).sendMessage(rendered);
            }
        }
    }

    private Component render(@NotNull AsyncPlayerChatEvent event, boolean global) {
        Component message = paragraphSerializer.deserialize(event.getMessage());
        Player source = event.getPlayer();
        Component sourceDisplayName = TextUtil.getDisplayName(source);

        Component chat;
        if (global) {
            chat = Component.text("Ⓖ ").color(NamedTextColor.GREEN);
        } else {
            chat = Component.text("Ⓛ ").color(NamedTextColor.YELLOW);
        }

        TextComponent textMessage = (TextComponent) message.color(NamedTextColor.GRAY);
        textMessage = textMessage.hoverEvent(HoverEvent.showText(configSerializer.deserialize(TextUtil.fromNullable(hoverEvents.node("copyChatMessage").getString()))));
        textMessage = textMessage.clickEvent(ClickEvent.copyToClipboard(textMessage.content()));
        final TextComponent finalMessage = textMessage;

        TextComponent textDisplayName = (TextComponent) sourceDisplayName.color(NamedTextColor.WHITE);
        Component sendMessage = configSerializer.deserialize(TextUtil.fromNullable(hoverEvents.node("sendMessage").getString()));
        sendMessage = sendMessage.replaceText(builder -> builder.matchLiteral("{player}").replacement(sourceDisplayName));
        textDisplayName = textDisplayName.hoverEvent(HoverEvent.showText(sendMessage));
        textDisplayName = textDisplayName.clickEvent(ClickEvent.suggestCommand("/tell " + source.getName() + " "));
        final TextComponent finalName = textDisplayName;

        Component rendered = configSerializer.deserialize(TextUtil.fromNullable(messages.node("chatFormat").getString()));
        rendered = rendered.replaceText(builder -> builder.matchLiteral("{player}").replacement(finalName));
        rendered = rendered.replaceText(builder -> builder.matchLiteral("{message}").replacement(finalMessage));

        return chat.append(rendered);
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
    public void signChange(@NotNull SignChangeEvent event) {
        String[] lines = event.getLines();
        Player player = event.getPlayer();
        for(int i = 0; i < lines.length; i++) {
            if(player.hasPermission("survival.color")) {
                event.setLine(i, TextUtil.color(lines[i]));
            } else {
                event.setLine(i, ChatColor.stripColor(lines[i]));
            }
        }
    }

    @EventHandler
    public void unknownCommand(@NotNull UnknownCommandEvent event) {
        event.setMessage(TextUtil.colorConfig(TextUtil.fromNullable(messages.node("unknownCommand").getString())));
    }

    @EventHandler
    public void bookChange(@NotNull PlayerEditBookEvent event) {
        Player player = event.getPlayer();
        BookMeta newMeta = event.getNewBookMeta();
        List<String> oldPages = newMeta.getPages();
        List<String> updatedPages = new ArrayList<>();
        for (String page : oldPages) {
            if (player.hasPermission("survival.color")) {
                page = TextUtil.color(page);
            } else {
                page = ChatColor.stripColor(page);
            }
            updatedPages.add(page);
        }
        newMeta.setPages(updatedPages);
        event.setNewBookMeta(newMeta);
    }

    @EventHandler
    public void onKick(@NotNull PlayerKickEvent event) {
        Player player = event.getPlayer();
        Component format = configSerializer.deserialize(TextUtil.fromNullable(slezhka.node("kick").getString()));
        format = format.replaceText(builder -> builder.matchLiteral("{player}").replacement(TextUtil.getDisplayName(player).color(NamedTextColor.WHITE)));
        format = format.replaceText(builder -> builder.matchLiteral("{message}").replacement(paragraphSerializer.deserialize(event.getReason()).color(NamedTextColor.GRAY)));
        Slezhka.send(format);
    }

    @EventHandler
    public void onCommandPreprocess(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Component format = configSerializer.deserialize(TextUtil.fromNullable(slezhka.node("command").getString()));
        format = format.replaceText(builder -> builder.matchLiteral("{player}").replacement(TextUtil.getDisplayName(player).color(NamedTextColor.WHITE)));
        format = format.replaceText(builder -> builder.matchLiteral("{command}").replacement(event.getMessage()));
        Slezhka.send(format);
    }
}
