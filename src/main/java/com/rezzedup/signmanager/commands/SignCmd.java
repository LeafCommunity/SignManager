package com.rezzedup.signmanager.commands;

import com.rezzedup.signmanager.Clipboard;
import com.rezzedup.signmanager.Send;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SignCmd implements CommandExecutor{    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("signmanager.command")){
                if (args.length > 0){
                    /*
                     *   /sign help
                     *   /sign ?
                     *   /sign usage
                     *   /sign commands
                     */
                    if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("usage") || args[0].equalsIgnoreCase("commands")){
                        String help = "&oUsage\n";
                        help += "&r&m------------------------------------&r\n";
                        help += " &r&b/sign set &o<line number> <text>\n";
                        help += " &r&b/sign copy &8&o[optional: &b&o<pastes>&8&o]\n";
                        help += "    &r&3Tip:&o Set <pastes> to any number less than 1\n";
                        help += "         &r&3for unlimited pastes.\n";
                        help += " &r&b/sign copyline &o<line number> &8&o[optional: &b&o<pastes>&8&o]\n";
                        help += " &r&b/sign cancel\n";
                        help += "   &r&3Tip:&o Use this to clear your Clipboard.\n";
                        help += "&r&m------------------------------------";
                        Send.message(player, Send.messageMode.NORMAL, help);
                    }
                    /*
                     *   /sign set
                     *   /sign setline
                     *   /sign line
                     */
                    else if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("setline") || args[0].equalsIgnoreCase("line")){
                        if (args.length > 2){
                            int playerLine = 1;
                            try{
                                playerLine = Integer.parseInt(args[1]);
                            }
                            catch(NumberFormatException exception){
                                Send.message(player, Send.messageMode.ERROR, "\"" + args[1] + "\" is invalid. Defaulting to &oline 1&c.");
                            }
                            if (playerLine > 0 && playerLine < 5){
                                if (Clipboard.isCopying(player)) Clipboard.setCopyStatus(player, false);
                                if (Clipboard.verbatim(player)) Clipboard.setVerbatimStatus(player, false);
                                
                                int line = playerLine - 1;
    
                                int argCheck = 2;
                                String lineText = "";
                                while(argCheck <= args.length - 1){
                                    if (lineText.equalsIgnoreCase("")){
                                        lineText += args[argCheck];
                                    }else{
                                        lineText += " " + args[argCheck];
                                    }
                                    argCheck += 1;
                                }
                                
                                String[] lines = new String[4];
                                for (int i = 0; i < 4; i++){
                                    lines[i] = (line == i) ? lineText : "";
                                }
                                Clipboard.set(player, 1, lines);
                                
                                Send.message(player, Send.messageMode.NORMAL, "Copied to clipboard. Click a sign to paste.");
                                
                            }else Send.message(player, Send.messageMode.ERROR, "Line number must be 1-4.");
                            
                        }else Send.message(player, Send.messageMode.ERROR, "Missing content to set.");
                    }
                    /*
                     *   /sign copy
                     */
                    else if (args[0].equalsIgnoreCase("copy")){
                        // Clipboard persistence
                        int persistence = 1;
                        // If the persistence arg is set:
                        if (args.length > 1){
                            try{
                                persistence = Integer.parseInt(args[1]);
                            }
                            catch(NumberFormatException exception){
                                Send.message(player, Send.messageMode.ERROR, "\"" + args[1] + "\" is invalid. Defaulting to &o1&c.");
                            }
                        }
                        Clipboard.setCopyStatus(player, true);
                        Clipboard.setPastes(player, persistence);
                        Send.message(player, Send.messageMode.NORMAL, "Click a sign to copy.");
                    }
                    //copy specific sign line
                    /*
                     *   /sign copyline
                     *   /sign cpln
                     */
                    else if (args[0].equalsIgnoreCase("copyline") || args[0].equalsIgnoreCase("cpln")){
                        int line = 1;
                        int persistence = 1;
                        for (int i = 1; i <= 2; i++){
                            if (args.length > i){
                                try{
                                    if (i == 1) line = Integer.parseInt(args[i]);
                                    else persistence = Integer.parseInt(args[i]);
                                }
                                catch(NumberFormatException exception){
                                    Send.message(player, Send.messageMode.ERROR, "\"" + args[i] + "\" is invalid. Defaulting to &o1&c.");
                                }
                            } else if (i < 2) Send.message(player, Send.messageMode.ERROR, "Defaulting to &oline 1&c.");
                        }
                        if (line > 0 && line < 5){
                            Clipboard.setCopyLine(player, line);
                            Clipboard.setPastes(player, persistence);
                            Send.message(player, Send.messageMode.NORMAL, "Click a sign to copy.");
                        } else Send.message(player, Send.messageMode.ERROR, "Line must be 1-4.");
                    }
                    /*
                     *   /sign cancel
                     *   /sign stop
                     *   /sign clear
                     */
                    else if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("stop") || args[0].equalsIgnoreCase("clear")){
                        Clipboard.clear(player);
                        Send.message(player, Send.messageMode.NORMAL, "Cleared your clipboard.");
                    }
                    // Invalid argument
                    else {
                        Send.message(player, Send.messageMode.ERROR, "Unknown argument.");
                    }
                // No arguments given
                } else {
                    Send.message(player, Send.messageMode.NORMAL, "For command usage, do &b/sign help");
                }
            
            // No permission
            }else Send.message(player, Send.messageMode.ERROR, "You don't have permission to use this.");
    
            return true;
        }
        else{
            // Command sender is not a player
            Send.status(Send.messageMode.NORMAL, "Only players may execute this command.");
        }
        return false;
    }
}
