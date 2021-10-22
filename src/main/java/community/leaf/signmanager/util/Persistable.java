/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

public interface Persistable<T, P extends Persistable<T, P>>
{
	PersistentDataType<T, P> persistentDataType();
	
	@SuppressWarnings("unchecked")
	default T toPersistentData(PersistentDataAdapterContext context)
	{
		return persistentDataType().toPrimitive((P) this, context);
	}
}
