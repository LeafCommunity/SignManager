package space.rezz.signmanager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import space.rezz.signmanager.Send.messageMode;

public class Clipboard {
    private static HashMap<UUID, String[]> storage = new HashMap<UUID, String[]>();
    private static HashMap<UUID, Integer> pastes = new HashMap<UUID, Integer>();
    private static HashMap<UUID, Integer> copy = new HashMap<UUID, Integer>();
    private static ArrayList<UUID> verbatim = new ArrayList<UUID>();
    
    // Check if a player has clipboard data
    public static boolean hasLines(Player player){
        return storage.containsKey(player.getUniqueId());
    }
    // Check if a player has pastes remaining
    public static boolean hasPastes(Player player){
        return pastes.containsKey(player.getUniqueId());
    }
    // Check the amount of pastes a player has left
    public static int getPastes(Player player){
        return pastes.get(player.getUniqueId());
    }
    // Adjust a player's paste count
    public static void setPastes(Player player, int pasteCount){
        pastes.put(player.getUniqueId(), pasteCount);
    }
    // Check if a player is currently copying a sign
    public static boolean isCopying(Player player){
        return copy.containsKey(player.getUniqueId());
    }
    // Check the line a player is copying
    // (returns 0 if a player is copying the whole sign)
    public static int getCopyLine(Player player){
        return copy.get(player.getUniqueId());
    }
    /* 
     * Set the player's sign copy status.
     * Default copy amount is an entire sign.
     * Use setCopyLine to only copy a specific line.
     */
    public static void setCopyStatus(Player player, Boolean status){
        if (status) copy.put(player.getUniqueId(), 0);
        else copy.remove(player.getUniqueId());
    }
    // Set the specific line for a player to copy.
    public static void setCopyLine(Player player, int line){
        if (line > 4) line = 4;
        else if (line < 0) line = 1;
        copy.put(player.getUniqueId(), line);
    }
    // Check if a player's pasting mode is verbatim
    public static boolean verbatim(Player player){
        return verbatim.contains(player.getUniqueId());
    }
    // Set a player's verbatim pasting status
    // ---
    // Verbatim copying/pasting means it will include empty lines
    public static void setVerbatimStatus(Player player, Boolean status){
        if (status) verbatim.add(player.getUniqueId());
        else verbatim.remove(player.getUniqueId());
    }
    /*
     *  Set a player's sign clipboard.
     *  Method arguments:
     *      - player
     *      - memory: amount of pastes for clipboard to last (usually 1)
     *      - lines: a string array with the sign lines
     */
    public static void set(Player player, int memory, String[] lines){
        storage.put(player.getUniqueId(), lines);
        pastes.put(player.getUniqueId(), memory);
        if (isCopying(player)){
            if (getCopyLine(player) < 1 || getCopyLine(player) > 4) setVerbatimStatus(player, true);
            else setVerbatimStatus(player, false);
            setCopyStatus(player, false);
        } 
        else setVerbatimStatus(player, false);
    }
    // View the content of a player's clipboard without decrementing pastes.
    public static String[] viewLines(Player player){
        return storage.get(player.getUniqueId());
    }
    /*
     *  Grab the contents from a player's clipboard.
     *  It will decrease the paste count & remove clipboard data automatically.
     */
    public static String[] getLines(Player player){
        UUID uuid = player.getUniqueId();
        String[] lines = viewLines(player);
        int remaining = getPastes(player);
        if (remaining == 1){
            storage.remove(uuid);
            pastes.remove(uuid);
            if (verbatim(player)) player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&9&oClipboard is empty."));
        }
        else if (remaining > 1){
            pastes.put(uuid, remaining - 1);
            Send.message(player, messageMode.BLANK, "Remaining Pastes: &b&o" + (remaining - 1));
        }
        return lines;
    }
    /*
     * Clear a player's clipboard
    */
    public static void clear(Player player){
        UUID uuid = player.getUniqueId();
        storage.remove(uuid);
        pastes.remove(uuid);
        if (isCopying(player)) setCopyStatus(player, false);
        if (verbatim(player)) setVerbatimStatus(player, false);
    }
    // A method to return a single-line message of the player's clipboard
    public static String getFormattedLinesMessage(Player player){
        String[] lines = viewLines(player);
        String singleLineLines = "";
        int signLineNumber = 1;
        for(String line : lines){
            if (!line.equalsIgnoreCase("")){
                singleLineLines += "&8" + signLineNumber + ": &7" + line + " &8| ";
            }
            signLineNumber++;
        }
        return singleLineLines;
    }
}
