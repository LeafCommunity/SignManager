/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.common;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.block.Sign;

public interface SignLine extends SerializedSignLine
{
	@Override
	int index();
	
	void apply(Sign sign);
	
	String asPlainText();
	
	@Override
	String asSerializedString();
	
	BaseComponent[] asPreview();
}
