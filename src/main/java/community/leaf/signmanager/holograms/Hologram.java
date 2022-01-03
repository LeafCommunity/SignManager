/*
 * Copyright © 2016-2022, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.holograms;

import org.bukkit.Location;

public interface Hologram
{
    static Location baseOffsetFromTopLocation(Location top)
    {
        // Height: 1.975 blocks
        return top.clone().subtract(0, 1.975, 0);
    }
    
    Location location();
    
    boolean isDestroyed();
    
    void destroy();
}
