/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util;

import net.md_5.bungee.api.ChatColor;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Strings
{
	private Strings() { throw new UnsupportedOperationException(); }
	
	private static final Pattern HASH_HEX_COLOR_PATTERN = Pattern.compile("(?i)&x?#(?<hex>[a-f0-9]{6})");
	
	public static boolean isEmptyOrNull(@NullOr String text) { return text == null || text.isEmpty(); }
	
	public static String colorful(@NullOr String text)
	{
		return (isEmptyOrNull(text))
			? ""
			: ChatColor.translateAlternateColorCodes(
				'&',
				HASH_HEX_COLOR_PATTERN.matcher(text).replaceAll(match ->
					match.group(1).chars() // group 1 = hex
						.mapToObj(c -> "&" + (char) c)
						.collect(Collectors.joining("", "&x", ""))
				)
			);
	}
}
