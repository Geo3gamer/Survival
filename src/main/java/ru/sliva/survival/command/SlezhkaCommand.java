package ru.sliva.survival.command;

import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurationNode;
import ru.sliva.api.TextUtil;
import ru.sliva.survival.Survival;
import ru.sliva.api.Slezhka;
import ru.sliva.api.command.AbstractCommand;
import ru.sliva.survival.config.PluginConfig;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class SlezhkaCommand extends AbstractCommand {

    private final PluginConfig config;

    private final LegacyComponentSerializer configSerializer = TextUtil.configSerializer;

    public SlezhkaCommand(@NotNull Survival plugin) {
        super(plugin, "slezhka", "Шпионство за действиями игроков", "/slezhka", "survival.slezhka", Collections.singletonList("spy"));
        this.config = plugin.getPluginConfig();
    }

    @Override
    public boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if(args.length < 1) {
            if(sender instanceof Player) {
                ConfigurationNode command = config.getCommand("slezhka");
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                List<UUID> spies = Slezhka.spies;
                if(spies.contains(uuid)) {
                    spies.remove(uuid);
                    player.sendMessage(TextUtil.colorConfig(TextUtil.fromNullable(command.node("disabled").getString())));
                } else {
                    spies.add(uuid);
                    player.sendMessage(TextUtil.colorConfig(TextUtil.fromNullable(command.node("enabled").getString())));
                }
            }
            return true;
        }
        return false;
    }
}
