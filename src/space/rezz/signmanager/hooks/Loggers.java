package space.rezz.signmanager.hooks;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import space.rezz.signmanager.hooks.loggers.CoreProt;

public class Loggers {
    private static CoreProt cp = new CoreProt();
    
    public static void log(Player player, Block block){
        cp.log(player, block);
    }
    
    public static void load(){
        // just get this class in memory <3
    }
}
