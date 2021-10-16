/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.common;

import community.leaf.signmanager.common.util.Signs;
import org.bukkit.block.Sign;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface SignContentAdapter
{
	static <T> SignContentAdapter of(String key, BiFunction<Sign, Integer, T> getter, BiFunction<Integer, T, SignLine> constructor)
	{
		Objects.requireNonNull(key, "key");
		Objects.requireNonNull(getter, "getter");
		Objects.requireNonNull(constructor, "constructor");
		
		return new SignContentAdapter()
		{
			@Override
			public String key() { return key; }
			
			@Override
			public SignLine getLine(Sign sign, int index)
			{
				return constructor.apply(index, getter.apply(sign, index));
			}
		};
	}
	
	String key();
	
	SignLine getLine(Sign sign, int index);
	
	default List<SignLine> getSpecificLines(Sign sign, int ... indices)
	{
		return IntStream.of(indices)
			.filter(Signs::isIndex)
			.sorted()
			.mapToObj(index -> getLine(sign, index))
			.collect(Collectors.toList());
	}
	
	default List<SignLine> getAllLines(Sign sign)
	{
		return Signs.indexRange()
			.mapToObj(index -> getLine(sign, index))
			.collect(Collectors.toList());
	}
}
