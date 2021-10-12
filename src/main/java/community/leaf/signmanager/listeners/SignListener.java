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
import community.leaf.signmanager.util.Signs;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

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
		Signs.blockState(event.getClickedBlock()).ifPresent(sign ->
		{
			sign.setGlowingText(!sign.isGlowingText()); // :D
		});
	}
}
