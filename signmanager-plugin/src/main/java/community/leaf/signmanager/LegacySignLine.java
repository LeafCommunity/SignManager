/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import community.leaf.signmanager.common.SignContentAdapter;
import community.leaf.signmanager.common.SignLine;
import community.leaf.signmanager.common.util.Signs;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.block.Sign;

import java.util.Objects;

public class LegacySignLine implements SignLine
{
	public static final SignContentAdapter ADAPTER =
		SignContentAdapter.of("legacy", Sign::getLine, LegacySignLine::new);
	
	private final int index;
	private final String content;
	
	public LegacySignLine(int index, String content)
	{
		this.index = Signs.index(index);
		this.content = Objects.requireNonNull(content, "content");
	}
	
	@Override
	public int index() { return index; }
	
	@Override
	public void apply(Sign sign) { sign.setLine(index(), content); }
	
	@Override
	public String asPlainText() { return ChatColor.stripColor(content); }
	
	@Override
	public String asSerializedString() { return content; }
	
	@Override
	public BaseComponent[] asPreview() { return TextComponent.fromLegacyText(content); }
}
