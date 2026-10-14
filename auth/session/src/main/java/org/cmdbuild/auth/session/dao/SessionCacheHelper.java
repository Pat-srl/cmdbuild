/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.dao;

import com.google.common.eventbus.Subscribe;
import java.util.Optional;
import org.cmdbuild.auth.grant.GrantDataUpdatedEvent;
import org.cmdbuild.auth.grant.RoleUpdateEvent;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.cache.CacheService;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.eventbus.EventBusService;
import org.springframework.stereotype.Component;

@Component
public class SessionCacheHelper {

    private final CmCache<Optional<Session>> sessionCacheBySessionId;

    public SessionCacheHelper(EventBusService grantEventService, CacheService cacheService) {
        sessionCacheBySessionId = cacheService.newCache("session_cache");
        grantEventService.getGrantEventBus().register(new Object() {
            @Subscribe
            public void handleGrantDataUpdatedEvent(GrantDataUpdatedEvent event) {
                sessionCacheBySessionId.invalidateAll();
            }

            @Subscribe
            public void handleGroupUpdateEvent(RoleUpdateEvent event) {
                sessionCacheBySessionId.invalidateAll();
            }
        });
    }

    public CmCache<Optional<Session>> getCache() {
        return sessionCacheBySessionId;
    }

}
