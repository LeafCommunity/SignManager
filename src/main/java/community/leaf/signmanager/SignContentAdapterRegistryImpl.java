/*
 * Copyright © 2016-2021, RezzedUp <https://github.com/LeafCommunity/SignManager>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package community.leaf.signmanager;

import community.leaf.signmanager.common.SignContentAdapter;
import community.leaf.signmanager.common.SignContentAdapterRegistry;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

final class SignContentAdapterRegistryImpl implements SignContentAdapterRegistry
{
	private final Map<String, SignContentAdapter> contentAdaptersByKey = new HashMap<>();
	
	private @NullOr SignContentAdapter defaultContentAdapter = null;
	
	SignContentAdapterRegistryImpl() {}
	
	void add(SignContentAdapter adapter)
	{
		contentAdaptersByKey.put(adapter.key(), adapter);
		defaultContentAdapter = adapter;
	}
	
	public SignContentAdapter defaultContentAdapter()
	{
		if (defaultContentAdapter != null) { return defaultContentAdapter; }
		throw new IllegalStateException("No sign content adapter added yet");
	}
	
	public Optional<SignContentAdapter> contentAdapterByKey(String key)
	{
		return Optional.ofNullable(contentAdaptersByKey.get(key));
	}
}
