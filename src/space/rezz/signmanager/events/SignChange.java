package space.rezz.signmanager.events;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChange implements Listener{
    @EventHandler
    public void onSignChange(SignChangeEvent event){
        Player player = event.getPlayer();
        if (player.hasPermission("signmanager.colors")){
            String[] lines = event.getLines();
            for (int i = 0; i < lines.length; i++){
                if (!lines[i].isEmpty()){
                    event.setLine(i, ChatColor.translateAlternateColorCodes('&', lines[i]));
                }
            }
        }
    }
}
