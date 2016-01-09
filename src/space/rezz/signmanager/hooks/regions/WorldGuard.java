package space.rezz.signmanager.hooks.regions;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import space.rezz.signmanager.Send;
import space.rezz.signmanager.SignManager;
import space.rezz.signmanager.Send.messageMode;

public class WorldGuard implements RegionPlugin{
	
	private Plugin plugin;
	private WorldGuardPlugin wg;
	private boolean loaded = false;
	
	public WorldGuard() {
		this.plugin = SignManager.getInstance().getServer().getPluginManager().getPlugin("WorldGuard");
		if (!(plugin == null)){
			this.wg = (WorldGuardPlugin) plugin;
			this.loaded = true;
			Send.status(messageMode.INFO, "Enabled support for WorldGuard.");
		}
	}
	
	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}
	
	public WorldGuardPlugin getWorldGuardPlugin(){
		return this.wg;
	}
	
	@Override
	public boolean canBuild(Player player, Block block) {
		return (loaded) ? this.wg.canBuild(player, block) : true;
	}
}
