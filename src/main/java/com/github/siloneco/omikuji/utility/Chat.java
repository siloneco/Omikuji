package com.github.siloneco.omikuji.utility;

import java.text.MessageFormat;
import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

/**
 * Cited: https://github.com/AzisabaNetwork/LeonGunWar/blob/master/src/main/java/net/azisaba/lgw/core/utils/Chat.java
 *
 * @author YukiLeafX
 */
@UtilityClass
public class Chat {

    // メッセージをフォーマットして、&で色をつける
    public String f(String text, Object... args) {
        return MessageFormat.format(ChatColor.translateAlternateColorCodes('&', text), args);
    }

    // 色を消す
    public String r(String text) {
        return ChatColor.stripColor(text);
    }
}