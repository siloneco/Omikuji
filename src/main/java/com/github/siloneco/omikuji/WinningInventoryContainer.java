package com.github.siloneco.omikuji;

import com.github.siloneco.omikuji.utility.Chat;
import org.apache.commons.lang.RandomStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WinningInventoryContainer {

    private final HashMap<String, Inventory> invMap = new HashMap<>();
    private final List<String> alreadyUsedIDs = new ArrayList<>();
    private final HashMap<String, String> publicToSecretIdMap = new HashMap<>();

    public OmikujiWinningInventoryID createInventoryWithID(OmikujiResult result) {
        if (result.getItems().isEmpty()) {
            return null;
        }

        OmikujiWinningInventoryID id = new OmikujiWinningInventoryID(issueID(), issueID());

        int size = 9 * ((int) ((double) result.getItems().size() / 9d) + 1);
        Inventory inv = Bukkit.createInventory(null, size, Chat.f("&cOmikuji Winnings &7- &8{0}", id.getPublicID()));

        for (ItemStack item : result.getItems()) {
            item = item.clone();
            inv.addItem(item);
        }
        invMap.put(id.getSecretID(), inv);
        publicToSecretIdMap.put(id.getPublicID(), id.getSecretID());

        return id;
    }

    public Inventory getInventory(String secretId) {
        return invMap.getOrDefault(secretId, null);
    }

    public void disposeInventory(String id) {
        invMap.remove(id);
    }

    public String getSecretID(String publicID) {
        return publicToSecretIdMap.getOrDefault(publicID, null);
    }

    private String issueID() {
        String id = RandomStringUtils.randomAlphabetic(6);
        while (alreadyUsedIDs.contains(id)) {
            id = RandomStringUtils.randomAlphabetic(6);
        }

        alreadyUsedIDs.add(id);
        return id;
    }
}
