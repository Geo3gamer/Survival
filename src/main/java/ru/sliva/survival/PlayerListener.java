package ru.sliva.survival;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
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
import org.jetbrains.annotations.NotNull;
import ru.sliva.api.Events;
import ru.sliva.api.Slezhka;
import ru.sliva.api.TextUtil;
import ru.sliva.api.legacy.Audiences;
import ru.sliva.survival.config.Cmds;
import ru.sliva.survival.config.HoverEvents;
import ru.sliva.survival.config.Messages;

import java.util.*;

public final class PlayerListener implements Listener {

    private final LegacyComponentSerializer paragraphSerializer = TextUtil.paragraphSerializer;

    public PlayerListener() {
        Events.registerListener(this);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Player player = event.getPlayer();
        Component join = Messages.join.definePlayer(player).getComponent();
        for (Player p : Bukkit.getOnlinePlayers()) {
            Audiences.player(p).sendActionBar(join);
        }
        Audiences.sender(Bukkit.getConsoleSender()).sendMessage(join);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent event) {
        event.setQuitMessage(null);
        Player player = event.getPlayer();
        Component quit = Messages.quit.definePlayer(player).getComponent();
        for (Player p : Bukkit.getOnlinePlayers()) {
            Audiences.player(p).sendActionBar(quit);
        }
        Audiences.sender(Bukkit.getConsoleSender()).sendMessage(quit);
    }

    @EventHandler
    public void onPing(@NotNull PaperServerListPingEvent event) {
        event.setMotd(Messages.motd.getString());
        event.setMaxPlayers(event.getNumPlayers() + 1);
        event.setProtocolVersion(-1);
        event.setVersion(Messages.listPlayers.getString().replace("{min}", String.valueOf(event.getNumPlayers())).replace("{max}", String.valueOf(event.getMaxPlayers())));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final @NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Audience audience = Audiences.player(player);

        Component message = paragraphSerializer.deserialize(event.getMessage());
        if (player.hasPermission("survival.color")) {
            message = TextUtil.color(message);
        }
        message = TextUtil.removeBadWords(message);

        boolean global = false;

        String literalMessage = paragraphSerializer.serialize(message);

        if (literalMessage.startsWith("!")) {
            message = message.replaceText(builder -> builder.matchLiteral("!").once().replacement(""));
            literalMessage = paragraphSerializer.serialize(message);
            global = true;
        } else {
            Set<Player> viewers = event.getRecipients();
            for (Player viewer : new HashSet<>(viewers)) {
                if (outOfRange(viewer.getLocation(), player.getLocation())) {
                    viewers.remove(player);
                }
            }
            if (viewers.size() == 1) {
                audience.sendMessage(Messages.nobodyHeard.getComponent());
            }
        }

        event.setMessage(literalMessage);

        if (Objects.equals(literalMessage, "")) {
            event.setCancelled(true);
            return;
        }

        if(!event.isCancelled()) {
            event.setCancelled(true);
            Component rendered = render(player, message, global);
            List<Player> viewers = new ArrayList<>(event.getRecipients());
            if(!viewers.contains(player)) viewers.add(player);
            for(Player viewer : new HashSet<>(event.getRecipients())) {
                Audiences.player(viewer).sendMessage(rendered);
            }
            Audiences.sender(Bukkit.getConsoleSender()).sendMessage(rendered);
        }
    }

    private Component render(@NotNull Player source, Component message, boolean global) {
        Component sourceDisplayName = TextUtil.getDisplayName(source);

        Component chat;
        if (global) {
            chat = Component.text("Ⓖ ").color(NamedTextColor.GREEN);
        } else {
            chat = Component.text("Ⓛ ").color(NamedTextColor.YELLOW);
        }

        TextComponent textMessage = (TextComponent) message.color(NamedTextColor.GRAY);
        textMessage = textMessage.hoverEvent(HoverEvent.showText(HoverEvents.copyChatMessage.getComponent()));
        textMessage = textMessage.clickEvent(ClickEvent.copyToClipboard(textMessage.content()));
        final TextComponent finalMessage = textMessage;

        TextComponent textDisplayName = (TextComponent) sourceDisplayName.color(NamedTextColor.WHITE);
        textDisplayName = textDisplayName.hoverEvent(HoverEvent.showText(HoverEvents.sendMessage.definePlayer(source).getComponent()));
        textDisplayName = textDisplayName.clickEvent(ClickEvent.suggestCommand("/tell " + source.getName() + " "));
        final TextComponent finalName = textDisplayName;

        Component rendered = Messages.chatFormat.getComponent();
        rendered = TextUtil.replaceLiteral(rendered, "{player}", finalName);
        rendered = TextUtil.replaceLiteral(rendered, "{message}", finalMessage);

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
        event.setMessage(Messages.unknownCommand.getString());
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
        if (event.getReason().equalsIgnoreCase("Kicked for spamming")) {
            event.setCancelled(true);
        }

        Player player = event.getPlayer();
        Component format = Cmds.slezhka_kick.defineTarget(player).defineMessage(paragraphSerializer.deserialize(event.getReason())).getComponent();
        Slezhka.send(format);
    }

    @EventHandler
    public void onCommandPreprocess(@NotNull PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        Component format = Cmds.slezhka_command.defineTarget(player).defineCommand(event.getMessage()).getComponent();
        Slezhka.send(format);
    }
}
