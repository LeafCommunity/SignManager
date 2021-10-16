package community.leaf.signmanager.common.item;

import community.leaf.signmanager.common.SignLine;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ClipboardItemData
{
	public static final PersistentDataType<PersistentDataContainer, ClipboardItemData> TYPE = persistenDataType();
	
	private final String contentAdapterClassName;
	private final List<SignLine<String>> lines;
	
	public ClipboardItemData(String contentAdapterClassName, List<SignLine<String>> lines)
	{
		this.contentAdapterClassName = contentAdapterClassName;
		this.lines = List.copyOf(lines);
	}
	
	public String contentAdapterClassName()
	{
		return contentAdapterClassName;
	}
	
	public List<SignLine<String>> lines()
	{
		return lines;
	}
	
	@SuppressWarnings("NullableProblems")
	private static PersistentDataType<PersistentDataContainer, ClipboardItemData> persistenDataType()
	{
		return new PersistentDataType<>()
		{
			private NamespacedKey key(String key) { return new NamespacedKey("SignManager", key); }
			
			@Override
			public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
			
			@Override
			public Class<ClipboardItemData> getComplexType() { return ClipboardItemData.class; }
			
			@Override
			public PersistentDataContainer toPrimitive(ClipboardItemData complex, PersistentDataAdapterContext context)
			{
				PersistentDataContainer container = context.newPersistentDataContainer();
				
				container.set(key("using"), PersistentDataType.STRING, complex.contentAdapterClassName());
				
				container.set(
					key("lines"),
					PersistentDataType.TAG_CONTAINER_ARRAY,
					complex.lines().stream()
						.map(line -> {
							PersistentDataContainer data = context.newPersistentDataContainer();
							data.set(key("line"), PersistentDataType.BYTE, (byte) line.index());
							data.set(key("content"), PersistentDataType.STRING, line.content());
							return data;
						})
						.toArray(PersistentDataContainer[]::new)
				);
				
				return container;
			}
			
			@Override
			public ClipboardItemData fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context)
			{
				String using = Objects.requireNonNull(primitive.get(key("using"), PersistentDataType.STRING), "using");
				PersistentDataContainer[] lines = Objects.requireNonNull(primitive.get(key("lines"), PersistentDataType.TAG_CONTAINER_ARRAY), "lines");
				
				return new ClipboardItemData(
					using,
					Arrays.stream(lines)
						.map(data -> {
							@NullOr Byte index = data.get(key("line"), PersistentDataType.BYTE);
							@NullOr String content = data.get(key("content"), PersistentDataType.STRING);
							return (index == null || content == null) ? null : new SignLine<String>(index.intValue(), content);
						})
						.filter(Objects::nonNull)
						.collect(Collectors.toList())
				);
			}
		};
	}
}
