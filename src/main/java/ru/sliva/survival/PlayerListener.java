package ru.sliva.survival;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.jetbrains.annotations.NotNull;
import ru.sliva.survival.config.PlayersConfig;
import ru.sliva.survival.config.PluginConfig;
import ru.sliva.survival.config.SPlayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PlayerListener implements Listener {

    private final PlayersConfig playersConfig;
    private final PluginConfig config;
    private final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();
    private final LegacyComponentSerializer paragraphSerializer = LegacyComponentSerializer.legacySection();

    public PlayerListener(@NotNull Survival plugin) {
        this.playersConfig = plugin.getPlayersConfig();
        this.config = plugin.getConfig();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(@NotNull PlayerJoinEvent e) {
        e.joinMessage(null);
        Player p = e.getPlayer();
        if(playersConfig.getPlayer(p.getName()) == null) {
            playersConfig.set(p.getName(), new SPlayer());
        }
        Component join = ampersandSerializer.deserialize(config.getString("messages.join"));
        join = join.replaceText(builder -> builder.matchLiteral("<target>").replacement(p.displayName()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(join);
        }
        Bukkit.getConsoleSender().sendMessage(join);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        e.quitMessage(null);
        Player p = e.getPlayer();
        Component quit = ampersandSerializer.deserialize(config.getString("messages.quit"));
        quit = quit.replaceText(builder -> builder.match("<target>").replacement(p.displayName()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(quit);
        }
        Bukkit.getConsoleSender().sendMessage(quit);
    }

    @EventHandler
    public void onPing(@NotNull PaperServerListPingEvent event) {
        event.motd(ampersandSerializer.deserialize(config.getString("messages.motd")));
        event.setMaxPlayers(event.getNumPlayers() + 1);
        event.setProtocolVersion(-1);
        event.setVersion(config.getColorizedString("messages.listPlayers").replace("<min>", String.valueOf(event.getNumPlayers())).replace("<max>", String.valueOf(event.getMaxPlayers())));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final @NotNull AsyncChatEvent e) {
        Player p = e.getPlayer();
        Component message = e.message();
        Set<Audience> viewers = e.viewers();
        if(p.hasPermission("survival.color")) {
            message = ampersandSerializer.deserialize(ampersandSerializer.serialize(message));
        }
        message = message.replaceText(builder -> builder.match("(?iu)\\b((у|[нз]а|(хитро|не)?вз?[ыьъ]|с[ьъ]|(и|ра)[зс]ъ?|(о[тб]|под)[ьъ]?|(.\\B)+?[оаеи])?-?([её]б(?!о[рй])|и[пб][ае][тц]).*?|(н[иеа]|([дп]|верт)о|ра[зс]|з?а|с(ме)?|о(т|дно)?|апч)?-?ху([яйиеёю]|ли(?!ган)).*?|(в[зы]|(три|два|четыре)жды|(н|сук)а)?-?бл(я(?!(х|ш[кн]|мб)[ауеыио]).*?|[еэ][дт]ь?)|(ра[сз]|[зн]а|[со]|вы?|п(ере|р[оие]|од)|и[зс]ъ?|[ао]т)?п[иеё]зд.*?|(за)?п[ие]д[аое]?р(ну.*?|[оа]м|(ас)?(и(ли)?[нщктл]ь?)?|(о(ч[еи])?|ас)?к(ой)|юг)[ауеы]?|манд([ауеыи](л(и[сзщ])?[ауеиы])?|ой|[ао]вошь?(е?к[ауе])?|юк(ов|[ауи])?)|муд([яаио].*?|е?н([ьюия]|ей))|мля([тд]ь)?|лять|([нз]а|по)х|м[ао]л[ао]фь([яию]|[еёо]й))\\b").replacement(""));
        message = message.replaceText(builder -> builder.match("^[ \\t]+|[ \\t]+(?=\\s)").replacement(""));
        message = message.replaceText(builder -> builder.matchLiteral("%").replacement("%%"));
        String literalMessage = paragraphSerializer.serialize(message);
        if(literalMessage.startsWith("!")) {
            message = message.replaceText(builder -> builder.matchLiteral("!").once().replacement(""));
            e.message(message);
            literalMessage = paragraphSerializer.serialize(message);
            if(Objects.equals(literalMessage, "")) {
                e.setCancelled(true);
                return;
            }
            e.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, msg) -> {
                Component globalChat = Component.text("Ⓖ ").color(NamedTextColor.GREEN);
                Component rendered = ampersandSerializer.deserialize(config.getString("messages.chatFormat"));
                rendered = rendered.replaceText(builder -> builder.matchLiteral("<player>").replacement(sourceDisplayName.color(NamedTextColor.GRAY)));
                rendered = rendered.replaceText(builder -> builder.matchLiteral("<message>").replacement(msg.color(NamedTextColor.GRAY)));
                return globalChat.append(rendered);
            }));
        } else {
            e.message(message);
            if(Objects.equals(literalMessage, "")) {
                e.setCancelled(true);
                return;
            }
            e.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, msg) -> {
                Component localChat = Component.text("Ⓛ ").color(NamedTextColor.YELLOW);
                Component rendered = ampersandSerializer.deserialize(config.getString("messages.chatFormat"));
                rendered = rendered.replaceText(builder -> builder.matchLiteral("<player>").replacement(sourceDisplayName.color(NamedTextColor.GRAY)));
                rendered = rendered.replaceText(builder -> builder.matchLiteral("<message>").replacement(msg.color(NamedTextColor.GRAY)));
                return localChat.append(rendered);
            }));
            for(Audience audience : viewers) {
                if(audience instanceof Player) {
                    Player player = (Player) audience;
                    if(outOfRange(p.getLocation(), player.getLocation())) {
                        viewers.remove(audience);
                    }
                }
            }
            if(viewers.size() == 2) {
                p.sendMessage(ampersandSerializer.deserialize(config.getString("messages.nobodyHeard")));
            }
        }
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
        e.message(ampersandSerializer.deserialize(config.getString("messages.unknownCommand")));
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
}
