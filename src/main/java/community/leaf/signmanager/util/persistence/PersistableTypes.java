/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util.persistence;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("NullableProblems")
public class PersistableTypes
{
	private PersistableTypes() { throw new UnsupportedOperationException(); }
	
	public static final PersistentDataType<Byte, Byte> BYTE = PersistentDataType.BYTE;
	
	public static final PersistentDataType<String, String> STRING = PersistentDataType.STRING;
	
	// Recreated tag container array type because it
	// was only introduced in version 1.16+
	public static final PersistentDataType<PersistentDataContainer[], PersistentDataContainer[]> TAG_CONTAINER_ARRAY =
		new PersistentDataType<>()
		{
			@Override
			public Class<PersistentDataContainer[]> getPrimitiveType() { return PersistentDataContainer[].class; }
			
			@Override
			public Class<PersistentDataContainer[]> getComplexType() { return PersistentDataContainer[].class; }
			
			@Override
			public PersistentDataContainer[] toPrimitive(PersistentDataContainer[] complex, PersistentDataAdapterContext context)
			{
				return complex;
			}
			
			@Override
			public PersistentDataContainer[] fromPrimitive(PersistentDataContainer[] primitive, PersistentDataAdapterContext context)
			{
				return primitive;
			}
		};
	
	public static final PersistentDataType<Byte, Boolean> BOOLEAN =
		new PersistentDataType<>()
		{
			@Override
			public Class<Byte> getPrimitiveType() { return Byte.class; }
			
			@Override
			public Class<Boolean> getComplexType() { return Boolean.class; }
			
			@Override
			public Byte toPrimitive(Boolean complex, PersistentDataAdapterContext context)
			{
				return (byte) ((complex) ? 1 : 0);
			}
			
			@Override
			public Boolean fromPrimitive(Byte primitive, PersistentDataAdapterContext context)
			{
				return primitive != 0;
			}
		};
}
