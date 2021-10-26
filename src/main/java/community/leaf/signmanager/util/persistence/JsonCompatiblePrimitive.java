/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util.persistence;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

@SuppressWarnings("NullableProblems")
public interface JsonCompatiblePrimitive<T> extends JsonPrimitiveGetter<T>, JsonPrimitiveSetter<T>, PersistentDataType<T, T>
{
	static <T> JsonCompatiblePrimitive<T> of(Class<T> type, JsonPrimitiveGetter<T> getter, JsonPrimitiveSetter<T> setter)
	{
		return new JsonCompatiblePrimitive<>()
		{
			@Override
			public Class<T> getPrimitiveType() { return type; }
			
			@Override
			public T getFromJson(JsonElement element) { return getter.getFromJson(element); }
			
			@Override
			public void putIntoJson(JsonObject object, String key, T primitive) { setter.putIntoJson(object, key, primitive); }
		};
	}
	
	@Override
	default Class<T> getComplexType() { return getPrimitiveType(); }
	
	@Override
	default T toPrimitive(T complex, PersistentDataAdapterContext context) { return complex; }
	
	@Override
	default T fromPrimitive(T primitive, PersistentDataAdapterContext context) { return primitive; }
}
