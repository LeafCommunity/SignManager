/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.WrappedDataWatcherObject;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public interface HologramDisplay
{
	void showHologram(Location location, Player player, Duration duration, BaseComponent[] text);
	
	class ProtocolLibHologram implements HologramDisplay
	{
		private static final int MIN_ID = Integer.MAX_VALUE / 2;
		
		private final SignManagerPlugin plugin;
		private final ProtocolManager protocol;
		
		public ProtocolLibHologram(SignManagerPlugin plugin)
		{
			this.plugin = plugin;
			this.protocol = ProtocolLibrary.getProtocolManager();
		}
		
		@Override
		public void showHologram(Location location, Player player, Duration duration, BaseComponent[] text)
		{
			// Packet #1: spawn the fake armor stand
			// https://wiki.vg/Protocol#Spawn_Living_Entity
			PacketContainer spawn = protocol.createPacket(PacketType.Play.Server.SPAWN_ENTITY_LIVING);
			
			int entityId = ThreadLocalRandom.current().nextInt(MIN_ID, Integer.MAX_VALUE);
			UUID entityUuid = UUID.randomUUID();
			
			spawn.getIntegers().write(0, entityId);
			spawn.getUUIDs().write(0, entityUuid);
			
			// https://wiki.vg/Entity_metadata#Mobs
			spawn.getIntegers().write(1, 1); // ArmorStand ID: 1
			
			spawn.getDoubles().write(0, location.getX());
			spawn.getDoubles().write(1, location.getY() - 1.75);
			spawn.getDoubles().write(2, location.getZ());
			
			// Packet #2: send fake armor stand metadata
			// https://wiki.vg/Protocol#Entity_Metadata
			PacketContainer metadata = protocol.createPacket(PacketType.Play.Server.ENTITY_METADATA);
			metadata.getIntegers().write(0, entityId);
			
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
			
			metadata.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
			
			try
			{
				plugin.getLogger().info("Sending SPAWN packet.");
				protocol.sendServerPacket(player, spawn);
				
				plugin.getLogger().info("Sending METADATA packet.");
				protocol.sendServerPacket(player, metadata);
			}
			catch (InvocationTargetException e)
			{
				e.printStackTrace();
			}
			
			plugin.async().delayByMilliseconds(duration.toMillis()).run(() ->
			{
				// Packet #3: destroy hologram
				// https://wiki.vg/Protocol#Destroy_Entities
				PacketContainer destroy = protocol.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
				destroy.getIntLists().write(0, List.of(entityId));
				
				try
				{
					plugin.getLogger().info("Sending DESTROY packet.");
					protocol.sendServerPacket(player, destroy);
				}
				catch (InvocationTargetException e)
				{
					e.printStackTrace();
				}
			});
		}
	}
}
