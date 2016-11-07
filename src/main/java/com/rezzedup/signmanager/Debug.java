package com.rezzedup.signmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Debug
{
    private static boolean enabled = false;
    
    public static void setEnabled(boolean value)
    {
        enabled = value;
    }

    public static void shout(String message)
    {
        Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&', message), "");
    }
}
