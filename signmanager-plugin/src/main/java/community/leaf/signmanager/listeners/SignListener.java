/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.listeners;

import community.leaf.eventful.bukkit.CancellationPolicy;
import community.leaf.eventful.bukkit.annotations.CancelledEvents;
import community.leaf.eventful.bukkit.annotations.EventListener;
import community.leaf.signmanager.SignManagerPlugin;
import community.leaf.signmanager.common.CopiedSign;
import community.leaf.signmanager.common.SerializedCopiedSign;
import community.leaf.signmanager.common.SignContentAdapter;
import community.leaf.signmanager.common.SignLine;
import community.leaf.signmanager.common.util.Signs;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Sign;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.stream.Collectors;

@CancelledEvents(CancellationPolicy.REJECT)
public class SignListener implements Listener
{
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
		ItemStack tool = player.getInventory().getItem(hand);
		
		if (!Tag.SIGNS.isTagged(tool.getType())) { return; } // Only continue if holding a sign.
		if (player.isSneaking()) { return; } // Do nothing is sneaking.
		
		@NullOr ItemMeta meta = tool.getItemMeta();
		if (meta == null) { return; }
		
		PersistentDataContainer data = meta.getPersistentDataContainer();
		NamespacedKey key = new NamespacedKey(plugin, "clipboard");
		
		// COPY
		if (event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			event.setCancelled(true); // Prevent breaking the sign.
			
			SignContentAdapter adapter = plugin.adapters().defaultContentAdapter();
			CopiedSign copy = new CopiedSign(adapter, adapter.allLines(sign));
			
			data.set(key, SerializedCopiedSign.persistentDataType(), copy);
			
			meta.addEnchant(Enchantment.DURABILITY, 1, true);
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			
			meta.setDisplayName("Punch to copy, click to paste!");
			meta.setLore(copy.lines().stream().map(SignLine::asPlainText).collect(Collectors.toList()));
			
			tool.setItemMeta(meta);
		}
		// PASTE
		else if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			if (!data.has(key, PersistentDataType.TAG_CONTAINER)) { return; }
			
			event.setCancelled(true); // Cancel interaction in case other plugins handle sign clicks.
			
			@NullOr SerializedCopiedSign serialized = data.get(key, SerializedCopiedSign.persistentDataType());
			if (serialized == null) { return; }
			
			CopiedSign copy = CopiedSign.deserialize(plugin.adapters(), serialized);
			copy.paste(sign); // TODO: store copy/paste history
		}
	}
}
