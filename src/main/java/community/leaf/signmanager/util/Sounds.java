/*
 * Copyright © 2016-2022, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util;

import org.bukkit.Sound;

public class Sounds
{
    private Sounds() { throw new UnsupportedOperationException(); }
    
    public static FloatRange volume(float ... bounds) { return FloatRange.of(bounds); }
    
    public static FloatRange pitch(float ... bounds) { return FloatRange.of(bounds); }
    
    public static PlayableSound playable(Sound sound)
    {
        return new PlayableSound(sound, FloatRange.Constant.ONE, FloatRange.Constant.ONE);
    }
    
    public static PlayableSound playable(Sound sound, FloatRange volume)
    {
        return new PlayableSound(sound, volume, FloatRange.Constant.ONE);
    }
    
    public static PlayableSound playable(Sound sound, FloatRange volume, FloatRange pitch)
    {
        return new PlayableSound(sound, volume, pitch);
    }
}
