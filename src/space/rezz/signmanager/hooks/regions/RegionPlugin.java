package space.rezz.signmanager.hooks.regions;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface RegionPlugin {
	Plugin getPlugin();
	boolean canBuild(Player player, Block block);
}
