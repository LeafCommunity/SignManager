/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util.persistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rezzedup.util.constants.Aggregates;
import com.rezzedup.util.constants.annotations.AggregatedResult;
import com.rezzedup.util.constants.types.TypeCapture;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
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
		@NullOr VirtualJsonPrimitive<T> primitive =
			(VirtualJsonPrimitive<T>) VirtualJsonPrimitive.VALUES.get(type.getPrimitiveType());
		
		if (primitive == null)
		{
			throw new IllegalArgumentException(
				"Unsupported type: " + type + " (" + type.getPrimitiveType() + ")"
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
		@NullOr VirtualJsonPrimitive<T> primitive =
			(VirtualJsonPrimitive<T>) VirtualJsonPrimitive.VALUES.get(type.getPrimitiveType());
		
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
	
	@FunctionalInterface
	interface JsonPrimitiveGetter<T>
	{
		T getFromJson(JsonElement element);
	}
	
	@FunctionalInterface
	interface JsonPrimitiveSetter<T>
	{
		static <T extends Number> JsonPrimitiveSetter<T> number() { return JsonObject::addProperty; }
		
		void putIntoJson(JsonObject object, String key, T primitive);
	}
	
	static abstract class VirtualJsonPrimitive<T> implements JsonPrimitiveGetter<T>, JsonPrimitiveSetter<T>
	{
		static final VirtualJsonPrimitive<Byte> BYTE =
			of(Byte.class, JsonElement::getAsByte, JsonPrimitiveSetter.number());
		
		static final VirtualJsonPrimitive<String> STRING =
			of(String.class, JsonElement::getAsString, JsonObject::addProperty);
		
		static final VirtualJsonPrimitive<PersistentDataContainer> TAG_CONTAINER =
			of(
				PersistentDataContainer.class,
				(element) -> new JsonPersistentDataContainer(element.getAsJsonObject()),
				(object, key, primitive) -> object.add(key, ((JsonPersistentDataContainer) primitive).json())
			);
		
		static final VirtualJsonPrimitive<PersistentDataContainer[]> TAG_CONTAINER_ARRAY =
			of(
				PersistentDataContainer[].class,
				(element) ->
				{
					List<PersistentDataContainer> containers = new ArrayList<>();
					for (JsonElement item : element.getAsJsonArray()) { containers.add(TAG_CONTAINER.getFromJson(item)); }
					return containers.toArray(new PersistentDataContainer[0]);
				},
				(object, key, primitive) ->
				{
					JsonArray array = new JsonArray();
		
					Arrays.stream(primitive)
						.map(container -> (JsonPersistentDataContainer) container)
						.map(JsonPersistentDataContainer::json)
						.forEach(array::add);
		
					object.add(key, array);
				}
			);
		
		@AggregatedResult
		static final Map<Class<?>, VirtualJsonPrimitive<?>> VALUES = new HashMap<>();
		
		static
		{
			Aggregates.visit(
				VirtualJsonPrimitive.class,
				new TypeCapture<VirtualJsonPrimitive<?>>() {},
				Aggregates.matching().all(),
				(name, primitive) -> VALUES.put(primitive.type(), primitive)
			);
		}
		
		static <T> VirtualJsonPrimitive<T> of(Class<T> type, JsonPrimitiveGetter<T> getter, JsonPrimitiveSetter<T> setter)
		{
			return new VirtualJsonPrimitive<>(type)
			{
				@Override
				public T getFromJson(JsonElement element) { return getter.getFromJson(element); }
				
				@Override
				public void putIntoJson(JsonObject object, String key, T primitive)
				{
					setter.putIntoJson(object, key, primitive);
				}
			};
		}
		
		final Class<T> type;
		
		VirtualJsonPrimitive(Class<T> type)
		{
			this.type = type;
		}
		
		public Class<T> type() { return type; }
	}
}
