/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.common;

import com.rezzedup.util.valuables.Adapter;
import community.leaf.signmanager.common.util.Signs;
import org.bukkit.block.Sign;

import java.util.List;
import java.util.stream.Collectors;

public interface SignContentAdapter<T>
{
	Adapter<String, T> contentAsString();
	
	SignLine<T> getLine(Sign sign, int index);
	
	default List<SignLine<T>> getLines(Sign sign)
	{
		return Signs.indexRange().mapToObj(index -> getLine(sign, index)).collect(Collectors.toList());
	}
	
	void setLine(Sign sign, SignLine<T> line);
	
	default void setLines(Sign sign, List<SignLine<T>> lines)
	{
		lines.forEach(line -> setLine(sign, line));
	}
}
