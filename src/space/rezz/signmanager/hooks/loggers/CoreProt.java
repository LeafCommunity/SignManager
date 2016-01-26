package space.rezz.signmanager.hooks.loggers;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import space.rezz.signmanager.Send;
import space.rezz.signmanager.SignManager;
import space.rezz.signmanager.Send.messageMode;

public class CoreProt implements LoggerPlugin{
    
    private Plugin plugin;
    private boolean loaded = false;
    private CoreProtectAPI cp;
    
    public CoreProt(){
        this.plugin = SignManager.getInstance().getServer().getPluginManager().getPlugin("CoreProtect");
        if (!(plugin == null)){
            cp = ((CoreProtect)plugin).getAPI();
            if (cp.isEnabled()){
                this.loaded = true;
                Send.status(messageMode.INFO, "Enabled support for CoreProtect.");
            }
        }
    }
    
    @Override
    public Plugin getPlugin(){
        return this.plugin;
    }
    
    @SuppressWarnings("deprecation")
    @Override
    public void log(Player player, Block block){
        if (this.loaded){
            @SuppressWarnings("unused")
            boolean log = this.cp.logPlacement(player.getName(), block.getLocation(), block.getType(), block.getData());
        }
    }
}
