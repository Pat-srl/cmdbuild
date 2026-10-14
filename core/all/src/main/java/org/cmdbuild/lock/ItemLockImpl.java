/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lock;

import java.time.ZonedDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.lock.LockScopeUtils.parseLockScope;
import static org.cmdbuild.lock.LockScopeUtils.serializeLockScope;

@CardMapping("_Lock")
public class ItemLockImpl implements ItemLock {

    private final int timeToLiveSeconds;
    private final String itemId, sessionId, requestId;
    private final ZonedDateTime beginDate, lastActiveDate;
    private final LockScope scope;

    private ItemLockImpl(ItemLockImplBuilder builder) {
        this.timeToLiveSeconds = firstNotNull(builder.timeToLiveSeconds, 0);
        this.itemId = checkNotBlank(builder.itemId);
        this.sessionId = checkNotBlank(builder.sessionId);
        this.beginDate = firstNotNull(builder.beginDate, now());
        this.lastActiveDate = firstNotNull(builder.lastActiveDate, now());
        this.scope = checkNotNull(builder.scope);
        switch (scope) {
            case LS_REQUEST:
                this.requestId = checkNotBlank(builder.requestId);
                break;
            case LS_SESSION:
                this.requestId = "_any";
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    @CardAttr("TimeToLive")
    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    @Override
    @CardAttr
    public String getItemId() {
        return itemId;
    }

    @Override
    @CardAttr
    public String getSessionId() {
        return sessionId;
    }

    @Override
    @CardAttr
    public String getRequestId() {
        return requestId;
    }

    @Override
    @CardAttr(value = ATTR_BEGINDATE, writeToDb = false)
    public ZonedDateTime getBeginDate() {
        return beginDate;
    }

    @Override
    @CardAttr
    public ZonedDateTime getLastActiveDate() {
        return lastActiveDate;
    }

    @Override
    public LockScope getScope() {
        return scope;
    }

    @CardAttr(ITEMLOCK_ATTR_SCOPE)
    public String getScopeAsString() {
        return serializeLockScope(scope);
    }

    @Override
    public String toString() {
        return "ItemLock{" + "itemId=" + itemId + ", sessionId=" + sessionId + ", requestId=" + requestId + ", scope=" + scope + '}';
    }

    public static ItemLockImplBuilder builder() {
        return new ItemLockImplBuilder();
    }

    public static ItemLockImplBuilder copyOf(ItemLock source) {
        return new ItemLockImplBuilder()
                .withTimeToLiveSeconds(source.getTimeToLiveSeconds())
                .withItemId(source.getItemId())
                .withSessionId(source.getSessionId())
                .withRequestId(source.getRequestId())
                .withBeginDate(source.getBeginDate())
                .withLastActiveDate(source.getLastActiveDate())
                .withScope(source.getScope());
    }

    public static class ItemLockImplBuilder implements Builder<ItemLockImpl, ItemLockImplBuilder> {

        private Integer timeToLiveSeconds;
        private String itemId;
        private String sessionId;
        private String requestId;
        private ZonedDateTime beginDate;
        private ZonedDateTime lastActiveDate;
        private LockScope scope;

        public ItemLockImplBuilder withTimeToLiveSeconds(Integer timeToLiveSeconds) {
            this.timeToLiveSeconds = timeToLiveSeconds;
            return this;
        }

        public ItemLockImplBuilder withItemId(String itemId) {
            this.itemId = itemId;
            return this;
        }

        public ItemLockImplBuilder withSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public ItemLockImplBuilder withRequestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public ItemLockImplBuilder withBeginDate(ZonedDateTime beginDate) {
            this.beginDate = beginDate;
            return this;
        }

        public ItemLockImplBuilder withLastActiveDate(ZonedDateTime lastActiveDate) {
            this.lastActiveDate = lastActiveDate;
            return this;
        }

        public ItemLockImplBuilder withScope(LockScope scope) {
            this.scope = scope;
            return this;
        }

        public ItemLockImplBuilder withScopeAsString(String scope) {
            return this.withScope(parseLockScope(scope));
        }

        @Override
        public ItemLockImpl build() {
            return new ItemLockImpl(this);
        }

    }
}
