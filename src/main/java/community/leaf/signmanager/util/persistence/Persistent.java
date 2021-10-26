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
import com.rezzedup.util.constants.Aggregates;
import com.rezzedup.util.constants.annotations.AggregatedResult;
import com.rezzedup.util.constants.types.TypeCapture;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface Persistent<T, Z extends Persistent<T, Z>>
{
	@SuppressWarnings("NullableProblems")
	static <T, Z> PersistentDataType<T, Z> dataType(
		Class<T> primitiveType,
		Class<Z> complexType,
		BiFunction<Z, PersistentDataAdapterContext, T> toPrimitive,
		BiFunction<T, PersistentDataAdapterContext, Z> fromPrimitive
	)
	{
		return new PersistentDataType<>()
		{
			@Override
			public Class<T> getPrimitiveType() { return primitiveType; }
			
			@Override
			public Class<Z> getComplexType() { return complexType; }
			
			@Override
			public T toPrimitive(Z complex, PersistentDataAdapterContext context) { return toPrimitive.apply(complex, context); }
			
			@Override
			public Z fromPrimitive(T primitive, PersistentDataAdapterContext context) { return fromPrimitive.apply(primitive, context); }
		};
	}
	
	static <T, Z> PersistentDataType<T, Z> dataType(
		Class<T> primitiveType,
		Class<Z> complexType,
		Function<Z, T> toPrimitive,
		Function<T, Z> fromPrimitive
	)
	{
		return dataType(
			primitiveType,
			complexType,
			(complex, ignored) -> toPrimitive.apply(complex),
			(primitive, ignored) -> fromPrimitive.apply(primitive)
		);
	}
	
	PersistentDataType<T, Z> persistentDataType();
	
	@SuppressWarnings("unchecked")
	default T toPersistentData(PersistentDataAdapterContext context)
	{
		return persistentDataType().toPrimitive((Z) this, context);
	}
	
	@SuppressWarnings("NullableProblems")
	class Types
	{
		private Types() { throw new UnsupportedOperationException(); }
		
		public static final JsonCompatiblePrimitive<Byte> BYTE =
			JsonCompatiblePrimitive.of(Byte.class, JsonElement::getAsByte, JsonPrimitiveSetter.number());
		
		public static final JsonCompatiblePrimitive<String> STRING =
			JsonCompatiblePrimitive.of(String.class, JsonElement::getAsString, JsonObject::addProperty);
		
		public static final JsonCompatiblePrimitive<PersistentDataContainer> TAG_CONTAINER =
			JsonCompatiblePrimitive.of(
				PersistentDataContainer.class,
				(element) -> new JsonPersistentDataContainer(element.getAsJsonObject()),
				(object, key, primitive) -> object.add(key, ((JsonPersistentDataContainer) primitive).json())
			);
		
		public static final JsonCompatiblePrimitive<PersistentDataContainer[]> TAG_CONTAINER_ARRAY =
			JsonCompatiblePrimitive.of(
				PersistentDataContainer[].class,
				(element) ->
				{
					List<PersistentDataContainer> containers = new ArrayList<>();
					for (JsonElement item : element.getAsJsonArray()) { containers.add(TAG_CONTAINER.getFromJson(item)); }
					return containers.toArray(PersistentDataContainer[]::new);
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
		
		public static final PersistentDataType<Byte, Boolean> BOOLEAN =
			Persistent.dataType(
				Byte.class,
				Boolean.class,
				complex -> (byte) ((complex) ? 1 : 0),
				primitive -> primitive != 0
			);
		
		@AggregatedResult
		public static final Set<PersistentDataType<?, ?>> VALUES = Aggregates.set(Types.class, new TypeCapture<>() {});
		
		@AggregatedResult
		public static final Map<Class<?>, JsonCompatiblePrimitive<?>> JSON_COMPATIBLE;
		
		static
		{
			Map<Class<?>, JsonCompatiblePrimitive<?>> compatible = new LinkedHashMap<>();
			
			Aggregates.visit(
				Types.class,
				new TypeCapture<JsonCompatiblePrimitive<?>>() {},
				Aggregates.matching().all(),
				(name, primitive) -> compatible.put(primitive.getPrimitiveType(), primitive)
			);
			
			JSON_COMPATIBLE = Map.copyOf(compatible);
		}
	}
}
