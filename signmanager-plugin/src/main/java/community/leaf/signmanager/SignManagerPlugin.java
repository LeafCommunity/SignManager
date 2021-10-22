/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import com.github.zafarkhaja.semver.Version;
import community.leaf.eventful.bukkit.BukkitEventSource;
import community.leaf.signmanager.common.SignContentAdapterRegistry;
import community.leaf.signmanager.listeners.SignListener;
import community.leaf.tasks.bukkit.BukkitTaskSource;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tlinkowski.annotation.basic.NullOr;

import java.nio.file.Path;

public class SignManagerPlugin extends JavaPlugin implements BukkitEventSource, BukkitTaskSource
{
	private @NullOr Version version;
	private @NullOr Path rootDirectory;
	private @NullOr SignContentAdapterRegistryImpl adapters;
	
	@Override
	public void onEnable()
	{
		this.version = Version.valueOf(plugin().getDescription().getVersion());
		this.rootDirectory = getDataFolder().toPath();
		this.adapters = new SignContentAdapterRegistryImpl();
		
		adapters.add(LegacySignLine.ADAPTER);
		
		Version bukkit = Version.valueOf(Bukkit.getBukkitVersion());
		
		if (PaperLib.isPaper() && bukkit.greaterThanOrEqualTo(Version.forIntegers(1,16,5)))
		{
			getLogger().info("Running on Paper: " + Bukkit.getVersion() + " -> " + Bukkit.getBukkitVersion());
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
	
	public SignContentAdapterRegistry adapters() { return initialized(adapters); }
}
