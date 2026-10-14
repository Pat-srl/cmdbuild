/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.model;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.auth.session.SessionExpirationStrategy;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.auth.user.OperationUserStack;
import org.cmdbuild.auth.user.OperationUserStackImpl;
import static org.cmdbuild.auth.user.OperationUserStackImpl.toSimpleOperationUser;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * a session object. Most of this object data is immutable.
 *
 * Mutable fields are:<ul>
 * <li>lastActiveDate: initially equal to lastSavedDate, may be incremented if
 * the session is activated-modified and not yet saved</li>
 * <li>sessionData: session data is a mutable map of session data</li>
 * <li>dirty: a dirty flag is set to true if session data is modified</li>
 * </ul>
 *
 * all mutable fields are re-set in case of session save/persist (session data
 * is saved, other fields are re-set to their default values).
 *
 * all the other fields (operation user, etc) require a copy of the whole
 * session object to be modified.
 *
 * This session object is supposed to be created (or restored from persistence),
 * then accumulate session data and updates for a while, and then be persisted
 * again on storage.
 */
public class SessionImpl implements Session {

    private final OperationUserStack operationUserStack;
    private final String sessionId;
    private final ZonedDateTime beginDate, lastSavedDate;
    private final ZonedDateTime lastActiveDate, expirationDate;
    private final Map<String, Object> sessionData;
    private final boolean dirty;
    private final SessionExpirationStrategy expirationStrategy;

    private SessionImpl(NewSessionBuilder builder) {
        this.operationUserStack = checkNotNull(builder.operationUserStack);
        this.sessionId = checkNotBlank(builder.sessionId);
        this.beginDate = checkNotNull(builder.beginDate);
        this.lastActiveDate = checkNotNull(builder.lastActiveDate);
        this.expirationStrategy = checkNotNull(builder.expirationStrategy);
        this.lastSavedDate = builder.lastSaveDate;
        this.dirty = builder.isDirty;
        this.sessionData = map(builder.sessionData).immutable();
        switch (expirationStrategy) {
            case ES_EXPIRATIONDATE:
                this.expirationDate = checkNotNull(builder.expirationDate);
                break;
            default:
                this.expirationDate = null;

        }
    }

    @Override
    public OperationUserStack getOperationUser() {
        return operationUserStack;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

    @Override
    public ZonedDateTime getBeginDate() {
        return beginDate;
    }

    @Override
    public ZonedDateTime getLastActiveDate() {
        return lastActiveDate;
    }

    @Override
    @Nullable
    public ZonedDateTime getExpirationDate() {
        return expirationDate;
    }

    @Override
    public SessionExpirationStrategy getExpirationStrategy() {
        return expirationStrategy;
    }

    public static NewSessionBuilder builder() {
        return new NewSessionBuilder();
    }

    public static NewSessionBuilder copyOf(Session session) {
        return new NewSessionBuilder()
                .withExpirationDate(session.getExpirationDate())
                .withExpirationStrategy(session.getExpirationStrategy())
                .withBeginDate(session.getBeginDate())
                .withLastActiveDate(session.getLastActiveDate())
                .withLastSaveDate(session.getLastSavedDate())
                .withOperationUser(session.getOperationUser())
                .withSessionId(session.getSessionId())
                .withSessionData(session.getSessionData())
                .withDirty(session.isDirty());
    }

    @Override
    public Map<String, Object> getSessionData() {
        return sessionData;
    }

    @Override
    public @Nullable
    ZonedDateTime getLastSavedDate() {
        return lastSavedDate;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    public static class NewSessionBuilder implements Builder<Session, NewSessionBuilder> {

        private boolean isDirty = false;

        private OperationUserStack operationUserStack;
        private String sessionId;
        private ZonedDateTime beginDate, lastActiveDate, lastSaveDate, expirationDate;
        private SessionExpirationStrategy expirationStrategy;
        private Map<String, Object> sessionData = map();

        private NewSessionBuilder() {
            beginDate = lastActiveDate = now();
        }

        public NewSessionBuilder withExpirationDate(@Nullable ZonedDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public NewSessionBuilder withExpirationStrategy(SessionExpirationStrategy expirationStrategy) {
            this.expirationStrategy = expirationStrategy;
            return this;
        }

        public NewSessionBuilder withOperationUser(OperationUserStack operationUserStack) {
            this.operationUserStack = checkNotNull(operationUserStack);
            return this;
        }

        public NewSessionBuilder withOperationUser(OperationUser operationUser) {
            return this.withOperationUser(OperationUserStackImpl.wrapOrCast(operationUser));
        }

        public NewSessionBuilder withSessionId(String sessionId) {
            this.sessionId = checkNotNull(trimToNull(sessionId));
            return this;
        }

        public NewSessionBuilder withSessionData(Map<String, Object> sessionData) {
            sessionData = firstNotNull(sessionData, emptyMap());
            if (!equal(this.sessionData, sessionData)) {
                this.isDirty = true;
            }
            this.sessionData = map(sessionData);
            return this;
        }

        public NewSessionBuilder addSessionData(Map<String, Object> sessionData) {
            return this.withSessionData(map(this.sessionData).with(firstNotNull(sessionData, emptyMap())));
        }

        public NewSessionBuilder withBeginDate(ZonedDateTime beginDate) {
            this.beginDate = checkNotNull(beginDate);
            return this;
        }

        public NewSessionBuilder withDirty(boolean isDirty) {
            this.isDirty = isDirty;
            return this;
        }

        public NewSessionBuilder withLastActiveDate(ZonedDateTime lastActiveDate) {
            this.lastActiveDate = checkNotNull(lastActiveDate);
            return this;
        }

        public NewSessionBuilder withLastSaveDate(@Nullable ZonedDateTime lastSaveDate) {
            this.lastSaveDate = lastSaveDate;
            return this;
        }

        public NewSessionBuilder impersonate(OperationUser operationUser) {
            return this.withDirty(true).withOperationUser(new OperationUserStackImpl(ImmutableList.<OperationUser>builder().addAll(this.operationUserStack.getOperationUserStack()).add(toSimpleOperationUser(operationUser)).build()));
        }

        public NewSessionBuilder deImpersonate() {
            return this.withDirty(true).withOperationUser(new OperationUserStackImpl(ImmutableList.<OperationUser>builder().addAll(Iterables.limit(this.operationUserStack.getOperationUserStack(), this.operationUserStack.getOperationUserStackSize() - 1)).build()));
        }

        public NewSessionBuilder deImpersonateAll() {
            return this.withDirty(true).withOperationUser(new OperationUserStackImpl(this.operationUserStack.getRootOperationUser()));
        }

        @Override
        public Session build() {
            return new SessionImpl(this);
        }

    }

    @Override
    public String toString() {
        return "Session{" + "user=" + operationUserStack.getLoginUser().getUsername() + ", sessionId=" + sessionId + ", beginDate=" + beginDate + ", lastActiveDate=" + lastActiveDate + '}';
    }

}
