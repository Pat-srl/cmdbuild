/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.lock.ItemLock;
import org.cmdbuild.utils.lang.CmMapUtils;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.lock.LockScopeUtils.serializeLockScope;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class ItemLockSerializationHelper {

    public static CmMapUtils.FluentMap<String, Object> serializeLockData(ItemLock lock) {
        return map("_id", lock.getItemId(),
                "sessionId", lock.getSessionId(),
                "requestId", lock.getRequestId(),
                "scope", serializeLockScope(lock.getScope()),
                "beginDate", toIsoDateTime(lock.getBeginDate()),
                "lastActive", toIsoDateTime(lock.getLastActiveDate()));
    }

    public static CmMapUtils.FluentMap<String, Object> serializeLock(ItemLock lock, SessionService sessionService) {
        return serializeLockData(lock).with("_owned_by_current_session", equal(sessionService.getCurrentSessionIdOrNull(), lock.getSessionId()));
    }
}
