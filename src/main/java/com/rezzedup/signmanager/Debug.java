package com.rezzedup.signmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Debug
{
    private static boolean enabled = true;

    public static void shout(String message)
    {
        Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', message), "");
    }
}
