package com.rezzedup.signmanager;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

public class BuildPermission implements Listener
{
    private static class BreakPermissionEvent extends BlockBreakEvent
    {
        private boolean allowed = true;

        BreakPermissionEvent(Block block, Player player)
        {
            super(block, player);
        }

        void setAllowed(boolean access)
        {
            this.allowed = access;
        }

        boolean isAllowed()
        {
            return this.allowed;
        }
    }

    public BuildPermission(Plugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent event)
    {
        if (event instanceof BreakPermissionEvent)
        {
            BreakPermissionEvent e = (BreakPermissionEvent) event;

            if (e.isCancelled())
            {
                e.setAllowed(false);
            }
            e.setCancelled(true);
        }
    }

    public static boolean check(Player player)
    {
        return check(player, player.getLocation());
    }

    public static boolean check(Player player, Location location)
    {
        return check(player, location.getBlock());
    }

    public static boolean check(Player player, Block block)
    {
        BreakPermissionEvent event = new BreakPermissionEvent(block, player);
        Bukkit.getPluginManager().callEvent(event);
        return event.isAllowed();
    }
}
