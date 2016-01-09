package space.rezz.signmanager;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import space.rezz.signmanager.Send.messageMode;
import space.rezz.signmanager.commands.SignCmd;
import space.rezz.signmanager.events.Join;
import space.rezz.signmanager.events.SignChange;
import space.rezz.signmanager.events.SignClick;
import space.rezz.signmanager.hooks.Regions;

public class SignManager extends JavaPlugin{
	
	private static SignManager instance;
	private final Server server = getServer();
	
	public SignManager(){
		instance = this;
	}
	
	public static SignManager getInstance(){
		return instance;
	}
	
	@Override
	public void onEnable(){
		server.getPluginManager().registerEvents(new SignChange(), this);
		server.getPluginManager().registerEvents(new SignClick(), this);
		server.getPluginManager().registerEvents(new Join(), this);
		this.getCommand("sign").setExecutor(new SignCmd());
		
		Send.status(messageMode.NORMAL, "Loaded SignManager &6by RezzedUp");
		
		Regions.load();
	}
	
	@Override
	public void onDisable(){
		Send.status(messageMode.NORMAL, "Unloaded.");
	}
}
