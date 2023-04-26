package com.github.siloneco.omikuji;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public class ResultItemLoader {

    private final Omikuji plugin;

    private YamlConfiguration conf = null;

    private HashMap<String, List<ItemStack>> itemMap = new HashMap<>();

    public List<ItemStack> getItemList(String id) {
        return itemMap.getOrDefault(id, null);
    }

    public boolean hasItemList(String id) {
        return itemMap.containsKey(id);
    }

    public Map<String, List<ItemStack>> load() {
        if (conf == null) {
            reloadConfigFile();
        }

        HashMap<String, List<ItemStack>> itemMap = new HashMap<>();

        ConfigurationSection sec = conf.getConfigurationSection("");
        if (sec == null) {
            return itemMap;
        }

        for (String key : sec.getKeys(false)) {
            List<ItemStack> items = new ArrayList<>();
            for (int i = 0; i < 54; i++) {
                if (!conf.isSet(key + "." + i)) {
                    continue;
                }

                ItemStack item = conf.getItemStack(key + "." + i);
                items.add(item);
            }

            itemMap.put(key, items);
        }

        this.itemMap = itemMap;
        return itemMap;
    }

    public boolean save(String id, List<ItemStack> items) {
        if (conf == null) {
            reloadConfigFile();
        }

        conf.set(id, null);

        for (int i = 0, size = items.size(); i < size; i++) {
            ItemStack item = items.get(i);
            conf.set(id + "." + i, item);
        }

        try {
            saveConfig();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void saveConfig() throws IOException {
        if (conf == null) {
            return;
        }

        conf.save(new File(plugin.getDataFolder(), "items.yml"));
    }

    public void reloadConfigFile() {
        File file = new File(plugin.getDataFolder(), "items.yml");
        Reader reader;
        try {
            reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            conf = YamlConfiguration.loadConfiguration(reader);
        } catch (FileNotFoundException e) {
            conf = new YamlConfiguration();
        }
    }

    public void reloadItems() {
        reloadConfigFile();
        itemMap.clear();
        load();
    }
}
