/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.paper;

import community.leaf.signmanager.common.SignContentAdapter;
import community.leaf.signmanager.common.SignLine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.block.Sign;

public class AdventureSignLine implements SignLine
{
	public static final SignContentAdapter ADAPTER =
		SignContentAdapter.of("adventure", Sign::line, AdventureSignLine::new, (index, serialized) ->
			new AdventureSignLine(index, GsonComponentSerializer.gson().deserialize(serialized))
		);
	
	private final int index;
	private final Component component;
	
	public AdventureSignLine(int index, Component component)
	{
		this.index = index;
		this.component = component;
	}
	
	@Override
	public int index() { return index; }
	
	@Override
	public void apply(Sign sign) { sign.line(index, component); }
	
	@Override
	public String asPlainText() { return PlainTextComponentSerializer.plainText().serialize(component); }
	
	@Override
	public String asSerializedString() { return GsonComponentSerializer.gson().serialize(component); }
	
	@Override
	public BaseComponent[] asPreview() { return ComponentSerializer.parse(asSerializedString()); }
}
