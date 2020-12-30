package com.github.siloneco.omikuji.listener;

import com.github.siloneco.omikuji.Omikuji;
import com.github.siloneco.omikuji.OmikujiResult;
import com.github.siloneco.omikuji.utility.Chat;
import com.github.siloneco.omikuji.utility.VersionUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class ItemRegisterListener implements Listener {

    private final Omikuji plugin;

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }

        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();

        String title = VersionUtils.getInventoryTitle(e.getView(), inv);

        if (title == null) {
            return;
        }

        if (title.startsWith(Chat.f("&aOmikuji Item Edit GUI &7- &c"))) {
            String id = title.substring("&aOmikuji Item Edit GUI &7- &c".length());

            OmikujiResult result = plugin.getPluginConfig().getResultContainer().getResult(id);

            if (result == null) {
                p.sendMessage(Chat.f("{0} &c保存に失敗しました。編集したIDのおみくじ結果が見つかりません。&7(ID: {1}&7)", plugin.getPluginConfig().getPrefix(), id));
                return;
            }

            int itemCount = 0;
            for (ItemStack item : inv.getContents()) {
                if (item != null && item.getType() != Material.AIR) {
                    itemCount++;
                }
            }

            if (result.updateItems()) {
                if (itemCount <= 0) {
                    p.sendMessage(Chat.f("{0} &aアイテムを削除し、保存しました。", plugin.getPluginConfig().getPrefix()));
                } else {
                    p.sendMessage(Chat.f("{0} &aアイテムを保存しました。", plugin.getPluginConfig().getPrefix()));
                }
            } else {
                p.sendMessage(Chat.f("{0} &cアイテムの保存に失敗しました。", plugin.getPluginConfig().getPrefix()));
            }
        }
    }
}
