/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.common;

import org.bukkit.block.Sign;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;

public class SignLine<T>
{
	public enum Index
	{
		FIRST,
		SECOND,
		THIRD,
		FOURTH;
		
		public <T> SignLine<T> content(T content)
		{
			return new SignLine<>(this, content);
		}
	}
	
	private final Index index;
	private final T content;
	
	public SignLine(Index index, T content)
	{
		this.index = Objects.requireNonNull(index, "index");
		this.content = Objects.requireNonNull(content, "content");
	}
	
	public Index index() { return index; }
	
	public T content() { return content; }
	
	@Override
	public boolean equals(@NullOr Object o)
	{
		if (this == o) { return true; }
		if (o == null || getClass() != o.getClass()) { return false; }
		SignLine<?> signLine = (SignLine<?>) o;
		return index == signLine.index && content.equals(signLine.content);
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(index, content);
	}
	
	public static class Content
	{
		private Content() { throw new UnsupportedOperationException(); }
		
		@FunctionalInterface
		public interface Getter<T>
		{
			T getLineContent(Sign sign, int index);
		}
		
		@FunctionalInterface
		public interface Setter<T>
		{
			void setLineContent(Sign sign, int index, T content);
		}
		
		public interface Handler<T> extends Getter<T>, Setter<T>
		{
			static <T> Handler<T> of(Getter<T> getter, Setter<T> setter)
			{
				return new Handler<>()
				{
					@Override
					public T getLineContent(Sign sign, int index)
					{
						return getter.getLineContent(sign, index);
					}
					
					@Override
					public void setLineContent(Sign sign, int index, T content)
					{
						setter.setLineContent(sign, index, content);
					}
				};
			}
		}
	}
}
