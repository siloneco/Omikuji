package com.github.siloneco.omikuji.listener;

import com.github.siloneco.omikuji.Omikuji;
import com.github.siloneco.omikuji.utility.Chat;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Calendar;

@RequiredArgsConstructor
public class JoinListener implements Listener {

    private final Omikuji plugin;

    /**
     * 参加時にメッセージを送るListener
     */
    @EventHandler
    public void onNormalPlayerJoin(PlayerJoinEvent e) {
        String msg = plugin.getPluginConfig().getJoinMessage();

        if (msg == null) {
            return;
        }

        Calendar enable = plugin.getPluginConfig().getAutoEnable();
        Calendar disable = plugin.getPluginConfig().getAutoDisable();

        if (enable == null) {
            enable = Calendar.getInstance();
            enable.add(Calendar.DATE, -1);
        }
        if (disable == null) {
            disable = Calendar.getInstance();
            disable.add(Calendar.DATE, 1);
        }

        Calendar now = Calendar.getInstance();
        if (!(enable.before(now) && disable.after(now))) {
            return;
        }

        e.getPlayer().sendMessage(plugin.getPluginConfig().getPrefix() + ChatColor.RESET + " " + msg);
    }

    /**
     * 運営が参加したときに、おみくじを引ける期間の終了から1週間以上経っていた場合はPluginの削除を促すListenr
     */
    @EventHandler
    public void onAdminJoin(PlayerJoinEvent e) {
        Calendar disableCal = plugin.getPluginConfig().getAutoDisable();
        if (disableCal == null) {
            return;
        }
        disableCal = (Calendar) disableCal.clone();

        Player p = e.getPlayer();
        if (!p.hasPermission("omikuji.disablealert")) {
            return;
        }

        disableCal.add(Calendar.DATE, 7);

        if (disableCal.after(Calendar.getInstance())) {
            return;
        }

        p.sendMessage(Chat.f("{0} &eおみくじを引ける期間が終了してから&c1週間&eが経過しています。余計な&c負荷&eとなるため、Pluginの&c削除&eを検討してください。", plugin.getPluginConfig().getPrefix()));
    }
}
