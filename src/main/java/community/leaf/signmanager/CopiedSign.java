/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import community.leaf.eventful.bukkit.Events;
import community.leaf.signmanager.events.SignPasteEvent;
import community.leaf.signmanager.exceptions.SignPasteException;
import community.leaf.signmanager.util.Keys;
import community.leaf.signmanager.util.persistence.Persistent;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CopiedSign implements Persistent<PersistentDataContainer, CopiedSign>
{
	@SuppressWarnings("NullableProblems")
	public static final PersistentDataType<PersistentDataContainer, CopiedSign> TYPE =
		new PersistentDataType<>()
		{
			@Override
			public Class<PersistentDataContainer> getPrimitiveType() { return PersistentDataContainer.class; }
			
			@Override
			public Class<CopiedSign> getComplexType() { return CopiedSign.class; }
			
			@Override
			public PersistentDataContainer toPrimitive(CopiedSign complex, PersistentDataAdapterContext context)
			{
				PersistentDataContainer data = context.newPersistentDataContainer();
				
				data.set(
					Keys.signManager("lines"),
					Types.TAG_CONTAINER_ARRAY,
					complex.lines().stream()
						.map(line -> line.toPersistentData(context))
						.toArray(PersistentDataContainer[]::new)
				);
				
				return data;
			}
			
			@Override
			public CopiedSign fromPrimitive(PersistentDataContainer primitive, PersistentDataAdapterContext context)
			{
				@NullOr PersistentDataContainer @NullOr[] lines =
					primitive.get(Keys.signManager("lines"), Types.TAG_CONTAINER_ARRAY);
				
				if (lines == null) { throw new NullPointerException("lines"); }
				
				return new CopiedSign(
					Arrays.stream(lines)
						.filter(Objects::nonNull)
						.map(line -> SignLine.TYPE.fromPrimitive(line, context))
						.collect(Collectors.toList())
				);
			}
		};
	
	private final List<SignLine> lines;
	
	public CopiedSign(List<SignLine> lines)
	{
		this.lines = List.copyOf(lines);
	}
	
	public CopiedSign(Sign sign)
	{
		this(SignLine.list(sign.getLines()));
	}
	
	@Override
	public PersistentDataType<PersistentDataContainer, CopiedSign> persistentDataType() { return TYPE; }
	
	public List<SignLine> lines() { return lines; }
	
	public PastedSign paste(Sign sign, Player player) throws SignPasteException
	{
		CopiedSign snapshot = new CopiedSign(sign);
		
		String[] raw = sign.getLines();
		lines().forEach(line -> raw[line.index()] = line.text());
		
		SignChangeEvent event = Events.dispatcher().call(new SignPasteEvent(sign, player, raw));
		if (event.isCancelled()) { throw new SignPasteException(this, sign, player); }
		
		CopiedSign result = new CopiedSign(SignLine.list(event.getLines()));
		
		result.lines().forEach(line -> sign.setLine(line.index(), line.text()));
		sign.update();
		
		return new PastedSign(sign, snapshot, result);
	}
	
	@Override
	public boolean equals(@NullOr Object o)
	{
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		CopiedSign that = (CopiedSign) o;
		return lines.equals(that.lines);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(lines);
	}
}
