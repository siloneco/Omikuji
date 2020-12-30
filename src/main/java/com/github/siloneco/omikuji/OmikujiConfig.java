package com.github.siloneco.omikuji;

import com.github.siloneco.omikuji.utility.Chat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class OmikujiConfig {

    private final Omikuji plugin;

    private String prefix;

    private ResultContainer resultContainer;
    private double cost;
    private int maximumAmountPerPlayer;
    private long drawTicks;

    private boolean allowCommand;
    private boolean allowSign;

    private String signPrefix;
    private long signIntervalMilliseconds;

    private Calendar autoEnable = null;
    private Calendar autoDisable = null;

    private String joinMessage;
    private String resultTitle;
    private String resultChat;

    public void load() {
        File file = new File(plugin.getDataFolder(), "config.yml");
        Reader reader;
        try {
            reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }
        YamlConfiguration conf = YamlConfiguration.loadConfiguration(reader);

        prefix = Chat.f(conf.getString("Prefix", "&e[&cおみくじ&e]"));
        cost = conf.getDouble("Cost", -1);
        maximumAmountPerPlayer = conf.getInt("MaximumAmountPerPlayer", -1);
        drawTicks = conf.getLong("DrawTicks", 0L);

        allowCommand = conf.getBoolean("Methods.Command", false);
        allowSign = conf.getBoolean("Methods.Sign", false);

        signPrefix = ChatColor.translateAlternateColorCodes('&', conf.getString("Sign.Prefix", ""));
        signIntervalMilliseconds = conf.getLong("Sign.IntervalMilliseconds", 1000L);

        joinMessage = conf.getString("Chats.JoinMessage", null);
        resultTitle = conf.getString("Chats.ResultTitle", null);
        resultChat = conf.getString("Chats.ResultChat", null);
        if (joinMessage != null)
            joinMessage = Chat.f(joinMessage);
        if (resultTitle != null)
            resultTitle = Chat.f(resultTitle);
        if (resultChat != null)
            resultChat = Chat.f(resultChat);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (conf.getBoolean("AutoEnable.Enable", false)) {
            try {
                Date date = dateFormat.parse(conf.getString("AutoEnable.Date"));

                autoEnable = Calendar.getInstance();
                autoEnable.setTime(date);
            } catch (ParseException e) {
                plugin.getLogger().warning("AutoEnable の読み込みに失敗しました。不正なフォーマットです。");
                return;
            }
        }
        if (conf.getBoolean("AutoDisable.Enable", false)) {
            try {
                Date date = dateFormat.parse(conf.getString("AutoDisable.Date"));

                autoDisable = Calendar.getInstance();
                autoDisable.setTime(date);
            } catch (ParseException e) {
                plugin.getLogger().warning("AutoDisable の読み込みに失敗しました。不正なフォーマットです。");
                return;
            }
        }

        HashMap<Double, OmikujiResult> results = new HashMap<>();

        double currentPercentage = 0;
        int priority = 0; // 数が低い方が優先度が高い
        for (String key : conf.getConfigurationSection("Results").getKeys(false)) {
            if (!isValid(conf, "Results." + key)) {
                continue;
            }

            String displayTitle = Chat.f(conf.getString("Results." + key + ".DisplayTitle", ""));
            double percentage = conf.getDouble("Results." + key + ".Percentage");
            List<ItemStack> items = new ArrayList<>();

            if (displayTitle.equals("")) {
                displayTitle = key;
            }

            if (plugin.getResultItemLoader().hasItemList(key)) {
                items = plugin.getResultItemLoader().getItemList(key);
            }

            results.put(currentPercentage, new OmikujiResult(plugin, key, displayTitle, percentage, items, priority));
            currentPercentage += percentage;
            priority++;
        }

        if (currentPercentage != 100) {
            plugin.getLogger().warning("確率の合計が100ではありません！ (" + currentPercentage + ")");
            plugin.getLogger().warning("不正な設定のため、おみくじ機能を無効化します。");
        }

        resultContainer = new ResultContainer(results);
    }

    private boolean isValid(YamlConfiguration conf, String key) {
        boolean valid = true;
        if (!conf.isSet(key + ".DisplayTitle")) {
            plugin.getLogger().warning(key + ".DisplayTitle が指定されていません！");
            valid = false;
        }
        if (!conf.isSet(key + ".Percentage")) {
            plugin.getLogger().warning(key + ".Percentage が指定されていません！");
            valid = false;
        }
        if (conf.getDouble(key + ".Percentage") <= 0) {
            plugin.getLogger().warning(key + ".Percentage は0より大きな数でなければいけません！");
            valid = false;
        }
        if (conf.getDouble(key + ".Percentage") > 100) {
            plugin.getLogger().warning(key + ".Percentage は100以下でなければいけません！");
            valid = false;
        }

        return valid;
    }
}
