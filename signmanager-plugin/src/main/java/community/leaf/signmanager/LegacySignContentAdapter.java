/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import com.rezzedup.util.valuables.Adapter;
import community.leaf.signmanager.common.SignContentAdapter;
import community.leaf.signmanager.common.SignLine;
import org.bukkit.block.Sign;

public class LegacySignContentAdapter implements SignContentAdapter<String>
{
	@Override
	public Adapter<String, String> contentAsString()
	{
		return Adapter.identity();
	}
	
	@Override
	public SignLine<String> getLine(Sign sign, int index)
	{
		return new SignLine<>(index, sign.getLine(index));
	}
	
	@Override
	public void setLine(Sign sign, SignLine<String> line)
	{
		sign.setLine(line.index(), line.content());
	}
}
