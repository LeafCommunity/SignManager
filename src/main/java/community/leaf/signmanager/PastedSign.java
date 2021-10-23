/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import community.leaf.signmanager.exceptions.SignPasteException;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class PastedSign
{
	private final Sign sign;
	private final CopiedSign before;
	private final CopiedSign after;
	
	private boolean isUndone = false;
	
	public PastedSign(Sign sign, CopiedSign before, CopiedSign after)
	{
		this.sign = sign;
		this.before = before;
		this.after = after;
	}
	
	public Sign sign() { return sign; }
	
	public boolean isUndone() { return isUndone; }
	
	public void undo(Player player) throws SignPasteException
	{
		before.paste(sign, player);
		isUndone = true;
	}
	
	public void redo(Player player) throws SignPasteException
	{
		after.paste(sign, player);
		isUndone = false;
	}
}
