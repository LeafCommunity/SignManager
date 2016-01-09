package space.rezz.signmanager.hooks.regions;

import java.util.List;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import space.rezz.signmanager.Send;
import space.rezz.signmanager.Send.messageMode;
import space.rezz.signmanager.SignManager;

public class GriefPrev implements RegionPlugin{
	
	private Plugin plugin;
	private boolean loaded = false;
	
	public GriefPrev(){
		this.plugin = SignManager.getInstance().getServer().getPluginManager().getPlugin("GriefPrevention");
		if (!(plugin == null)){
			this.loaded = true;
		}
	}

	@Override
	public Plugin getPlugin() {
		return this.plugin;
	}

	@Override
	public boolean canBuild(Player player, Block block) {
		boolean build = true;
		if (loaded){
			Claim claim = GriefPrevention.instance.dataStore.getClaimAt(block.getLocation(), true, null);
			if (!claim.ownerID.equals(player.getUniqueId())) build = false;
			List<String> managers = claim.managers;
			for (String manager : managers){
				Send.status(messageMode.INFO, "<Debug> manager: " + manager);
				if (manager.equalsIgnoreCase(player.getName())) return true;
			}
		}
		return build;
	}

}
