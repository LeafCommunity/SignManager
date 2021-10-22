/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.common;

import org.bukkit.block.Sign;

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
	
	public void undo()
	{
		before.apply(sign);
		isUndone = true;
	}
	
	public void redo()
	{
		after.apply(sign);
		isUndone = false;
	}
}
