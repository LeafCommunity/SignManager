/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public interface PasteHistory
{
	UUID uuid();
	
	void add(PastedSign pasted);
	
	List<PastedSign> pastes();
	
	default List<PastedSign> activePastes()
	{
		return pastes().stream()
			.filter(Predicate.not(PastedSign::isUndone))
			.collect(Collectors.toList());
	}
	
	default List<PastedSign> undonePastes()
	{
		return pastes().stream()
			.filter(PastedSign::isUndone)
			.collect(Collectors.toList());
	}
}
