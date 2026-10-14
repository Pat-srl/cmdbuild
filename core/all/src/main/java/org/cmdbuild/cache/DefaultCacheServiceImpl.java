/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

import com.google.common.eventbus.Subscribe;
import java.util.Collections;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.cluster.ClusterMessageImpl;
import org.cmdbuild.cluster.ClusterMessageReceivedEvent;
import org.cmdbuild.cluster.ClusterService;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_TWO;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Primary
@Qualifier(SYSTEM_LEVEL_TWO)
public class DefaultCacheServiceImpl implements CacheService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final String CLUSTER_MESSAGE_INVALIDATE_MANY = "cache.invalidate_many",
            CLUSTER_MESSAGE_DATA_KEYS_PARAM = "keys",
            CLUSTER_MESSAGE_DATA_CACHE_PARAM = "cache",
            CLUSTER_MESSAGE_INVALIDATE_ALL = "cache.invalidate_all";

    private final CacheService localCacheService;
    private final ClusterService clusteringService;

    private final Map<String, MyCache> caches = map();

    public DefaultCacheServiceImpl(@Qualifier(SYSTEM_LEVEL_ONE) CacheService localCacheService, ClusterService clusteringService) {
        this.localCacheService = checkNotNull(localCacheService);
        this.clusteringService = checkNotNull(clusteringService);
        clusteringService.getEventBus().register(new Object() {

            @Subscribe
            public void handleClusterMessageReceivedEvent(ClusterMessageReceivedEvent event) {
                try {
                    if (event.isOfType(CLUSTER_MESSAGE_INVALIDATE_MANY)) {
                        String cache = checkNotNull(event.getData(CLUSTER_MESSAGE_DATA_CACHE_PARAM));
                        Iterable keys = checkNotNull(event.getData(CLUSTER_MESSAGE_DATA_KEYS_PARAM));
                        logger.debug("received cache invalidate from cluster for cache = {} keys = {}", cache, keys);
                        checkNotNull(caches.get(cache), "cache not found for name = {}", cache).getInner().invalidateAll(keys);
                    } else if (event.isOfType(CLUSTER_MESSAGE_INVALIDATE_ALL)) {
                        String cache = checkNotNull(event.getData(CLUSTER_MESSAGE_DATA_CACHE_PARAM));
                        logger.debug("received cache invalidate from cluster for cache = {}, all keys", cache);
                        checkNotNull(caches.get(cache), "cache not found for name = {}", cache).getInner().invalidateAll();
                    }
                } catch (Exception ex) {
                    logger.error("error processing cluster message event " + event, ex);
                }
            }
        });
    }

    @Override
    public synchronized <V> CmCache<V> newCache(String cacheName, CacheConfig cacheConfig) {
        logger.debug("create cache {} (configuration {})", cacheName, cacheConfig);
        checkNotNull(trimToNull(cacheName));
        checkNotNull(cacheConfig);
        checkArgument(!caches.containsKey(cacheName), "cache name '%s' already used", cacheName);
        CmCache<V> innerCache = localCacheService.newCache(cacheName + "_CLUSTERED", cacheConfig);
        MyCache<V> cache = new MyCache(cacheName, innerCache);
        caches.put(cacheName, cache);
        return cache;
    }

    private void invalidateAllOnCluster(String cache) {
        logger.debug("invalidate all cache entries on cluster for cache = {}", cache);
        checkNotNull(cache);
        clusteringService.sendMessage(ClusterMessageImpl.builder()
                .withMessageType(CLUSTER_MESSAGE_INVALIDATE_ALL)
                .withMessageData(Map.of(CLUSTER_MESSAGE_DATA_CACHE_PARAM, cache))
                .build());
    }

    private void invalidateOneOnCluster(String cache, Object key) {
        invalidateManyOnCluster(cache, singletonList(key));
    }

    private void invalidateManyOnCluster(String cache, Iterable<Object> keys) {
        checkNotNull(keys);
        checkNotBlank(cache);
        logger.debug("invalidate many cache entries on cluster for cache = {} keys = {}", cache, keys);
        keys = List.copyOf(list(keys).map(String::valueOf));
        keys.forEach((key) -> checkNotBlank((String) key));
        clusteringService.sendMessage(ClusterMessageImpl.builder()
                .withMessageType(CLUSTER_MESSAGE_INVALIDATE_MANY)
                .withMessageData(Map.of(CLUSTER_MESSAGE_DATA_CACHE_PARAM, cache, CLUSTER_MESSAGE_DATA_KEYS_PARAM, keys))
                .build());
    }

    @Override
    public Map<String, CmCache> getAll() {
        return Collections.unmodifiableMap(caches);
    }

    @Override
    public synchronized void invalidateAll() {
        logger.info("invalidate all caches");
        caches.values().forEach((cache) -> {
            cache.invalidateAll();
        });
    }

    @Override
    public synchronized <V> CmCache<V> getCache(String cacheName) {
        return checkNotNull(caches.get(cacheName), "cache not found for name = %s", cacheName);
    }

    @Override
    public Map<String, CmCacheStats> getStats() {
        return CmCacheUtils.getStats((Map) caches);
    }

    private class MyCache<V> implements CmCache<V> {

        private final String name;
        private final CmCache<V> inner;

        public MyCache(String name, CmCache<V> inner) {
            this.name = checkNotBlank(name);
            this.inner = checkNotNull(inner);
        }

        @Override
        public String getName() {
            return name;
        }

        public CmCache<V> getInner() {
            return inner;
        }

        @Override
        public V getIfPresent(Object key) {
            return inner.getIfPresent(key);
        }

        @Override
        public V get(Object key, Supplier<? extends V> loader) {
            return inner.get(key, loader);
        }

        @Override
        public void put(Object key, V value) {
            inner.put(key, value);
            invalidateOneOnCluster(name, key);
        }

        @Override
        public void putAll(Map<?, ? extends V> m) {
            inner.putAll(m);
            invalidateManyOnCluster(name, (Iterable) m.keySet());
        }

        @Override
        public void invalidate(Object key) {
            inner.invalidate(key);
            invalidateOneOnCluster(name, key);
        }

        @Override
        public void invalidateAll(Iterable<?> keys) {
            inner.invalidateAll(keys);
            invalidateManyOnCluster(name, (Iterable) keys);
        }

        @Override
        public void invalidateAll() {
            inner.invalidateAll();
            invalidateAllOnCluster(name);
        }

        @Override
        public long estimatedSize() {
            return inner.estimatedSize();
        }

        @Override
        public Map<String, V> asMap() {
            return Collections.unmodifiableMap(inner.asMap());
        }

        @Override
        public void cleanUp() {
            inner.cleanUp();
        }

    }

}
