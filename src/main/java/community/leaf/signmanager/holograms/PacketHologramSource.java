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
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
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
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Level;

public class PacketHologramSource implements HologramSource
{
	private static final int MIN_ID = Integer.MAX_VALUE / 2;
	
	private final SignManagerPlugin plugin;
	private final ProtocolManager protocol;
	
	public PacketHologramSource(SignManagerPlugin plugin)
	{
		this.plugin = plugin;
		this.protocol = ProtocolLibrary.getProtocolManager();
	}
	
	@Override
	public boolean supportsLocalHolograms() { return true; }
	
	private boolean sendPacket(Player player, PacketType type, Consumer<PacketContainer> packer)
	{
		PacketContainer packet = protocol.createPacket(type);
		boolean success = false;
		
		try
		{
			packer.accept(packet);
			protocol.sendServerPacket(player, packet);
			success = true;
		}
		catch (InvocationTargetException e)
		{
			plugin.getLogger().log(Level.WARNING, "Could not send packet: " + type, e);
		}
		catch (RuntimeException e)
		{
			plugin.getLogger().log(Level.WARNING, "Could not create packet: " + type, e);
		}
		finally
		{
			// Something went wrong with packets, revert to entities.
			if (!success) { plugin.fallbackToEntityHolograms(); }
		}
		
		return success;
	}
	
	private static <T> T resolve(Supplier<T> supplier) { return supplier.get(); }
	
	@Override
	public Hologram showHologram(Player viewer, Location location, BaseComponent[] text)
	{
		Location base = Hologram.baseOffsetFromTopLocation(location);
		int entityId = ThreadLocalRandom.current().nextInt(MIN_ID, Integer.MAX_VALUE);
		
		// Packet #1: spawn the fake armor stand
		// https://wiki.vg/Protocol#Spawn_Living_Entity
		sendPacket(viewer, PacketType.Play.Server.SPAWN_ENTITY_LIVING, spawn ->
		{
			spawn.getIntegers().write(0, entityId);
			spawn.getUUIDs().write(0, UUID.randomUUID());
			
			// https://wiki.vg/Entity_metadata#Mobs
			spawn.getIntegers().write(1, 1); // ArmorStand ID: 1
			
			spawn.getDoubles().write(0, base.getX());
			spawn.getDoubles().write(1, base.getY());
			spawn.getDoubles().write(2, base.getZ());
		});
		
		// Packet #2: send fake armor stand metadata
		// https://wiki.vg/Protocol#Entity_Metadata
		sendPacket(viewer, PacketType.Play.Server.ENTITY_METADATA, metadata ->
		{
			metadata.getIntegers().write(0, entityId);
			metadata.getWatchableCollectionModifier().write(0, resolve(() ->
			{
				WrappedDataWatcher watcher = new WrappedDataWatcher();
			
				// https://wiki.vg/Entity_metadata#Entity
				// Bit mask 0x20 = invisible
				watcher.setObject(new WrappedDataWatcherObject(0, Registry.get(Byte.class)), (byte) 0x20);
				
				// Custom name
				watcher.setObject(
					new WrappedDataWatcherObject(2, Registry.getChatComponentSerializer(true)),
					Optional.of(WrappedChatComponent.fromJson(ComponentSerializer.toString(text)).getHandle())
				);
				
				// Make custom name visible
				watcher.setObject(new WrappedDataWatcherObject(3, Registry.get(Boolean.class)), true);
				
				// No gravity
				watcher.setObject(new WrappedDataWatcherObject(5, Registry.get(Boolean.class)), true);
				
				return watcher.getWatchableObjects();
			}));
		});
		
		return new PacketHologram(viewer, base, entityId);
	}
	
	class PacketHologram implements Hologram
	{
		private final Player viewer;
		private final Location location;
		private final int entityId;
		
		private boolean isDestroyed = false;
		
		PacketHologram(Player viewer, Location location, int entityId)
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
			isDestroyed = sendPacket(viewer, PacketType.Play.Server.ENTITY_DESTROY, destroy ->
			{
				if (MinecraftVersions.SERVER.lessThan(MinecraftVersions.V1_17_0))
				{
					destroy.getIntegerArrays().write(0, new int[] { entityId });
				}
				else if (MinecraftVersions.SERVER.lessThan(MinecraftVersions.V1_17_1))
				{
					destroy.getIntegers().write(0, entityId);
				}
				else // >= 1.17.1
				{
					destroy.getIntLists().write(0, List.of(entityId));
				}
			});
		}
	}
}
