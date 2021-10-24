/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.listeners;

import community.leaf.eventful.bukkit.CancellationPolicy;
import community.leaf.eventful.bukkit.ListenerOrder;
import community.leaf.eventful.bukkit.annotations.CancelledEvents;
import community.leaf.eventful.bukkit.annotations.EventListener;
import community.leaf.signmanager.CopiedSign;
import community.leaf.signmanager.SignManagerPlugin;
import community.leaf.signmanager.exceptions.SignPasteException;
import community.leaf.signmanager.holograms.Hologram;
import community.leaf.signmanager.util.Signs;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.ArrayList;
import java.util.List;

@CancelledEvents(CancellationPolicy.REJECT)
public class SignListener implements Listener
{
	public static final Color AQUA = Color.fromRGB(0xa9f1fc);
	
	public static final Color MAGENTA = Color.fromRGB(0xfc94fa);
	
	public static final Color RED = Color.fromRGB(0xf9502a);
	
	private final SignManagerPlugin plugin;
	
	public SignListener(SignManagerPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	@EventListener
	public void onSignClick(PlayerInteractEvent event)
	{
		@NullOr Sign sign = Signs.blockState(event.getClickedBlock()).orElse(null);
		if (sign == null) { return; }
		
		@NullOr EquipmentSlot hand = event.getHand();
		if (hand == null) { return; }
		
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(hand);
		
		if (!Tag.SIGNS.isTagged(item.getType())) { return; } // Only continue if holding a sign.
		if (player.isSneaking()) { return; } // Do nothing is sneaking.
		
		@NullOr ItemMeta meta = item.getItemMeta();
		if (meta == null) { return; }
		
		PersistentDataContainer data = meta.getPersistentDataContainer();
		NamespacedKey key = new NamespacedKey(plugin, "clipboard");
		Location centered = sign.getLocation().clone().add(0.5, 0.5, 0.5);
		
		// COPY
		if (event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			event.setCancelled(true); // Prevent breaking the sign.
			
			CopiedSign copy = new CopiedSign(sign);
			data.set(key, CopiedSign.TYPE, copy);
			
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			
			List<String> lore = new ArrayList<>();
			
			lore.add("Punch to copy or click to paste:");
			
			// Preview lines
			copy.lines().stream()
				.map(line ->
					new ComponentBuilder()
						.append("→ #" + (line.index() + 1) + ": ")
							.color(ChatColor.GRAY)
						.append(new TextComponent(line.toPreview()))
							.color(ChatColor.WHITE)
						.create()
				)
				.map(TextComponent::toLegacyText)
				.forEach(lore::add);
				
			meta.setDisplayName("Punch to copy, click to paste!");
			meta.setLore(lore);
			item.setItemMeta(meta);
			
			particle(centered, MAGENTA);
			hologram(player, centered, "&o&lCopied!");
		}
		// PASTE
		else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if (!data.has(key, PersistentDataType.TAG_CONTAINER)) { return; }
			
			event.setCancelled(true); // Cancel interaction in case other plugins handle sign clicks.
			
			@NullOr CopiedSign copy = data.get(key, CopiedSign.TYPE);
			if (copy == null) { return; }
			
			try
			{
				copy.paste(sign, player); // TODO: store copy/paste history
				particle(centered, AQUA);
				hologram(player, centered, "&o&lPasted!");
				
			}
			catch (SignPasteException e)
			{
				particle(centered, RED);
				hologram(player, centered, "&o&lCould not paste...");
			}
		}
	}
	
	@EventListener(ListenerOrder.FIRST)
	public void onSignPlace(BlockPlaceEvent event)
	{
		class SignPlaceEvent extends BlockPlaceEvent
		{
			SignPlaceEvent()
			{
				super(
					event.getBlockPlaced(),
					event.getBlockReplacedState(),
					event.getBlockAgainst(),
					event.getItemInHand(),
					event.getPlayer(),
					event.canBuild(),
					event.getHand()
				);
			}
		}
		
		// We don't listen to our own event.
		if (event instanceof SignPlaceEvent) { return; }
		
		Block block = event.getBlockPlaced();
		if (!Tag.SIGNS.isTagged(block.getType())) { return; }
		
		Player player = event.getPlayer();
		Location location = block.getLocation();
		BlockData clonedBlockData = block.getBlockData().clone();
		ItemStack item = event.getItemInHand();
		
		@NullOr ItemMeta meta = item.getItemMeta();
		if (meta == null) { return; }
		
		PersistentDataContainer data = meta.getPersistentDataContainer();
		NamespacedKey key = new NamespacedKey(plugin, "clipboard");
		
		if (!data.has(key, PersistentDataType.TAG_CONTAINER)) { return; }
		
		// Handle placing the sign manually (skip sign editor UI)
		event.setCancelled(true);
		
		// Check if the player can actually place the sign...
		SignPlaceEvent place = plugin.events().call(new SignPlaceEvent());
		if (place.isCancelled() || !place.canBuild()) { return; }
		
		@NullOr CopiedSign copy = data.get(key, CopiedSign.TYPE);
		if (copy == null) { return; }
		
		// Serialized data exists, subtract one clipboard from the player's inventory if they're not in creative.
		if (player.getGameMode() != GameMode.CREATIVE) { item.setAmount(item.getAmount() - 1); }
		
		// Since there's a slight delay, play some particles!!
		Location centered = location.clone().add(0.5, 0.5, 0.5);
		particle(centered, AQUA);
		
		// Place it on the next tick.
		plugin.sync().run(() ->
		{
			location.getBlock().setBlockData(clonedBlockData);
			
			Signs.blockState(location.getBlock()).ifPresent(sign -> {
				try
				{
					copy.paste(sign, player);
					hologram(player, centered, "&o&lPasted!");
				}
				catch (SignPasteException e)
				{
					particle(centered, RED);
					hologram(player, centered, "&o&lCould not paste...");
				}
			});
		});
	}
	
	private void particle(Location location, Color color)
	{
		if (location.getWorld() == null) { return; }
		
		location.getWorld().spawnParticle(
			Particle.REDSTONE,
			location,
			25,
			0.25,
			0.35,
			0.25,
			0.5,
			new Particle.DustOptions(color, 0.5F)
		);
	}
	
	private void hologram(Player player, Location location, String text)
	{
		BaseComponent[] components = TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text));
		Hologram hologram = plugin.holograms().showHologram(player, location, components);
		plugin.sync().delay(1).seconds().run(hologram::destroy);
	}
}
