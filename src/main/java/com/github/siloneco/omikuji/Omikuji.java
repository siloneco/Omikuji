package com.github.siloneco.omikuji;

import com.github.siloneco.omikuji.command.OmikujiCommand;
import com.github.siloneco.omikuji.listener.ItemRegisterListener;
import com.github.siloneco.omikuji.listener.JoinListener;
import com.github.siloneco.omikuji.listener.ResultItemViewListener;
import com.github.siloneco.omikuji.listener.SignListener;
import com.github.siloneco.omikuji.listener.WinningInventoryDisposeListener;
import com.github.siloneco.omikuji.utility.Chat;
import com.github.siloneco.omikuji.utility.MessageBridge;
import com.github.siloneco.omikuji.utility.VersionUtils;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Omikuji extends JavaPlugin {

    private OmikujiConfig pluginConfig;
    private final ResultItemLoader resultItemLoader = new ResultItemLoader(this);
    private final WinningInventoryContainer winningInventoryContainer = new WinningInventoryContainer();
    private final DrewOmikujiCounter counter = new DrewOmikujiCounter(this);
    private final OmikujiDrawingMessageUpdateTask messageTask = new OmikujiDrawingMessageUpdateTask(this);

    private final List<UUID> executingPlayers = new ArrayList<>();

    private static Economy econ = null;

    @Override
    public void onEnable() {
        resultItemLoader.load();
        reloadConfig();
        counter.load();

        Bukkit.getPluginManager().registerEvents(new ItemRegisterListener(this), this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new WinningInventoryDisposeListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ResultItemViewListener(this), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(this), this);

        Bukkit.getPluginCommand("omikuji").setExecutor(new OmikujiCommand(this));
        Bukkit.getPluginCommand("omikuji").setPermissionMessage(Chat.f("{0} &c権限がありません！", pluginConfig.getPrefix()));

        setupEconomy();

        messageTask.runTaskTimerAsynchronously(this, 5L, 5L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            // 非同期でセーブ
            new Thread(counter::save).start();
        }, 20L * 60L, 20L * 60L);

        Bukkit.getLogger().info(getName() + " enabled.");
    }

    @Override
    public void onDisable() {
        counter.save();
        Bukkit.getLogger().info(getName() + " disabled.");
    }

    public void execute(Player p) {
        boolean isExactTime = true;
        if (pluginConfig.getAutoEnable() != null && pluginConfig.getAutoEnable().after(Calendar.getInstance())) {
            isExactTime = false;
        }
        if (pluginConfig.getAutoDisable() != null && pluginConfig.getAutoDisable().before(Calendar.getInstance())) {
            isExactTime = false;
        }

        if (!p.hasPermission("omikuji.bypass.drawtimelimit") && !isExactTime) {
            p.sendMessage(Chat.f("{0} &c現在おみくじを引くことはできません！", pluginConfig.getPrefix()));
            return;
        }

        if (executingPlayers.contains(p.getUniqueId())) {
            p.sendMessage(Chat.f("{0} &a現在おみくじを引いています！", pluginConfig.getPrefix()));
            return;
        }

        int count = counter.getCount(p);
        if (pluginConfig.getMaximumAmountPerPlayer() >= 0 && count >= pluginConfig.getMaximumAmountPerPlayer()) {
            if (!p.hasPermission("omikuji.bypass.amount")) {
                p.sendMessage(Chat.f("{0} &cこれ以上おみくじを引くことはできません！ &7({1}回)", pluginConfig.getPrefix(), count));
                return;
            }

            p.sendMessage(Chat.f("{0} &a権限を持っているため、回数制限を免除しました。", pluginConfig.getPrefix()));
        }

        double cost = pluginConfig.getCost();
        final boolean paid;
        if (cost > 0) {
            if (econ == null) {
                p.sendMessage(Chat.f("{0} &c料金の徴収に失敗しました。", pluginConfig.getPrefix()));
                if (p.hasPermission("omikuji.admin")) {
                    p.sendMessage(Chat.f("{0} &r&nFor OP => &e経済(Vaultか、その先)との連携が失敗しています。", pluginConfig.getPrefix()));
                }
                return;
            }

            if (p.hasPermission("omikuji.bypass.cost")) {
                p.sendMessage(Chat.f("{0} &a権限を持っているため、&c{1} &aの支払いを免除しました。", pluginConfig.getPrefix(), econ.format(cost)));
                paid = false;
            } else {
                if (!econ.has(p, cost)) {
                    p.sendMessage(Chat.f("{0} &cお金が足りません！おみくじを引くには &e{1} &c必要です！", pluginConfig.getPrefix(), econ.format(cost)));
                    return;
                }
                EconomyResponse res = econ.withdrawPlayer(p, cost);
                if (!res.transactionSuccess()) {
                    p.sendMessage(Chat.f("{0} &c支払いに失敗しました。&r{1}", pluginConfig.getPrefix(), res.errorMessage));
                    return;
                }
                p.sendMessage(Chat.f("{0} &c{1} &aを支払いました！", pluginConfig.getPrefix(), econ.format(cost)));
                paid = true;
            }
        } else {
            paid = false;
        }

        if (!isExactTime) {
            p.sendMessage(Chat.f("{0} &a権限を持っているため、引ける期間の制限を免除しました。", pluginConfig.getPrefix()));
        }

        executingPlayers.add(p.getUniqueId());
        counter.addCount(p);

        if (pluginConfig.getDrawTicks() > 0L) {
            messageTask.display(p);
        }

        Bukkit.getScheduler().runTaskLater(this, () -> {
            executingPlayers.remove(p.getUniqueId());

            if (!p.isOnline()) {
                counter.subtractCount(p);

                if (paid) {
                    EconomyResponse res = econ.depositPlayer(p, cost);
                    if (!res.transactionSuccess()) {
                        getLogger().warning(Chat.f("{0} への {1} の払い戻しに失敗しました。", p.getName(), econ.format(cost)));
                    }
                }
                return;
            }
            OmikujiResult result;
            try {
                result = pluginConfig.getResultContainer().execute();
            } catch (NoSuchAlgorithmException e) {
                getLogger().warning("SecureRandomでの乱数生成に失敗しました。Plugin開発者に連絡してください。");
                p.sendMessage(Chat.f("{0} &cおみくじの結果を取得できませんでした。管理者に連絡してください。", pluginConfig.getPrefix()));
                return;
            }

            VersionUtils.sendTitle(getPluginConfig().getResultTitle().replace("%RESULT%", result.getDisplayTitle()).replace("%PREFIX%", pluginConfig.getPrefix()), 0, 100, 20, p);

            p.sendMessage(getPluginConfig().getResultChat().replace("%RESULT%", result.getDisplayTitle()).replace("%PREFIX%", pluginConfig.getPrefix()));

            VersionUtils.playLevelUpSound(p);

            if (result.getItems().isEmpty()) {
                return;
            }

            String secretID = winningInventoryContainer.createInventoryWithID(result).getSecretID();
            MessageBridge.create()
                    .bar().newline()
                    .then(Chat.f("{0} ", pluginConfig.getPrefix()))
                    .then(Chat.f("&e&nここをクリック")).runCommand("/omikuji openWinningInventory " + secretID)
                    .then(Chat.f("&aでアイテムをゲット！")).newline()
                    .bar()
                    .send(p);
        }, pluginConfig.getDrawTicks());

    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        super.reloadConfig();

        resultItemLoader.reloadItems();

        pluginConfig = new OmikujiConfig(this);
        pluginConfig.load();
    }

    private void setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return;
        }
        econ = rsp.getProvider();
    }
}
