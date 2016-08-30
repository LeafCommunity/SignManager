package com.rezzedup.signmanager;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Send
{
    public enum Mode
    {
        NORMAL, ERROR, INFO, BLANK;
    }

    private static String getConsolePrefix(Mode mode)
    {
        String prefix = "&f[SignManager] &r";
        switch (mode)
        {
            case NORMAL:
                return prefix + "&e";
            case ERROR:
                return prefix + "&cError: &r";
            case INFO:
                return prefix + "Info: &b";
            default:
                return  prefix;
        }
    }

    private static String getPlayerPrefix(Mode mode)
    {
        String prefix = "SignManager &l>&r ";

        switch (mode){
            case NORMAL:
                return prefix + "&3";
            case ERROR:
                return "&oError: &c";
            default:
                return prefix;
        }
    }

    private static void message(CommandSender sender, String message)
    {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void message(Mode mode, String message)
    {
        message(Bukkit.getServer().getConsoleSender(), getConsolePrefix(mode) + message);
    }

    public static void message(Mode mode, CommandSender sender, String message)
    {
        if (sender instanceof ConsoleCommandSender)
        {
            message(sender, getConsolePrefix(mode) + message);
        }
        else
        {
            message(sender, getPlayerPrefix(mode) + message);
        }
    }

    public static void message(Mode mode, Player player, String message)
    {
        message(mode, player, message);
    }
}
