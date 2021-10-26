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
import com.google.gson.JsonParser;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("NullableProblems")
public class JsonPersistentDataContainer implements PersistentDataContainer
{
	public static <Z> JsonPersistentDataContainer of(PersistentDataType<PersistentDataContainer, Z> type, Z complex)
	{
		return (JsonPersistentDataContainer) type.toPrimitive(complex, JsonPersistentDataContainer::new);
	}
	
	public static <Z> Z fromJsonString(PersistentDataType<PersistentDataContainer, Z> type, String json)
	{
		return type.fromPrimitive(
			new JsonPersistentDataContainer(new JsonParser().parse(json).getAsJsonObject()),
			JsonPersistentDataContainer::new
		);
	}
	
	private final JsonObject json;
	
	public JsonPersistentDataContainer(JsonObject json) { this.json = json; }
	
	public JsonPersistentDataContainer() { this(new JsonObject()); }
	
	public JsonObject json() { return json; }
	
	@SuppressWarnings("unchecked")
	@Override
	public <T, Z> void set(NamespacedKey key, PersistentDataType<T, Z> type, Z value)
	{
		@NullOr JsonCompatiblePrimitive<T> primitive =
			(JsonCompatiblePrimitive<T>) Persistent.Types.JSON_COMPATIBLE.get(type.getPrimitiveType());
		
		if (primitive == null)
		{
			throw new IllegalArgumentException(
				"Unsupported type: " + type.getPrimitiveType() + " (" + type + ")"
			);
		}
		
		T converted = type.toPrimitive(value, getAdapterContext());
		primitive.putIntoJson(json, key.toString(), converted);
	}
	
	@Override
	public <T, Z> boolean has(NamespacedKey key, PersistentDataType<T, Z> type)
	{
		return json.has(key.toString()); // TODO: type check?
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public @NullOr <T, Z> Z get(NamespacedKey key, PersistentDataType<T, Z> type)
	{
		@NullOr JsonCompatiblePrimitive<T> primitive =
			(JsonCompatiblePrimitive<T>) Persistent.Types.JSON_COMPATIBLE.get(type.getPrimitiveType());
		
		if (primitive == null)
		{
			throw new IllegalArgumentException(
				"Unsupported type: " + type + " (" + type.getPrimitiveType() + ")"
			);
		}
		
		@NullOr JsonElement element = json.get(key.toString());
		if (element == null) { return null; }
		
		return type.fromPrimitive(primitive.getFromJson(element), getAdapterContext());
	}
	
	@Override
	public <T, Z> Z getOrDefault(NamespacedKey key, PersistentDataType<T, Z> type, Z defaultValue)
	{
		@NullOr Z z = get(key, type);
		return (z != null) ? z : defaultValue;
	}
	
	@Override
	public Set<NamespacedKey> getKeys()
	{
		return json.entrySet().stream()
			.map(Map.Entry::getKey)
			.map(NamespacedKey::fromString)
			.filter(Objects::nonNull)
			.collect(Collectors.toCollection(LinkedHashSet::new));
	}
	
	@Override
	public void remove(NamespacedKey key)
	{
		json.remove(key.toString());
	}
	
	@Override
	public boolean isEmpty()
	{
		return json.size() == 0;
	}
	
	@Override
	public PersistentDataAdapterContext getAdapterContext()
	{
		return JsonPersistentDataContainer::new;
	}
}
