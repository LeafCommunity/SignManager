/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.common;

import org.bukkit.block.Sign;

import java.util.List;

public class CopiedSign implements SerializedCopiedSign
{
	private final SignContentAdapter contentAdapter;
	private final List<SignLine> lines;
	
	public CopiedSign(SignContentAdapter contentAdapter, List<SignLine> lines)
	{
		this.contentAdapter = contentAdapter;
		this.lines = lines;
	}
	
	public SignContentAdapter contentAdapter() { return contentAdapter; }
	
	public List<SignLine> lines() { return lines; }
	
	public PastedSign paste(Sign sign)
	{
		CopiedSign old = new CopiedSign(contentAdapter, contentAdapter.getAllLines(sign));
		apply(sign);
		return new PastedSign(sign, old, this);
	}
	
	protected void apply(Sign sign)
	{
		lines().forEach(line -> line.apply(sign));
		sign.update();
	}
	
	@Override
	public String contentAdapterKey() { return contentAdapter.key(); }
	
	@Override
	public List<? extends SerializedSignLine> serializedLines()
	{
		return lines;
	}
}
