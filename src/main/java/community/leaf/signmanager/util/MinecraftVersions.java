/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util;

import com.github.zafarkhaja.semver.Version;
import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MinecraftVersions
{
	private MinecraftVersions() { throw new UnsupportedOperationException(); }
	
	public static final Version V1_16_5 = Version.forIntegers(1, 16, 5);
	
	public static final Version V1_17_0 = Version.forIntegers(1, 17);
	
	public static final Version V1_17_1 = Version.forIntegers(1, 17, 1);
	
	public static final Version SERVER;
	
	static
	{
		Matcher matcher = Pattern.compile("\\d+\\.\\d+(\\.\\d+)?").matcher(Bukkit.getBukkitVersion());
		if (!matcher.find()) { throw new IllegalStateException("Invalid Bukkit version: " + Bukkit.getBukkitVersion()); }
		SERVER = Version.valueOf(matcher.group());
	}
}
