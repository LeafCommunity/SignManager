package space.rezz.signmanager.events;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import space.rezz.signmanager.Clipboard;
import space.rezz.signmanager.Send;
import space.rezz.signmanager.Send.messageMode;
import space.rezz.signmanager.hooks.Loggers;
import space.rezz.signmanager.hooks.Regions;

public class SignClick implements Listener{
    @EventHandler
    public void onSignClick(PlayerInteractEvent event){
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();
        if (!event.isCancelled()){
            if (block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN){
                BlockState state = block.getState();
                if (state instanceof Sign){
                    Sign sign = (Sign) state;
                    /*
                     * COPY EVENT
                     */
                    if (Clipboard.isCopying(player)){
                        if (Regions.canBuild(player, block)){
                            boolean cancel = false;
                            int line = Clipboard.getCopyLine(player);
                            String[] lines = sign.getLines();
                            
                            if (line < 1 || line > 4) 
                                Clipboard.set(player, Clipboard.getPastes(player), lines);
                            else{
                                String[] tempLines = new String[4];
                                for (int i = 0; i < lines.length; i++){
                                    tempLines[i] = (i == line) ? lines[i] : "";
                                }
                                if (tempLines[line - 1].equalsIgnoreCase("")){
                                    Clipboard.clear(player);
                                    Send.message(player, messageMode.ERROR, "The line you're trying to copy is empty!");
                                    cancel = true;
                                }
                                else{
                                    Clipboard.set(player, Clipboard.getPastes(player), tempLines);
                                }
                            }
                            if (!cancel) Send.message(player, messageMode.NORMAL, "Copied to clipboard. Click a sign to paste.\n" + Clipboard.getFormattedLinesMessage(player));
                            event.setCancelled(true);
                        } else {
                            Send.message(player, messageMode.ERROR, "You can't copy this sign!");
                        }
                    }
                    /*
                     * PASTE EVENT
                     */
                    else if (Clipboard.hasLines(player)){
                        if (Regions.canBuild(player, block)){
                            String[] lines = Clipboard.getLines(player);
                            int i = 0;
                            for (i = 0; i < lines.length; i++){
                                String line = lines[i];
                                if (!Clipboard.verbatim(player)){
                                    // Skip current loop iteration if the line has no content,
                                    // and the player's paste mode is not verbatim (including blank lines)
                                    if (line.equalsIgnoreCase("")) continue;
                                }
                                if (player.hasPermission("signmanager.colors")) sign.setLine(i,  
                                        ChatColor.translateAlternateColorCodes('&', line));
                                else sign.setLine(i, line);
                            }
                            sign.update();
                            event.setCancelled(true);
                            Loggers.log(player, block);
                        } else {
                            Send.message(player, messageMode.ERROR, "You can't paste a sign here!");
                        }
                    }
                }
            }
        }
    }
}
