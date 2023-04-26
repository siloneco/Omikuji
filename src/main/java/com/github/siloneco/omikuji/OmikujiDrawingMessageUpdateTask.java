package com.github.siloneco.omikuji;

import com.github.siloneco.omikuji.utility.Chat;
import com.github.siloneco.omikuji.utility.VersionUtils;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class OmikujiDrawingMessageUpdateTask extends BukkitRunnable {

    private final Omikuji plugin;

    private int numberOfDots = 1;

    @Override
    public void run() {
        if (plugin.getExecutingPlayers().isEmpty()) {
            return;
        }

        plugin.getExecutingPlayers().stream()
            .map(Bukkit::getPlayer)
            .filter(p -> p != null && p.isOnline()).forEach(this::display);

        numberOfDots++;
        if (numberOfDots > 3) {
            numberOfDots = 1;
        }
    }

    public void display(Player p) {
        VersionUtils.sendTitle(Chat.f("&aおみくじを引いています{0}", Strings.repeat(".", numberOfDots)), 0, 20, 10, p);
    }
}
