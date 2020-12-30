package com.github.siloneco.omikuji.listener;

import com.github.siloneco.omikuji.Omikuji;
import com.github.siloneco.omikuji.utility.Chat;
import com.github.siloneco.omikuji.utility.VersionUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

@RequiredArgsConstructor
public class ResultItemViewListener implements Listener {

    private final Omikuji plugin;

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();

        String title = VersionUtils.getInventoryTitle(e.getView(), inv);

        if (title == null) {
            return;
        }

        if (title.equals(Chat.f("&eOmikuji Result Item Viewer"))) {
            e.setCancelled(true);
        }
    }
}
