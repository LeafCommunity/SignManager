/*
 * Copyright © 2016-2022, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util;

import net.md_5.bungee.api.chat.ComponentBuilder;

public class Components
{
    private Components() { throw new UnsupportedOperationException(); }
    
    // a new empty builder. . . . . .
    public static ComponentBuilder builder()
    {
        return new ComponentBuilder("");
    }
}
