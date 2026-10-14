/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

import static java.util.Arrays.asList;
import java.util.Map;

public interface CacheService {

    <V> CmCache<V> newCache(String cacheName, CacheConfig cacheConfig);

    <V> CmCache<V> getCache(String cacheName);

    Map<String, CmCache> getAll();

    Map<String, CmCacheStats> getStats();

    void invalidateAll();

    default void invalidate(String... cacheIds) {
        asList(cacheIds).forEach(this::invalidate);
    }

    default void invalidate(String cacheId) {
        getCache(cacheId).invalidateAll();
    }

    default <V> CmCache<V> newCache(String cacheName) {
        return newCache(cacheName, CacheConfig.DEFAULT);
    }

    default <V> Holder<V> newHolder(String cacheName) {
        return newHolder(cacheName, CacheConfig.DEFAULT);
    }

    default <V> Holder<V> newHolder(String cacheName, CacheConfig cacheConfig) {
        return new HolderImpl<>(newCache(cacheName, cacheConfig));
    }

}
