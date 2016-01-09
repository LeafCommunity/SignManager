package space.rezz.signmanager.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import space.rezz.signmanager.Clipboard;
import space.rezz.signmanager.Send;
import space.rezz.signmanager.Send.messageMode;
import space.rezz.signmanager.SignManager;

public class Join implements Listener{
	@EventHandler
	public void joinEvent(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if (player.hasPermission("signmanager.command")){
			if (Clipboard.hasPastes(player)){
				new BukkitRunnable() {
					@Override
					public void run(){
						int pastes = Clipboard.getPastes(player);
						Send.message(player, messageMode.NORMAL, "You still have leftover pastes!\n" 
								+ "&rRemaining pastes: &b&o" + pastes + " &3. . . &bClipboard content:\n"
									+ Clipboard.getFormattedLinesMessage(player)); 
						if (pastes < 1){
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lWARNING:&7 You have unlimited pastes remaining.\nTo clear your clipboard, do &o/sign cancel"));
						}
					}
				}.runTaskLater(SignManager.getPlugin(SignManager.class), 20);
			}
		}
	}
}
