package com.github.siloneco.omikuji.listener;

import com.github.siloneco.omikuji.Omikuji;
import com.github.siloneco.omikuji.utility.Chat;
import com.github.siloneco.omikuji.utility.VersionUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class SignListener implements Listener {

    private final Omikuji plugin;

    private HashMap<UUID, Long> lastExecuted = new HashMap<>();

    @EventHandler
    public void onSignPlace(SignChangeEvent e) {
        Player p = e.getPlayer();

        if (!p.hasPermission("omikuji.admin")) {
            return;
        }

        if (e.getLine(0).equalsIgnoreCase("[omikuji]") || e.getLine(0).equalsIgnoreCase("[omikuzi]")) {
            e.setLine(0, plugin.getPluginConfig().getSignPrefix());
            e.setLine(1, ChatColor.translateAlternateColorCodes('&', e.getLine(1)));
            e.setLine(2, ChatColor.translateAlternateColorCodes('&', e.getLine(2)));
            e.setLine(3, ChatColor.translateAlternateColorCodes('&', e.getLine(3)));
        }

        p.sendMessage(Chat.f("{0} &a看板を設置しました！", plugin.getPluginConfig().getPrefix()));
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Block b = e.getClickedBlock();

        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (b == null || !VersionUtils.isSign(b)) {
            return;
        }

        Sign sign = (Sign) b.getState();
        if (!plugin.getPluginConfig().getSignPrefix().equalsIgnoreCase(sign.getLine(0))) {
            return;
        }

        if (lastExecuted.getOrDefault(p.getUniqueId(), 0L) + plugin.getPluginConfig().getSignIntervalMilliseconds()
                > System.currentTimeMillis()) {
            return;
        }

        lastExecuted.put(p.getUniqueId(), System.currentTimeMillis());
        plugin.execute(p);
    }
}
