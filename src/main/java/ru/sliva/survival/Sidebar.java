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

    private final List<String[]> informationList = new ArrayList<>(Arrays.asList(
            new String[] {
                    "§6| §fIP: §esurv.artgame.tk",
                    "§6| §fВанильное выживание без",
                    "§6| §fкоманд и телепортаций"
            },
            new String[] {
                    "§5| §fdiscord.io/artgameserv",
                    "§5| §dПравила, важная информация",
                    "§5| §dи ваши вопросы по серверу."
            },
            new String[] {
                    "§3| §ft.me/artgameserv",
                    "§3| §bСледите за новостями в",
                    "§3| §bнашем канале Telegram."
            },
            new String[] {
                    "§2| §aГриферство строго §c§nзапрещено.",
                    "§2| §aМодификации клиента (читы)",
                    "§2| §c§nзапрещены §aна сервере."
            }
    ));
    private int informationCounter = 0;
    private String[] currentInformation;

    public Sidebar() {
        Schedule.timer(this, 20);
        Schedule.timerAsync(() -> {
            currentInformation = informationList.get(informationCounter);
            informationCounter++;
            if(informationCounter >= informationList.size()) {
                informationCounter = 0;
            }
        }, 600);
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

        board.setName("§9§lArt§c§lGame");

        List<String> list = new ArrayList<>(Arrays.asList(
                "§r",
                "§8| §fНик: §7" + p.getName(),
                "§8| §fПинг: §7" + p.getPing(),
                "§r§r",
                "§2| §fОнлайн: §a" + Utils.getOnlinePlayers().size(),
                "§r§r§r"
        ));
        list.addAll(Arrays.asList(currentInformation));

        board.setAll(list.toArray(new String[0]));
    }
}