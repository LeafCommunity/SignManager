/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util;

import com.github.zafarkhaja.semver.Version;

public class MinecraftVersions
{
	private MinecraftVersions() { throw new UnsupportedOperationException(); }
	
	public static final Version V1_16_5 = Version.forIntegers(1, 16, 5);
	
	public static final Version V1_17_0 = Version.forIntegers(1, 17);
	
	public static final Version V1_17_1 = Version.forIntegers(1, 17, 1);
}
