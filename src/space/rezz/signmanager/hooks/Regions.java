package space.rezz.signmanager.hooks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import space.rezz.signmanager.hooks.regions.WorldGuard;

public class Regions {
	private static WorldGuard wg = new WorldGuard();
	
	public static boolean canBuild(Player player, Block block){
		return (wg.canBuild(player, block));
	}
	
	public static void load(){
		// just get this class in memory <3
	}
}
