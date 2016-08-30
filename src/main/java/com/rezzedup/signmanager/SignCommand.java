package com.rezzedup.signmanager;

import com.rezzedup.signmanager.clipboard.Clipboard;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SignCommand implements CommandExecutor
{
    private final SignManager plugin;

    public SignCommand(SignManager plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            Send.message(Send.Mode.ERROR, sender, "Only players may execute this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("signmanager.use"))
        {
            Send.message(Send.Mode.ERROR, sender, "You don't have permission to use this!");
            return true;
        }
        else if (args.length < 1)
        {
            return usage(sender);
        }

        switch (args[0].toLowerCase())
        {
            case "set":
            case "setline":
            case "line":
            case "setln":
                return setLine(player, args);

            case "copy":
            case "copysign":
            case "cp":
            case "cpsign":
                return copy(player, args);

            case "copyline":
            case "copyln":
            case "cpln":
            case "cpline":
                return copyLine(player, args);

            case "cancel":
            case "stop":
            case "clear":
            case "exit":
            case "done":
            case "kill":
            case "die":
                return cancel(player, args);

            default:
                return usage(sender);
        }
    }

    boolean usage(CommandSender sender)
    {
        String help =
            "&oUsage\n" +
            "&r&m------------------------------------&r\n" +
            " &r&b/sign set &o<line number> <text>\n" +
            " &r&b/sign copy &8&o[optional: &b&o<pastes>&8&o]\n" +
            "    &r&3Tip:&o Set <pastes> to any number less than 1\n" +
            "         &r&3for unlimited pastes.\n" +
            " &r&b/sign copyline &o<line number> &8&o[optional: &b&o<pastes>&8&o]\n" +
            " &r&b/sign cancel\n" +
            "   &r&3Tip:&o Use this to clear your Clipboard.\n" +
            "&r&m------------------------------------";

        Send.message(Send.Mode.NORMAL, sender, help);
        return true;
    }

    int parseLine(Player player, String arg)
    {
        int line = 1;

        try
        {
            line = Integer.parseInt(arg);

            if (line < 1 || line > 4)
            {
                line = 1;
                throw new IllegalStateException();
            }
        }
        catch (NumberFormatException e)
        {
            Send.message(Send.Mode.ERROR, player, "\"" + arg + "\" is invalid. Defaulting to &oline 1&c.");
        }
        catch (IllegalStateException e)
        {
            Send.message(Send.Mode.ERROR, player, "Line number must be 1-4.");
        }

        return line;
    }

    int parsePastes(Player player, String arg)
    {
        try
        {
            return Integer.parseInt(arg);
        }
        catch (NumberFormatException e)
        {
            Send.message(Send.Mode.ERROR, player, "\"" + arg + "\" is invalid. Defaulting to &o1&c.");
            return 1;
        }
    }

    boolean setLine(Player player, String[] args)
    {
        if (args.length <= 2)
        {
            Send.message(Send.Mode.ERROR, player, "Missing content to set.");
            return true;
        }

        int line = parseLine(player, args[1]);

        String content = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        Clipboard clipboard = plugin.getClipboard(player);

        clipboard.clear();
        clipboard.getStorage().updateSingleLine(line - 1, content);
        clipboard.setPastes(1);
        Send.message(Send.Mode.NORMAL, player, "Copied to clipboard. Click a sign to paste.");

        return true;
    }

    boolean copy(Player player, String[] args)
    {
        int pastes = (args.length > 1) ? parsePastes(player, args[1]) : 1;
        Clipboard clipboard = plugin.getClipboard(player);

        clipboard.clear();
        clipboard.setCopying(true);
        clipboard.setVerbatim(true);
        clipboard.setPastes(pastes);

        Send.message(Send.Mode.NORMAL, player, "Click a sign to copy.");

        return true;
    }

    boolean copyLine(Player player, String[] args)
    {
        if (args.length <= 1)
        {
            Send.message(Send.Mode.ERROR, player, "Missing line number,");
            return true;
        }

        int line = parseLine(player, args[1]);
        int pastes = (args.length > 2) ? parsePastes(player, args[2]) : 1;
        Clipboard clipboard = plugin.getClipboard(player);

        clipboard.clear();
        clipboard.setCopying(true);
        clipboard.setCopyLine(line - 1);
        clipboard.setPastes(pastes);

        Send.message(Send.Mode.NORMAL, player, "Click a sign to copy.");

        return true;
    }

    boolean cancel(Player player, String[] args)
    {
        plugin.getClipboard(player).clear();
        Send.message(Send.Mode.NORMAL, player, "Cleared your clipboard.");
        return true;
    }
}
