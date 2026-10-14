/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

import static com.google.common.base.Functions.identity;
import com.google.common.collect.Ordering;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;

public class CmCacheUtils {

    public static Map<String, CmCacheStats> getStats(Map<String, CmCache> caches) {
        return caches.values().stream().map((c) -> CmCacheStatsImpl.builder().withName(c.getName()).withSize(c.estimatedSize()).withEstimateMemSize(c.approxMemSize()).build())
                .sorted(Ordering.natural().onResultOf(CmCacheStats::getName)).collect((toMap(CmCacheStats::getName, identity())));
    }

}
