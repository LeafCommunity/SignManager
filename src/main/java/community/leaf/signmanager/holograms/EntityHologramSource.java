/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.holograms;

import community.leaf.signmanager.util.Keys;
import community.leaf.signmanager.util.persistence.Persistent;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import pl.tlinkowski.annotation.basic.NullOr;

public class EntityHologramSource implements HologramSource
{
	public static final NamespacedKey HOLOGRAM_KEY = Keys.signManager("hologram");
	
	@Override
	public Hologram showHologram(Player player, Location location, String text)
	{
		@NullOr World world = location.getWorld();
		if (world == null) { throw new NullPointerException("Location has null world"); }
		
		Location base = Hologram.baseOffsetFromTopLocation(location);
		
		return new EntityHologram(
			world.spawn(base, ArmorStand.class, armorStand ->
			{
				armorStand.setCollidable(false);
				armorStand.setGravity(false);
				armorStand.setInvulnerable(true);
				armorStand.setPersistent(false);
				armorStand.setVisible(false);
				
				armorStand.setCustomNameVisible(true);
				armorStand.setCustomName(text);
				
				armorStand.getPersistentDataContainer().set(HOLOGRAM_KEY, Persistent.Types.BOOLEAN, true);
			}
		));
	}
	
	static class EntityHologram implements Hologram
	{
		private final ArmorStand armorStand;
		
		EntityHologram(ArmorStand armorStand)
		{
			this.armorStand = armorStand;
		}
		
		@Override
		public Location location() { return armorStand.getLocation(); }
		
		@Override
		public boolean isDestroyed() { return armorStand.isDead(); }
		
		@Override
		public void destroy() { armorStand.remove(); }
	}
}
