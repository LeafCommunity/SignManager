package com.rezzedup.signmanager.event;

import com.rezzedup.signmanager.BuildPermission;
import com.rezzedup.signmanager.Send;
import com.rezzedup.signmanager.SignManager;
import com.rezzedup.signmanager.clipboard.Clipboard;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignCopyPasteListener implements Listener
{
    private final SignManager plugin;

    public SignCopyPasteListener(SignManager plugin)
    {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent event)
    {
        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        if (event.isCancelled())
        {
            return;
        }

        switch (block.getType())
        {
            case SIGN:
            case SIGN_POST:
            case WALL_SIGN:
                BlockState state = block.getState();

                if (state instanceof Sign)
                {
                    Sign sign = (Sign) state;
                    Clipboard clipboard = plugin.getClipboard(player);

                    if (clipboard.isCopying())
                    {
                        handleCopy(player, clipboard, block, sign, event);
                    }
                    else if (clipboard.hasPastes())
                    {
                        handlePaste(player, clipboard, block, sign, event);
                    }
                }
        }
    }

    private void handleCopy(Player player, Clipboard clipboard, Block block, Sign sign, Cancellable event)
    {
        if (BuildPermission.check(player, block))
        {
            if (clipboard.isVerbatim())
            {
                clipboard.copy(sign);
            }
            else
            {
                int line = clipboard.getCopyLine();
                String content = sign.getLine(line);

                if (content.isEmpty())
                {
                    Send.message(Send.Mode.ERROR, player, "The line you're trying to copy is empty!");
                    event.setCancelled(true);
                    return;
                }

                clipboard.getStorage().updateSingleLine(line, content);
                clipboard.setCopying(false);
            }

            event.setCancelled(true);
            Send.message
            (
                Send.Mode.NORMAL, player,
                "Copied to clipboard. Click a sign to paste.\n" + clipboard.getContentsMessage()
            );
        }
    }

    private void handlePaste(Player player, Clipboard clipboard, Block block, Sign sign, Cancellable event)
    {
        if (BuildPermission.check(player, block))
        {
            clipboard.paste(sign, player.hasPermission("signmanager.colors"));

            if (clipboard.hasPastes())
            {
                if (clipboard.hasUnlimitedPastes())
                {
                    Send.message(Send.Mode.NORMAL, player, "Remaining Pastes: &b&oUnlimited");
                }
                else
                {
                    Send.message(Send.Mode.NORMAL, player, "Remaining Pastes: &b&o" + clipboard.getPastes());
                }
            }
            event.setCancelled(true);
            Bukkit.getPluginManager().callEvent(new SignChangeEvent(block, player, sign.getLines()));
        }
    }
}
