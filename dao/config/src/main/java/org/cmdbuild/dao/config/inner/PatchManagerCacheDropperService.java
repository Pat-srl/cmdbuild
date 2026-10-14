/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import org.cmdbuild.cache.CacheService;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PatchManagerCacheDropperService {

    private final CacheService cacheService;

    public PatchManagerCacheDropperService(@Qualifier(SYSTEM_LEVEL_ONE) CacheService cacheService, PatchService patchManager) {
        this.cacheService = checkNotNull(cacheService);
        patchManager.getEventBus().register(new Object() {
            @Subscribe
            public void handlePatchAppliedOnDbEvent(PatchAppliedOnDbEvent event) {
                invalidateCache();
            }
        });
    }

    private void invalidateCache() {
        cacheService.invalidateAll();
    }
}
