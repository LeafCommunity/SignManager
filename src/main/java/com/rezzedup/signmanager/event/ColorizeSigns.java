package com.rezzedup.signmanager.event;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.Plugin;

public class ColorizeSigns implements Listener
{
    public ColorizeSigns(Plugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event)
    {
        Player player = event.getPlayer();

        if (player.hasPermission("signmanager.colors"))
        {
            String[] lines = event.getLines();

            for (int i = 0; i < lines.length; i++)
            {
                if (!lines[i].isEmpty())
                {
                    event.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
                }
            }
        }
    }
}
