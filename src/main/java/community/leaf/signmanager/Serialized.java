/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import community.leaf.signmanager.util.Keys;
import community.leaf.signmanager.util.Signs;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

final class Serialized
{
	private Serialized() { throw new UnsupportedOperationException(); }
	
	static final class SignLineImpl implements SerializedSignLine
	{
		@SuppressWarnings("NullableProblems")
		static final PersistentDataType<PersistentDataContainer, SerializedSignLine> TYPE =
			new PersistentDataType<>()
			{
				@Override
				public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
				
				@Override
				public Class<SerializedSignLine> getComplexType() { return SerializedSignLine.class; }
				
				@Override
				public PersistentDataContainer toPrimitive(SerializedSignLine complex, PersistentDataAdapterContext context)
				{
					PersistentDataContainer data = context.newPersistentDataContainer();
					
					data.set(Keys.signManager("index"), PersistentDataType.BYTE, (byte) complex.index());
					data.set(Keys.signManager("content"), PersistentDataType.STRING, complex.asSerializedString());
					
					return data;
				}
				
				@Override
				public SerializedSignLine fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context)
				{
					@NullOr Byte index = primitive.get(Keys.signManager("index"), PersistentDataType.BYTE);
					if (index == null) { throw new NullPointerException("index"); }
					
					@NullOr String content = primitive.get(Keys.signManager("content"), PersistentDataType.STRING);
					if (content == null) { throw new NullPointerException("content"); }
					
					return new SignLineImpl(index.intValue(), content);
				}
			};
		
		private final int index;
		private final String serialized;
		
		SignLineImpl(int index, String serialized)
		{
			this.index = Signs.index(index);
			this.serialized = Objects.requireNonNull(serialized, "serialized");
		}
		
		@Override
		public int index() { return index; }
		
		@Override
		public String asSerializedString() { return serialized; }
	}
	
	static final class CopiedSignImpl implements SerializedCopiedSign
	{
		@SuppressWarnings("NullableProblems")
		static final PersistentDataType<PersistentDataContainer, SerializedCopiedSign> TYPE =
			new PersistentDataType<>()
			{
				@Override
				public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
				
				@Override
				public Class<SerializedCopiedSign> getComplexType() { return SerializedCopiedSign.class; }
				
				@Override
				public PersistentDataContainer toPrimitive(SerializedCopiedSign complex, PersistentDataAdapterContext context)
				{
					PersistentDataContainer data = context.newPersistentDataContainer();
					
					data.set(Keys.signManager("using"), PersistentDataType.STRING, complex.contentAdapterKey());
					
					data.set(
						Keys.signManager("lines"),
						PersistentDataType.TAG_CONTAINER_ARRAY,
						complex.serializedLines().stream()
							.map(line -> SignLineImpl.TYPE.toPrimitive(line, context))
							.toArray(PersistentDataContainer[]::new)
					);
					
					return data;
				}
				
				@Override
				public SerializedCopiedSign fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context)
				{
					@NullOr String using = primitive.get(Keys.signManager("using"), PersistentDataType.STRING);
					if (using == null) { throw new NullPointerException("using"); }
					
					@NullOr PersistentDataContainer @NullOr[] lines =
						primitive.get(Keys.signManager("lines"), PersistentDataType.TAG_CONTAINER_ARRAY);
					
					if (lines == null) { throw new NullPointerException("lines"); }
					
					return new CopiedSignImpl(
						using,
						Arrays.stream(lines)
							.filter(Objects::nonNull)
							.map(line -> SignLineImpl.TYPE.fromPrimitive(line, context))
							.collect(Collectors.toList())
					);
				}
			};
		
		private final String contentAdapterKey;
		private final List<? extends SerializedSignLine> serializedLines;
		
		CopiedSignImpl(String contentAdapterKey, List<? extends SerializedSignLine> serializedLines)
		{
			this.contentAdapterKey = Objects.requireNonNull(contentAdapterKey, "contentAdapterKey");
			this.serializedLines = List.copyOf(Objects.requireNonNull(serializedLines, "serializedLines"));
		}
		
		@Override
		public String contentAdapterKey() { return contentAdapterKey; }
		
		@Override
		public List<? extends SerializedSignLine> serializedLines() { return serializedLines; }
	}
}
