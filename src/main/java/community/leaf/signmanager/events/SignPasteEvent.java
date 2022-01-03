/*
 * Copyright © 2016-2022, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.events;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public class SignPasteEvent extends SignChangeEvent
{
    private final Sign sign;
    
    public SignPasteEvent(Sign sign, Player thePlayer, String[] lines)
    {
        super(sign.getBlock(), thePlayer, lines);
        this.sign = sign;
    }
    
    public Sign getSign() { return sign; }
}
