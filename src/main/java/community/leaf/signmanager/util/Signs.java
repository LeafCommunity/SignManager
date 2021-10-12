/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util;

import com.rezzedup.util.constants.types.Cast;
import org.bukkit.block.Block;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Optional;

public class Signs
{
	private Signs() { throw new UnsupportedOperationException(); }
	
	public static Optional<org.bukkit.block.Sign> blockState(@NullOr Block block)
	{
		return (block == null) ? Optional.empty() : Cast.as(org.bukkit.block.Sign.class, block.getState());
	}
	
	public static Optional<org.bukkit.block.data.type.Sign> blockData(@NullOr Block block)
	{
		return (block == null) ? Optional.empty() : Cast.as(org.bukkit.block.data.type.Sign.class, block.getBlockData());
	}
}
