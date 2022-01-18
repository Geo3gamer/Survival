package ru.sliva.survival;

import fr.minuskube.netherboard.Netherboard;
import fr.minuskube.netherboard.bukkit.BPlayerBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import ru.sliva.api.Schedule;
import ru.sliva.api.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sidebar implements Runnable {

    public Sidebar() {
        Schedule.timer(this, 20);
    }

    @Override
    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            updateScoreboard(p);
        }
    }

    public void updateScoreboard(Player p) {
        BPlayerBoard board = Netherboard.instance().getBoard(p);
        if(board == null) {
            board = Netherboard.instance().createBoard(p, "");
        }

        board.setName("§с§lTest");

        List<String> list = new ArrayList<>(Arrays.asList(
                "§r",
                "§8| §fНик: §7" + p.getName(),
                "§8| §fПинг: §7" + p.spigot().getPing(),
                "§r§r",
                "§2| §fОнлайн: §a" + Utils.getOnlinePlayers().size(),
                "§r§r§r"
        ));

        board.setAll(list.toArray(new String[0]));
    }
}