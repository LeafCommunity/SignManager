package com.rezzedup.signmanager.clipboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LineStorage
{
    private List<String> lines;

    public LineStorage()
    {
        this.lines = new ArrayList<>();
    }

    public LineStorage(List<String> lines)
    {
        updateLines(lines);
    }

    public void updateLines(String[] lines)
    {
        updateLines(new ArrayList<>(Arrays.asList(lines)));
    }

    public void updateLines(List<String> lines)
    {
        if (lines.size() > 4)
        {
            lines = lines.subList(0, 4);
        }
        this.lines = trim(lines);
    }

    public List<String> getLines()
    {
        return lines;
    }

    public void clear()
    {
        lines.clear();
    }

    public void updateSingleLine(int line, String value)
    {
        if (line > 3 || line < 0)
        {
            throw new IllegalStateException("Expected index of 0 through 3, got " + line);
        }
        lines.clear();

        for (int i = 0; i <= line; i++)
        {
            lines.add( (i != line) ? "" : trim(value) );
        }
    }
    
    private String trim(String value)
    {
        if (value.length() >= 16)
        {
            return value.substring(0, 16);
        }
        return value;
    }
    
    private List<String> trim(List<String> values)
    {
        List<String> list = new ArrayList<>();
        
        for (String value : values)
        {
            list.add(trim(value));
        }
        return list;
    }
}
