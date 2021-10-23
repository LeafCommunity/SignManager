/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import community.leaf.signmanager.util.Keys;
import community.leaf.signmanager.util.Persistable;
import community.leaf.signmanager.util.Signs;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Sign;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class SignLine implements Persistable<PersistentDataContainer, SignLine>
{
	@SuppressWarnings("NullableProblems")
	public static final PersistentDataType<PersistentDataContainer, SignLine> TYPE =
		new PersistentDataType<>()
		{
			@Override
			public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
			
			@Override
			public Class<SignLine> getComplexType() { return SignLine.class; }
			
			@Override
			public PersistentDataContainer toPrimitive(SignLine complex, PersistentDataAdapterContext context)
			{
				PersistentDataContainer data = context.newPersistentDataContainer();
				
				data.set(Keys.signManager("index"), PersistentDataType.BYTE, (byte) complex.index());
				data.set(Keys.signManager("text"), PersistentDataType.STRING, complex.text());
				
				return data;
			}
			
			@Override
			public SignLine fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context)
			{
				@NullOr Byte index = primitive.get(Keys.signManager("index"), PersistentDataType.BYTE);
				if (index == null) { throw new NullPointerException("index"); }
				
				@NullOr String content = primitive.get(Keys.signManager("text"), PersistentDataType.STRING);
				if (content == null) { throw new NullPointerException("text"); }
				
				return new SignLine(index.intValue(), content);
			}
		};
	
	public static Stream<SignLine> stream(String ... raw)
	{
		return IntStream.range(0, raw.length).sorted().mapToObj(index -> new SignLine(index, raw[index]));
	}
	
	public static List<SignLine> list(String ... raw)
	{
		return stream(raw).collect(Collectors.toList());
	}
	
	private static final String BLANK = ChatColor.translateAlternateColorCodes('&', "&8&o(Blank)");
	
	private final int index;
	private final String text;
	
	public SignLine(int index, String text)
	{
		this.index = Signs.index(index);
		this.text = Objects.requireNonNull(text, "text");
	}
	
	@Override
	public PersistentDataType<PersistentDataContainer, SignLine> persistentDataType() { return TYPE; }
	
	public int index() { return index; }
	
	public String text() { return text; }
	
	public void apply(Sign sign) { sign.setLine(index(), text); }
	
	public String toPlainText() { return ChatColor.stripColor(text); }
	
	public BaseComponent[] toPreview()
	{
		return TextComponent.fromLegacyText((text.isEmpty()) ? BLANK : text);
	}
	
	@Override
	public boolean equals(@NullOr Object o)
	{
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		SignLine signLine = (SignLine) o;
		return index == signLine.index && text.equals(signLine.text);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(index, text);
	}
}
