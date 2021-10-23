/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.exceptions;

import community.leaf.signmanager.CopiedSign;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class SignPasteException extends Exception
{
	private final CopiedSign copy;
	private final Sign sign;
	private final Player player;
	
	public SignPasteException(CopiedSign copy, Sign sign, Player player)
	{
		this.copy = copy;
		this.sign = sign;
		this.player = player;
	}
	
	public CopiedSign copy() { return copy; }
	
	public Sign sign() { return sign; }
	
	public Player player() { return player; }
}
