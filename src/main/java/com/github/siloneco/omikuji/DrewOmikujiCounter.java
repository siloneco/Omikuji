package com.github.siloneco.omikuji;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

@RequiredArgsConstructor
public class DrewOmikujiCounter {

    private final Omikuji plugin;

    private final HashMap<UUID, Integer> countMap = new HashMap<>();

    public void load() {
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "count.yml"));

        ConfigurationSection sec = conf.getConfigurationSection("");
        if (sec == null) {
            return;
        }

        for (String uuidStr : sec.getKeys(false)) {
            countMap.put(UUID.fromString(uuidStr), conf.getInt(uuidStr));
        }
    }


    public int getCount(UUID uuid) {
        return countMap.getOrDefault(uuid, 0);
    }

    public void addCount(UUID uuid) {
        countMap.put(uuid, countMap.getOrDefault(uuid, 0) + 1);
    }

    public void subtractCount(UUID uuid) {
        int num = countMap.getOrDefault(uuid, 0) - 1;
        if (num > 0) {
            countMap.put(uuid, num);
        } else if (num == 0) {
            countMap.remove(uuid);
        }
    }

    public int getCount(Player p) {
        return getCount(p.getUniqueId());
    }

    public void addCount(Player p) {
        addCount(p.getUniqueId());
    }

    public void subtractCount(Player p) {
        subtractCount(p.getUniqueId());
    }

    /**
     * 基本、非同期で実行すること。
     */
    public boolean save() {
        File file = new File(plugin.getDataFolder(), "count.yml");
        YamlConfiguration conf = new YamlConfiguration();

        for (UUID uuid : countMap.keySet()) {
            conf.set(uuid.toString(), countMap.get(uuid));
        }

        try {
            conf.save(file);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
