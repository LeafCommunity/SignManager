package space.rezz.signmanager;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class Send {
	public static enum messageMode{
		NORMAL, ERROR, INFO, BLANK;
	}
	
	public static void status(messageMode mode, String message){
		ConsoleCommandSender console = SignManager.getInstance().getServer().getConsoleSender();
		String msg = "";
		
		switch (mode){
			case NORMAL: 	msg = "[SignManager] &e" + message;
						 	break;
			case ERROR:		msg = "[SignManager] &cError:&r " + message;
							break;
			case INFO:		msg = "[SignManager] Info:&r &b" + message;
							break;
			default:		// BLANK mode
							msg = "[SignManager] " + message;
							break;
		}
		
		console.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	
	public static void message(Player player, messageMode mode, String message){
		String msg = "";
		
		switch (mode){
			case NORMAL: 	msg = "SignManager &l>&r &3" + message;
						 	break;
			case ERROR:		msg = "SignManager &l>&r &oError: &c" + message;
							break;
			default:		// BLANK mode
							msg = message;
							break;
		}
		
		player.sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
}
