/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.holograms;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import community.leaf.signmanager.SignManagerPlugin;
import community.leaf.signmanager.util.MinecraftVersions;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class ProtocolLibHologramSource implements HologramSource
{
	private static final int MIN_ID = Integer.MAX_VALUE / 2;
	
	private final SignManagerPlugin plugin;
	private final ProtocolManager protocol;
	
	public ProtocolLibHologramSource(SignManagerPlugin plugin)
	{
		this.plugin = plugin;
		this.protocol = ProtocolLibrary.getProtocolManager();
	}
	
	private void logPacketException(PacketType type, Throwable e)
	{
		plugin.getLogger().log(Level.WARNING, "Could not send packet: " + type, e);
	}
	
	private void sendPacket(Player viewer, PacketContainer packet)
	{
		try { protocol.sendServerPacket(viewer, packet); }
		catch (InvocationTargetException e) { logPacketException(packet.getType(), e); }
	}
	
	@Override
	public boolean supportsLocalHolograms() { return true; }
	
	@Override
	public Hologram showHologram(Player viewer, Location location, BaseComponent[] text)
	{
		// Height: 1.975 blocks
		Location feet = location.clone().subtract(0, 1.975, 0);
		int entityId = ThreadLocalRandom.current().nextInt(MIN_ID, Integer.MAX_VALUE);
		
		// Packet #1: spawn the fake armor stand
		// https://wiki.vg/Protocol#Spawn_Living_Entity
		PacketContainer spawn = protocol.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
		
		spawn.getIntegers().write(0, entityId);
		spawn.getUUIDs().write(0, UUID.randomUUID());
		
		// https://wiki.vg/Entity_metadata#Mobs
		spawn.getIntegers().write(1, 1); // ArmorStand ID: 1
		
		spawn.getDoubles().write(0, feet.getX());
		spawn.getDoubles().write(1, feet.getY());
		spawn.getDoubles().write(2, feet.getZ());
		
		sendPacket(viewer, spawn);
		
		// Packet #2: send fake armor stand metadata
		// https://wiki.vg/Protocol#Entity_Metadata
		PacketContainer metadata = protocol.createPacket(PacketType.Play.Server.ENTITY_METADATA);
		metadata.getIntegers().write(0, entityId);
		
		WrappedDataWatcher watcher = new WrappedDataWatcher();
		
		// https://wiki.vg/Entity_metadata#Entity
		// Bit mask 0x20 = invisible
		watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x20);
		
		// Custom name
		watcher.setObject(
			new WrappedDataWatcher.WrappedDataWatcherObject(2, WrappedDataWatcher.Registry.getChatComponentSerializer(true)),
			Optional.of(WrappedChatComponent.fromJson(ComponentSerializer.toString(text)).getHandle())
		);
		
		// Make custom name visible
		watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(3, WrappedDataWatcher.Registry.get(Boolean.class)), true);
		
		// No gravity
		watcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(5, WrappedDataWatcher.Registry.get(Boolean.class)), true);
		
		metadata.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
		
		sendPacket(viewer, metadata);
	
		return new LocalHologram(viewer, feet, entityId);
	}
	
	class LocalHologram implements Hologram
	{
		private final Player viewer;
		private final Location location;
		private final int entityId;
		
		private boolean isDestroyed = false;
		
		LocalHologram(Player viewer, Location location, int entityId)
		{
			this.viewer = viewer;
			this.location = location;
			this.entityId = entityId;
		}
		
		@Override
		public boolean isLocal() { return true; }
		
		@Override
		public Player viewer() { return viewer; }
		
		@Override
		public Location location() { return location; }
		
		@Override
		public boolean isDestroyed() { return isDestroyed; }
		
		@Override
		public void destroy()
		{
			// Packet #3: destroy hologram
			// https://wiki.vg/Protocol#Destroy_Entities
			PacketContainer destroy = protocol.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
			
			if (plugin.serverVersion().lessThan(MinecraftVersions.V1_17_0))
			{
				destroy.getIntegerArrays().write(0, new int[] { entityId });
			}
			else if (plugin.serverVersion().equals(MinecraftVersions.V1_17_0))
			{
				destroy.getIntegers().write(0, entityId);
			}
			else // > 1.17.0
			{
				destroy.getIntLists().write(0, List.of(entityId));
			}
			
			try
			{
				protocol.sendServerPacket(viewer, destroy);
				isDestroyed = true;
			}
			catch (InvocationTargetException e)
			{
				logPacketException(destroy.getType(), e);
			}
		}
	}
}