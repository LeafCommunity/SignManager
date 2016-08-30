package com.rezzedup.signmanager.event;

import com.rezzedup.signmanager.Send;
import com.rezzedup.signmanager.SignManager;
import com.rezzedup.signmanager.clipboard.Clipboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class ClipboardReminder implements Listener
{
    private SignManager plugin;

    public ClipboardReminder(SignManager plugin)
    {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();

        if (!plugin.hasClipboard(player))
        {
            return;
        }

        final Clipboard clipboard = plugin.getClipboard(player);

        if (clipboard.isCopying() || !clipboard.hasPastes())
        {
            clipboard.clear();
            return;
        }

        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                String message =
                    "You still have leftover pastes!\n" +
                    "&rRemaining pastes: &b&o" + clipboard.getPastes() + " &3. . . &bClipboard content:\n" +
                    clipboard.getContentsMessage();

                if (clipboard.hasUnlimitedPastes())
                {
                    message +=
                        "\n&c&lWARNING:&7 You have unlimited pastes remaining." +
                        "\nTo clear your clipboard, do &o/sign cancel";
                }

                Send.message(Send.Mode.NORMAL, player, message);
            }
        }
        .runTaskLater(plugin, 20L);
    }
}
