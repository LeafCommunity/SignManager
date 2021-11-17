/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager.util;

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public interface FloatRange
{
	float min();
	
	float max();
	
	float resolve();
	
	static FloatRange of(float constant)
	{
		if (constant == 0F) { return Constant.ZERO; }
		if (constant == 1F) { return Constant.ONE; }
		return new Constant(constant);
	}
	
	static FloatRange of(float min, float max)
	{
		return (min == max) ? of(min) : new MinMax(min, max);
	}
	
	static FloatRange of(float ... bounds)
	{
		if (bounds.length == 1) { return of(bounds[0]); }
		if (bounds.length >= 2) { return of(bounds[0], bounds[1]); }
		throw new IllegalArgumentException("Missing bounds (empty array: expects at least one element)");
	}
	
	final class Constant implements FloatRange
	{
		public static final Constant ZERO = new Constant(0F);
		
		public static final Constant ONE = new Constant(1F);
		
		private final float constant;
		
		private Constant(float constant) { this.constant = constant; }
		
		@Override
		public float min() { return constant; }
		
		@Override
		public float max() { return constant; }
		
		@Override
		public float resolve() { return constant; }
		
		@Override
		public boolean equals(@NullOr Object o)
		{
			if (this == o) { return true; }
			if (o == null || getClass() != o.getClass()) { return false; }
			Constant constant1 = (Constant) o;
			return Float.compare(constant1.constant, constant) == 0;
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(constant);
		}
	}
	
	final class MinMax implements FloatRange
	{
		private final float min;
		private final float max;
		
		private MinMax(float min, float max)
		{
			this.min = Float.min(min, max);
			this.max = Float.max(min, max);
		}
		
		@Override
		public float min() { return min; }
		
		@Override
		public float max() { return max; }
		
		@Override
		public float resolve()
		{
			return (float) ThreadLocalRandom.current().nextDouble(min(), max());
		}
		
		@Override
		public boolean equals(@NullOr Object o)
		{
			if (this == o) { return true; }
			if (o == null || getClass() != o.getClass()) { return false; }
			MinMax minMax = (MinMax) o;
			return Float.compare(minMax.min, min) == 0 && Float.compare(minMax.max, max) == 0;
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(min, max);
		}
	}
}
