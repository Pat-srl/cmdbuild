/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import static com.google.common.collect.Iterables.transform;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(SYSTEM_LEVEL_ONE)
public class LocalCacheServiceImpl implements CacheService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, CmCache> caches = map();

    @Override
    public synchronized <V> CmCache<V> getCache(String cacheName) {
        return checkNotNull(caches.get(cacheName), "cache not found for name = %s", cacheName);
    }

    @Override
    public synchronized <V> CmCache<V> newCache(String cacheName, CacheConfig cacheConfig) {
        logger.debug("create cache {} (configuration {})", cacheName, cacheConfig);
        checkArgument(!caches.containsKey(cacheName), "already created a cache for name = %s", cacheName);
        CmCache<V> cmdbCache = new LocalCache<>(cacheName, cacheConfig);
        caches.put(cacheName, cmdbCache);
        return cmdbCache;
    }

    @Override
    public Map<String, CmCache> getAll() {
        return Collections.unmodifiableMap(caches);
    }

    @Override
    public synchronized void invalidateAll() {
        logger.debug("invalidate all local caches");
        caches.values().forEach((cache) -> {
            cache.invalidateAll();
        });
    }

    @Override
    public synchronized Map<String, CmCacheStats> getStats() {
        return CmCacheUtils.getStats(caches);
    }

    private static class LocalCache<V> implements CmCache<V> {

        private final String cacheName;
        private final Cache<String, V> inner;

        public LocalCache(String cacheName, CacheConfig cacheConfig) {
            this.cacheName = checkNotBlank(cacheName);
            checkNotNull(cacheConfig);
            CacheBuilder builder = CacheBuilder.newBuilder();

            switch (cacheConfig) {
                case DEFAULT ->
                    builder.expireAfterWrite(Duration.ofHours(1)).softValues();
                case SYSTEM_OBJECTS -> {
                }
                default ->
                    throw new IllegalArgumentException();
            }
            inner = builder.build();
        }

        @Override
        public V getIfPresent(Object key) {
            return inner.getIfPresent(String.valueOf(key));
        }

        @Override
        public V get(Object key, Supplier<? extends V> loader) {
            try {
                return inner.get(String.valueOf(key), loader::get);
            } catch (ExecutionException ex) {
                throw runtime(ex, "error loading key = %s for cache = %s", key, cacheName);
            }
        }

        @Override
        public void put(Object key, V value) {
            inner.put(String.valueOf(key), value);
        }

        @Override
        public void putAll(Map<?, ? extends V> m) {
            m.forEach(this::put);
        }

        @Override
        public void invalidate(Object key) {
            inner.invalidate(String.valueOf(key));
        }

        @Override
        public void invalidateAll(Iterable<?> keys) {
            inner.invalidateAll(transform(keys, String::valueOf));
        }

        @Override
        public void invalidateAll() {
            inner.invalidateAll();
        }

        @Override
        public long estimatedSize() {
//				return inner.estimatedSize();
            return inner.size();
        }

//			@Override TODO
//			public CacheStats stats() {
//				return inner.stats();
//			}
        @Override
        public Map<String, V> asMap() {
            return inner.asMap();
        }

        @Override
        public void cleanUp() {
            inner.cleanUp();
        }

        @Override
        public String getName() {
            return cacheName;
        }
    }

}
