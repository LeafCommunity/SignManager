/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import org.bukkit.block.Sign;

import java.util.List;
import java.util.stream.Collectors;

public class CopiedSign implements SerializedCopiedSign
{
	public static CopiedSign deserialize(SignContentAdapterRegistry contentAdapters, SerializedCopiedSign serializedCopy)
	{
		String key = serializedCopy.contentAdapterKey();
		SignContentAdapter adapter = contentAdapters.contentAdapterByKey(key)
			.orElseThrow(() -> new IllegalStateException("No content adapter found with key: " + key));
		
		return new CopiedSign(
			adapter,
			serializedCopy.serializedLines().stream()
				.map(line -> adapter.deserializeLine(line.index(), line.asSerializedString()))
				.collect(Collectors.toList())
		);
	}
	
	private final SignContentAdapter contentAdapter;
	private final List<SignLine> lines;
	
	public CopiedSign(SignContentAdapter contentAdapter, List<SignLine> lines)
	{
		this.contentAdapter = contentAdapter;
		this.lines = List.copyOf(lines);
	}
	
	public SignContentAdapter contentAdapter() { return contentAdapter; }
	
	public List<SignLine> lines() { return lines; }
	
	public PastedSign paste(Sign sign)
	{
		CopiedSign snapshot = new CopiedSign(contentAdapter, contentAdapter.allLines(sign));
		apply(sign);
		return new PastedSign(sign, snapshot, this);
	}
	
	protected void apply(Sign sign)
	{
		lines().forEach(line -> line.apply(sign));
		sign.update();
	}
	
	@Override
	public String contentAdapterKey() { return contentAdapter.key(); }
	
	@Override
	public List<? extends SerializedSignLine> serializedLines() { return lines; }
}
