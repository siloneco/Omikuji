package com.github.siloneco.omikuji.utility;

import java.lang.reflect.InvocationTargetException;
import me.rayzr522.jsonmessage.JSONMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class VersionUtils {

    private static int VERSION = Integer.parseInt((Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".")
            .substring(3).substring(0, (Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".").substring(3).indexOf("_")));

    public static int getVersion() {
        return VERSION;
    }

    public static String getInventoryTitle(InventoryView view, Inventory inv) {
        if (VERSION >= 14) {
            return view.getTitle();
        } else {
            try {
                return (String) Inventory.class.getMethod("getTitle").invoke(inv);
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exception) {
                exception.printStackTrace();
                return null;
            }
        }
    }

    public static boolean isSign(Block block) {
        if (VERSION >= 13) {
            return block.getType().toString().endsWith("_SIGN");
        } else {
            return block.getType() == Material.valueOf("SIGN_POST") || block.getType() == Material.valueOf("WALL_SIGN");
        }
    }

    public static void playLevelUpSound(Player p) {
        Sound sound;
        if (VERSION > 8) {
            sound = Sound.ENTITY_PLAYER_LEVELUP;
        } else {
            sound = Sound.valueOf("LEVEL_UP");
        }
        p.playSound(p.getLocation(), sound, 1, 1);
    }

    public static void sendTitle(String title, int fadeIn, int stay, int fadeOut, Player... players) {
        if (VERSION <= 18) {
            JSONMessage.create(title).title(0, 20, 10, players);
        } else {
            for (Player p : players) {
                p.sendTitle(title, "", fadeIn, stay, fadeOut);
            }
        }
    }
}
