/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;

public class PlayableSound
{
	private final Sound sound;
	private final FloatRange volume;
	private final FloatRange pitch;
	
	public PlayableSound(Sound sound, FloatRange volume, FloatRange pitch)
	{
		this.sound = Objects.requireNonNull(sound, "sound");
		this.volume = Objects.requireNonNull(volume, "volume");
		this.pitch = Objects.requireNonNull(pitch, "pitch");
	}
	
	public Sound sound() { return sound; }
	
	public FloatRange volume() { return volume; }
	
	public FloatRange pitch() { return pitch; }
	
	public void playToPlayer(Player listener, Location location)
	{
		listener.playSound(location, sound, volume.resolve(), pitch.resolve());
	}
	
	public void playToPlayer(Player listener)
	{
		playToPlayer(listener, listener.getEyeLocation());
	}
	
	public void playToAll(Location location)
	{
		@NullOr World world = location.getWorld();
		if (world == null) { return; }
		
		world.playSound(location, sound, volume.resolve(), pitch.resolve());
	}
	
	@Override
	public boolean equals(@NullOr Object o)
	{
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		PlayableSound that = (PlayableSound) o;
		return sound == that.sound && volume.equals(that.volume) && pitch.equals(that.pitch);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(sound, volume, pitch);
	}
}
