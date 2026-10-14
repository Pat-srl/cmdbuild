/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

import java.lang.instrument.Instrumentation;
import java.util.Map;
import java.util.function.Supplier;
import jakarta.annotation.Nullable;
import org.slf4j.LoggerFactory;
import static org.cmdbuild.cache.InstrumentationUtils.getInstrumentationOrNull;

public interface CmCache<V> {

	String getName();

	@Nullable
	V getIfPresent(Object key);

	V get(Object key, Supplier<? extends V> loader);

	void put(Object key, V value);

	void putAll(Map<?, ? extends V> m);

	void invalidate(Object key);

	void invalidateAll(Iterable<?> keys);

	void invalidateAll();

	long estimatedSize();

	Map<String, V> asMap();

	void cleanUp();

	default long approxMemSize() {
		Instrumentation instrumentation = getInstrumentationOrNull();
		if (instrumentation == null) {
			LoggerFactory.getLogger(getClass()).warn("unable to estimate precise cache mem size: instrumentation not found");
			return estimatedSize() * 1024;//really rough estimate
		} else {
			return asMap().values().stream().map((o) -> {
				return instrumentation.getObjectSize(o); //TODO check if this is recursive; if not, replace this with something recursive
			}).mapToLong(Long.class::cast).sum();
		}
	}
}
