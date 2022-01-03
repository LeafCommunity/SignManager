/*
 * Copyright © 2016-2022, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import com.github.zafarkhaja.semver.Version;
import community.leaf.eventful.bukkit.BukkitEventSource;
import community.leaf.signmanager.holograms.EntityHologramSource;
import community.leaf.signmanager.holograms.HologramSource;
import community.leaf.signmanager.holograms.PacketHologramSource;
import community.leaf.signmanager.listeners.SignListener;
import community.leaf.signmanager.util.MinecraftVersions;
import community.leaf.tasks.bukkit.BukkitTaskSource;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tlinkowski.annotation.basic.NullOr;

import java.nio.file.Path;

public class SignManagerPlugin extends JavaPlugin implements BukkitEventSource, BukkitTaskSource
{
    private @NullOr Version version;
    private @NullOr Path rootDirectory;
    private @NullOr HologramSource holograms;
    
    @Override
    public void onEnable()
    {
        this.version = Version.valueOf(plugin().getDescription().getVersion());
        this.rootDirectory = getDataFolder().toPath();
        
        getLogger().info("Minecraft version: " + MinecraftVersions.SERVER);
        
        if (getServer().getPluginManager().isPluginEnabled("ProtocolLib"))
        {
            getLogger().info("Using packet-based holograms (via ProtocolLib).");
            holograms = new PacketHologramSource(this);
        }
        else
        {
            getLogger().info("Using entity-based holograms.");
            holograms = new EntityHologramSource();
        }
        
        events().register(new SignListener(this));
    }
    
    @Override
    public Plugin plugin() { return this; }
    
    private <T> T initialized(@NullOr T thing)
    {
        if (thing != null) { return thing; }
        throw new NullPointerException("Not initialized yet");
    }
    
    public Version version() { return initialized(version); }
    
    public Path rootDirectory() { return initialized(rootDirectory); }
    
    public HologramSource holograms() { return initialized(holograms); }
    
    public void fallbackToEntityHolograms()
    {
        if (holograms instanceof EntityHologramSource) { return; }
        getLogger().info("Reverting to entity-based holograms.");
        holograms = new EntityHologramSource();
    }
}
