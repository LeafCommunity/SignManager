/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.common;

import community.leaf.signmanager.common.util.Signs;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;

public class SignLine<T>
{
	private final int index;
	private final T content;
	
	public SignLine(int index, T content)
	{
		this.index = Signs.index(index);
		this.content = Objects.requireNonNull(content, "content");
	}
	
	public int index() { return index; }
	
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
}
