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

    /**
     * BuildPermission constructor - pass your plugin's instance to allow events to be registered.
     * Checking build permissions won't work if these events aren't registered.
     * @param plugin Instance of your plugin.
     */
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

    /**
     * Check if a player can build based on where they're standing.
     * The player's location is converted to a block for checking.
     * @param player Player to check.
     * @return Whether a player has permission to build where they're standing or not.
     */
    public static boolean check(Player player)
    {
        return check(player, player.getLocation());
    }

    /**
     * Check if a player can build based on a specific location.
     * The block at the given location is used for checking.
     * @param player Player to check.
     * @param location Location to check (converted to a block).
     * @return Whether a player has permission to build or not.
     */
    public static boolean check(Player player, Location location)
    {
        return check(player, location.getBlock());
    }

    /**
     * Check if a player can build based on a specific block.
     * @param player Player to check.
     * @param block Block to check.
     * @return Whether a player has permission to build or not.
     */
    public static boolean check(Player player, Block block)
    {
        BreakPermissionEvent event = new BreakPermissionEvent(block, player);
        Bukkit.getPluginManager().callEvent(event);
        return event.isAllowed();
    }
}
