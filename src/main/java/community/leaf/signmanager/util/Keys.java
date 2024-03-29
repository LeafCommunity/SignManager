/*
 * Copyright © 2016-2022, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util;

import org.bukkit.NamespacedKey;

public class Keys
{
    private Keys() { throw new UnsupportedOperationException(); }
    
    public static NamespacedKey signManager(String key)
    {
        return new NamespacedKey("signmanager", key);
    }
}
