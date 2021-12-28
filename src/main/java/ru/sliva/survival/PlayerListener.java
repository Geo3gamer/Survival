package ru.sliva.survival;

import com.destroystokyo.paper.event.server.PaperServerListPingEvent;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.command.UnknownCommandEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.meta.BookMeta;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import ru.sliva.survival.config.PlayersConfig;
import ru.sliva.survival.config.PluginConfig;
import ru.sliva.survival.config.SPlayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PlayerListener implements Listener {

    private final PlayersConfig playersConfig;
    private final PluginConfig config;
    private final Survival plugin;

    public PlayerListener(@NotNull Survival plugin) {
        this.plugin = plugin;
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
        Component join = Component.text(config.getColorizedString("messages.join").replace("<target>", ((TextComponent) p.displayName()).content()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(join);
        }
        Bukkit.getConsoleSender().sendMessage(join);
    }

    @EventHandler
    public void onQuit(@NotNull PlayerQuitEvent e) {
        e.quitMessage(null);
        Player p = e.getPlayer();
        Component quit = Component.text(config.getColorizedString("messages.quit").replace("<target>", ((TextComponent) p.displayName()).content()));
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendActionBar(quit);
        }
        Bukkit.getConsoleSender().sendMessage(quit);
    }

    @EventHandler
    public void onPing(@NotNull PaperServerListPingEvent event) {
        event.motd(Component.text(config.getColorizedString("messages.motd")));
        event.setMaxPlayers(event.getNumPlayers() + 1);
        event.setProtocolVersion(-1);
        event.setVersion(config.getColorizedString("messages.listPlayers").replace("<min>", String.valueOf(event.getNumPlayers())).replace("<max>", String.valueOf(event.getMaxPlayers())));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(final @NotNull AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        String message = e.getMessage();
        if (p.hasPermission("survival.color")) {
            message = ChatColor.translateAlternateColorCodes('&', message);
        }

        String chatFormat = config.getColorizedString("messages.chatFormat");
        if(message.startsWith("!")) {
            message = message.substring(1);
            chatFormat = "§aⒼ §r" + chatFormat.replace("<player>", ((TextComponent) p.displayName()).content()).replace("<message>", message);
            e.setFormat(chatFormat);
        } else {
            chatFormat = "§eⓁ §r" + chatFormat.replace("<player>", ((TextComponent) p.displayName()).content()).replace("<message>", message);
            e.setFormat(chatFormat);
            for(Player player : new HashSet<>(e.getRecipients())) {
                if(outOfRange(p.getLocation(), player.getLocation())) {
                    e.getRecipients().remove(player);
                }
            }
            if(e.getRecipients().size() == 1) {
                p.sendMessage(Component.text(config.getColorizedString("messages.nobodyHeard")));
            }
        }
    }

    private boolean outOfRange(@NotNull Location l, Location ll) {
        if (l.equals(ll)) {
            return false;
        } else if (l.getWorld() != ll.getWorld()) {
            return true;
        }
        return l.distanceSquared(ll) > 100;
    }

    @EventHandler
    public void signChange(@NotNull SignChangeEvent e) {
        TextComponent line0 = (TextComponent) e.line(0);
        TextComponent line1 = (TextComponent) e.line(1);
        TextComponent line2 = (TextComponent) e.line(2);
        TextComponent line3 = (TextComponent) e.line(3);
        if (e.getPlayer().hasPermission("survival.color")) {
            assert line0 != null;
            line0 = line0.content(ChatColor.translateAlternateColorCodes('&', line0.content()));
            assert line1 != null;
            line1 = line1.content(ChatColor.translateAlternateColorCodes('&', line1.content()));
            assert line2 != null;
            line2 = line2.content(ChatColor.translateAlternateColorCodes('&', line2.content()));
            assert line3 != null;
            line3 = line3.content(ChatColor.translateAlternateColorCodes('&', line3.content()));
        } else {
            assert line0 != null;
            line0 = line0.content(ChatColor.stripColor(line0.content()));
            assert line1 != null;
            line1 = line1.content(ChatColor.stripColor(line1.content()));
            assert line2 != null;
            line2 = line2.content(ChatColor.stripColor(line2.content()));
            assert line3 != null;
            line3 = line3.content(ChatColor.stripColor(line3.content()));
        }
        e.line(0, line0);
        e.line(1, line1);
        e.line(2, line2);
        e.line(3, line3);
    }

    @EventHandler
    public void unknownCommand(@NotNull UnknownCommandEvent e) {
        e.message(Component.text(config.getColorizedString("messages.unknownCommand")));
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
                page = page.content(ChatColor.translateAlternateColorCodes('&', page.content()));
            } else {
                page = page.content(ChatColor.stripColor(page.content()));
            }
            updatedPages.add(page);
        }
        newMeta.pages(updatedPages);
        e.setNewBookMeta(newMeta);
    }
}
