/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import community.leaf.signmanager.common.Paste;
import community.leaf.signmanager.common.SignLine;
import org.bukkit.block.Sign;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CopyPasteHandler<T>
{
	private final SignLine.Content.Handler<T> contentHandler;
	
	public CopyPasteHandler(SignLine.Content.Handler<T> contentHandler)
	{
		this.contentHandler = contentHandler;
	}
	
	public List<SignLine<T>> copy(Sign sign)
	{
		return Arrays.stream(SignLine.Index.values())
			.map(index -> index.content(
				contentHandler.getLineContent(sign, index.ordinal())
			))
			.collect(Collectors.toList());
	}
	
	public Paste paste(Sign sign, List<SignLine<T>> lines)
	{
		List<SignLine<T>> old = copy(sign);
		
		return new Paste()
		{
			boolean isUndone = false;
			
			@Override
			public Sign sign() { return sign; }
			
			@Override
			public void undo()
			{
				apply(sign, old);
				isUndone = true;
			}
			
			@Override
			public boolean isUndone() { return isUndone; }
		};
	}
	
	private void apply(Sign sign, List<SignLine<T>> lines)
	{
		lines.forEach(line ->
			contentHandler.setLineContent(sign, line.index().ordinal(), line.content())
		);
		sign.update();
	}
}
