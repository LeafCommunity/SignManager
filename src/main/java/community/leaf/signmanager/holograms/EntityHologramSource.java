/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.holograms;

import community.leaf.signmanager.util.Keys;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import pl.tlinkowski.annotation.basic.NullOr;

public class EntityHologramSource implements HologramSource
{
	public static final NamespacedKey HOLOGRAM_KEY = Keys.signManager("hologram");
	
	@Override
	public boolean supportsLocalHolograms() { return false; }
	
	@Override
	public Hologram showHologram(Player viewer, Location location, BaseComponent[] text)
	{
		@NullOr World world = location.getWorld();
		if (world == null) { throw new NullPointerException("Location has null world"); }
		
		Location base = Hologram.baseOffsetFromTopLocation(location);
		
		return new EntityHologram(
			viewer,
			world.spawn(base, ArmorStand.class, armorStand ->
			{
				armorStand.setInvisible(true);
				armorStand.setGravity(false);
				
				armorStand.setCustomNameVisible(true);
				armorStand.setCustomName(TextComponent.toLegacyText(text));
				
				armorStand.getPersistentDataContainer().set(HOLOGRAM_KEY, PersistentDataType.BYTE, (byte) 1);
				armorStand.setPersistent(false);
			}
		));
	}
	
	static class EntityHologram implements Hologram
	{
		private final Player viewer;
		private final ArmorStand armorStand;
		
		EntityHologram(Player viewer, ArmorStand armorStand)
		{
			this.viewer = viewer;
			this.armorStand = armorStand;
		}
		
		@Override
		public boolean isLocal() { return false; }
		
		@Override
		public Player viewer() { return viewer; }
		
		@Override
		public Location location() { return armorStand.getLocation(); }
		
		@Override
		public boolean isDestroyed() { return armorStand.isDead(); }
		
		@Override
		public void destroy() { armorStand.remove(); }
	}
}
