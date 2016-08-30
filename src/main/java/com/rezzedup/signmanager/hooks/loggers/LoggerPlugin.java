package com.rezzedup.signmanager.hooks.loggers;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface LoggerPlugin {
    Plugin getPlugin();
    void log(Player player, Block block);
}
