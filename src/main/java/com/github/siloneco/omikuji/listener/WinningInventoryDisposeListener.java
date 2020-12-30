package com.github.siloneco.omikuji.listener;

import com.github.siloneco.omikuji.Omikuji;
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
public class WinningInventoryDisposeListener implements Listener {

    private final Omikuji plugin;

    @EventHandler
    public void onCloseInventory(InventoryCloseEvent e) {
        if (!(e.getPlayer() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();

        String title = VersionUtils.getInventoryTitle(e.getView(), inv);

        if (title == null) {
            return;
        }

        if (!title.startsWith(Chat.f("&cOmikuji Winnings &7- &8"))) {
            return;
        }

        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                p.sendMessage(Chat.f("{0} &aアイテムがまだ残っています！必要な場合は再度クリックしてインベントリを開いてください！", plugin.getPluginConfig().getPrefix()));
                // アイテムが全てなかった場合にのみ disposeInventory を呼び出したいので、ここは continue ではなく return で良い
                return;
            }
        }

        String publicID = title.substring("&cOmikuji Winnings &7- &8".length());
        String secretID = plugin.getWinningInventoryContainer().getSecretID(publicID);

        if (secretID == null) {
            return;
        }
        plugin.getWinningInventoryContainer().disposeInventory(secretID);
    }
}
