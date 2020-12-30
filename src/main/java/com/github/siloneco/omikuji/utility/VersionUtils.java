package com.github.siloneco.omikuji.utility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.lang.reflect.InvocationTargetException;

public class VersionUtils {

    private static int VERSION = Integer.parseInt((Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".")
            .substring(3).substring(0, (Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + ".").substring(3).indexOf("_")));

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
}
