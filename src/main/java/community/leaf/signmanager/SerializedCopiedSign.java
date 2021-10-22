/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public interface SerializedCopiedSign
{
	static SerializedCopiedSign of(String contentAdapterKey, List<? extends SerializedSignLine> serializedLines)
	{
		return new Serialized.CopiedSignImpl(contentAdapterKey, serializedLines);
	}
	
	static PersistentDataType<PersistentDataContainer, SerializedCopiedSign> persistentDataType()
	{
		return Serialized.CopiedSignImpl.TYPE;
	}
	
	String contentAdapterKey();
	
	List<? extends SerializedSignLine> serializedLines();
}
