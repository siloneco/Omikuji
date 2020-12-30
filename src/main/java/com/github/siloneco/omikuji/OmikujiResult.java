package com.github.siloneco.omikuji;

import com.github.siloneco.omikuji.utility.Chat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class OmikujiResult {

    private final Omikuji plugin;
    private final String id;
    private final String displayTitle;
    private final double percentage;
    private final List<ItemStack> items;

    private final int priority;

    private Inventory itemEditInventory = null;

    public void openItemEditInventory(Player p) {
        if (itemEditInventory == null) {
            itemEditInventory = Bukkit.createInventory(null, 9 * 6, Chat.f("&aOmikuji Item Edit GUI &7- &c{0}", id));
            for (ItemStack item : items) {
                itemEditInventory.addItem(item);
            }
        }

        p.openInventory(itemEditInventory);
    }

    public boolean updateItems() {
        if (itemEditInventory == null) {
            return false;
        }

        items.clear();

        for (int i = 0; i < 54; i++) {
            ItemStack item = itemEditInventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                continue;
            }

            items.add(item);
        }

        return plugin.getResultItemLoader().save(id, items);
    }
}
