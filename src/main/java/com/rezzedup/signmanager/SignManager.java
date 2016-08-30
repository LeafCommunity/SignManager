package com.rezzedup.signmanager;

import com.rezzedup.signmanager.clipboard.Clipboard;
import com.rezzedup.signmanager.event.ClipboardReminder;
import com.rezzedup.signmanager.event.ColorizeSigns;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignManager extends JavaPlugin
{
    private final Map<UUID, Clipboard> clipboards = new HashMap<>();

    @Override
    public void onEnable()
    {
        getCommand("sign").setExecutor(new SignCommand(this));

        new BuildPermission(this);
        new ClipboardReminder(this);
        new ColorizeSigns(this);
    }

    public Clipboard getClipboard(Player player)
    {
        return getClipboard(player.getUniqueId());
    }

    public Clipboard getClipboard(UUID uuid)
    {
        if (clipboards.containsKey(uuid))
        {
            return clipboards.get(uuid);
        }
        else
        {
            return clipboards.put(uuid, new Clipboard());
        }
    }

    public boolean hasClipboard(Player player)
    {
        return hasClipboard(player.getUniqueId());
    }

    public boolean hasClipboard(UUID uuid)
    {
        return clipboards.containsKey(uuid);
    }
}
