package ru.sliva.survival.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import ru.sliva.api.Slezhka;
import ru.sliva.api.command.AbstractCommand;
import ru.sliva.survival.Survival;
import ru.sliva.survival.config.Cmds;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public final class SlezhkaCommand extends AbstractCommand {

    public SlezhkaCommand(@NotNull Survival plugin) {
        super(plugin, "slezhka", "Шпионство за действиями игроков", "/slezhka", "survival.slezhka", Collections.singletonList("spy"));
    }

    @Override
    public boolean exec(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
        if(args.length < 1) {
            if(sender instanceof Player) {
                Player player = (Player) sender;
                UUID uuid = player.getUniqueId();
                List<UUID> spies = Slezhka.spies;
                if(spies.contains(uuid)) {
                    spies.remove(uuid);
                    player.sendMessage(Cmds.slezhka_disabled.getString());
                } else {
                    spies.add(uuid);
                    player.sendMessage(Cmds.slezhka_enabled.getString());
                }
            }
            return true;
        }
        return false;
    }
}
