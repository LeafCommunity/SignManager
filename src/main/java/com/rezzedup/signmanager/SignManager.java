package com.rezzedup.signmanager;

import java.io.IOException;

import com.rezzedup.signmanager.events.SignChange;
import com.rezzedup.signmanager.hooks.Loggers;
import com.rezzedup.signmanager.hooks.Regions;
import com.rezzedup.signmanager.metrics.MetricsLite;
import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import com.rezzedup.signmanager.Send.messageMode;
import com.rezzedup.signmanager.commands.SignCmd;
import com.rezzedup.signmanager.events.Join;
import com.rezzedup.signmanager.events.SignClick;

public class SignManager extends JavaPlugin{
    
    private static SignManager instance;
    private final Server server = getServer();
    
    public SignManager(){
        instance = this;
    }
    
    public static SignManager getInstance(){
        return instance;
    }
    
    @Override
    public void onEnable(){
        server.getPluginManager().registerEvents(new SignChange(), this);
        server.getPluginManager().registerEvents(new SignClick(), this);
        server.getPluginManager().registerEvents(new Join(), this);
        this.getCommand("sign").setExecutor(new SignCmd());
        
        Send.status(messageMode.NORMAL, "Loaded SignManager &6by RezzedUp");
        
        Regions.load();
        Loggers.load();
        
        try {
            MetricsLite metrics = new MetricsLite(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
        
    }
    
    @Override
    public void onDisable(){
        Send.status(messageMode.NORMAL, "Unloaded.");
    }
}
