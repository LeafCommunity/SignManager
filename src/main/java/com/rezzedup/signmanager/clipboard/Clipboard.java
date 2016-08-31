package com.rezzedup.signmanager.clipboard;

import com.rezzedup.signmanager.Debug;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;

import java.util.List;

public class Clipboard
{
    private final LineStorage storage = new LineStorage();
    private boolean copying = false;
    private int copyLine = 0;
    private boolean verbatim = false;
    private int pastes = 0;

    public void setCopying(boolean set)
    {
        copying = set;
    }

    public boolean isCopying()
    {
        return copying;
    }

    public void copy(Sign sign)
    {
        storage.updateLines(sign.getLines());
        copying = false;
    }

    public int getCopyLine()
    {
        return copyLine;
    }

    public void setCopyLine(int line)
    {
        if (line > 3 || line < 0)
        {
            throw new IllegalStateException("Expected index of 0 through 3, got " + line);
        }
        copyLine = line;
    }

    public void setVerbatim(boolean set)
    {
        verbatim = set;
    }

    public boolean isVerbatim()
    {
        return verbatim;
    }

    public LineStorage getStorage()
    {
        return storage;
    }

    public boolean hasPastes()
    {
        return pastes != 0;
    }

    public int getPastes()
    {
        return pastes;
    }

    public void setPastes(int pastes)
    {
        this.pastes = pastes;
    }

    public void setUnlimitedPastes()
    {
        pastes = -1;
    }

    public boolean hasUnlimitedPastes()
    {
        return pastes < 0;
    }

    public List<String> paste()
    {
        pastes -= 1;
        return this.storage.getLines();
    }

    public void paste(Sign sign)
    {
        paste(sign, false);
    }

    public void paste(Sign sign, boolean colorize)
    {
        List<String> lines = paste();

        for (int i = 0; i < 4; i++)
        {
            String line;

            try
            {
                line = lines.get(i);
            }
            catch (IndexOutOfBoundsException e)
            {
                line = "";
            }

            if (!verbatim)
            {
                if (line.isEmpty())
                {
                    continue;
                }
            }

            sign.setLine(i, (colorize) ? ChatColor.translateAlternateColorCodes('&', line) : line);
        }
        sign.update();
    }

    public void clear()
    {
        copying = false;
        copyLine = 0;
        verbatim = false;
        pastes = 0;
        storage.clear();
    }

    public boolean hasContent()
    {
        return storage.getLines().size() > 0;
    }

    public String getContentsMessage()
    {
        String message = "";
        int line = 1;

        for (String content : storage.getLines())
        {
            if (!content.isEmpty())
            {
                message += "&8" + line + ": &7" + content + " &8| ";
            }
            line += 1;
        }

        return message;
    }

}
