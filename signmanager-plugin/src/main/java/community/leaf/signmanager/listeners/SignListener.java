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
import community.leaf.signmanager.common.util.Signs;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import pl.tlinkowski.annotation.basic.NullOr;

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
		Player player = event.getPlayer();
		
		@NullOr EquipmentSlot hand = event.getHand();
		if (hand == null) { return; }
		
		ItemStack tool = player.getInventory().getItem(hand);
		if (!Tag.SIGNS.isTagged(tool.getType())) { return; }
		
		//tool.getItemMeta().getPersistentDataContainer().
		//tool.getData().
		
		Signs.blockState(event.getClickedBlock()).ifPresent(sign ->
		{
			sign.setGlowingText(!sign.isGlowingText()); // :D
			sign.update();
		});
	}
}
