/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;

public class HolderImpl<V> implements Holder<V> {

	private final static String DEFAULT = "default";
	private final CmCache<V> inner;

	public HolderImpl(CmCache<V> inner) {
		this.inner = checkNotNull(inner);
	}

	@Override
	public V getIfPresent() {
		return inner.getIfPresent(DEFAULT);
	}

	@Override
	public boolean isPresent() {
		return getIfPresent() != null;
	}

	@Override
	public V get(Supplier<V> loader) {
		return inner.get(DEFAULT, () -> loader.get());
	}

	@Override
	public void invalidate() {
		inner.invalidateAll();
	}
}
