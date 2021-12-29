package ru.sliva.survival.command;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.sliva.survival.Survival;
import ru.sliva.survival.api.Slezhka;
import ru.sliva.survival.config.PluginConfig;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SlezhkaCommand extends AbstractCommand{

    private final PluginConfig config;

    private final LegacyComponentSerializer ampersandSerializer = LegacyComponentSerializer.legacyAmpersand();

    public SlezhkaCommand(@NotNull Survival plugin) {
        super(plugin, "slezhka", "Шпионство за действиями игроков", "/slezhka", "survival.slezhka", Collections.singletonList("spy"));
        this.config = plugin.getConfig();
    }

    @Override
    public boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if(args.length < 1) {
            if(sender instanceof Player) {
                Player p = (Player) sender;
                UUID uuid = p.getUniqueId();
                List<UUID> spies = Slezhka.spies;
                if(spies.contains(uuid)) {
                    spies.remove(uuid);
                    p.sendMessage(ampersandSerializer.deserialize(config.getString("commands.slezhka.disabled")));
                } else {
                    spies.add(uuid);
                    p.sendMessage(ampersandSerializer.deserialize(config.getString("commands.slezhka.enabled")));
                }
            }
            return true;
        }
        return false;
    }
}
