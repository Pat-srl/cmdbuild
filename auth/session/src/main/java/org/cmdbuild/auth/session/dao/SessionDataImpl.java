/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.dao;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import java.time.ZonedDateTime;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.session.SessionExpirationStrategy;
import org.cmdbuild.auth.session.dao.beans.SessionDataJsonBean;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.json.JsonBean;

@CardMapping("_Session")
public class SessionDataImpl implements SessionData {

    private final Long id;
    private final String sessionId;
    private final ZonedDateTime beginDate, lastActiveDate, expirationDate;
    private final SessionDataJsonBean data;
    private final SessionExpirationStrategy expirationStrategy;

    private SessionDataImpl(SessionDataImplBuilder builder) {
        this.id = builder.id;
        this.sessionId = checkNotBlank(builder.sessionId);
        this.beginDate = firstNonNull(builder.beginDate, now());
        this.lastActiveDate = checkNotNull(builder.lastActiveDate);
        expirationStrategy = checkNotNull(builder.expirationStrategy);
        data = checkNotNull(builder.data);
        checkArgument(data.getVersion() == SessionDataJsonBean.BEAN_VERSION_ID, "unsupported json session data version = %s, current is = %s (json session data from db is not compatible with current java code)", data.getVersion(), SessionDataJsonBean.BEAN_VERSION_ID);
        switch (expirationStrategy) {
            case ES_EXPIRATIONDATE:
                this.expirationDate = checkNotNull(builder.expirationDate);
                break;
            default:
                this.expirationDate = null;

        }
    }

    @Override
    @CardAttr(ATTR_ID)
    public @Nullable
    Long getId() {
        return id;
    }

    @Override
    @CardAttr
    public String getSessionId() {
        return sessionId;
    }

    @Override
    @CardAttr
    @JsonBean
    public SessionDataJsonBean getData() {
        return data;
    }

    @Override
    @CardAttr("LoginDate")
    public ZonedDateTime getLoginDate() {
        return beginDate;
    }

    @Override
    @CardAttr
    public ZonedDateTime getLastActiveDate() {
        return lastActiveDate;
    }

    @Override
    @Nullable
    @CardAttr
    public ZonedDateTime getExpirationDate() {
        return expirationDate;
    }

    @Override
    public SessionExpirationStrategy getExpirationStrategy() {
        return expirationStrategy;
    }

    @CardAttr("ExpirationStrategy")
    public String getExpirationStrategyStr() {
        return serializeEnum(expirationStrategy);//TODO improve this
    }

    @Override
    public String toString() {
        return "SessionData{" + "id=" + id + ", sessionId=" + sessionId + '}';
    }

    public static SessionDataImplBuilder builder() {
        return new SessionDataImplBuilder();
    }

    public static SessionDataImplBuilder copyOf(SessionData source) {
        return new SessionDataImplBuilder()
                .withId(source.getId())
                .withSessionId(source.getSessionId())
                .withData(source.getData())
                .withLoginDate(source.getLoginDate())
                .withLastActiveDate(source.getLastActiveDate())
                .withExpirationDate(source.getExpirationDate())
                .withExpirationStrategy(source.getExpirationStrategy());
    }

    public static class SessionDataImplBuilder implements Builder<SessionDataImpl, SessionDataImplBuilder> {

        private Long id;
        private String sessionId;
        private SessionDataJsonBean data;
        private ZonedDateTime beginDate;
        private ZonedDateTime lastActiveDate, expirationDate;
        private SessionExpirationStrategy expirationStrategy;

        public SessionDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public SessionDataImplBuilder withSessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }

        public SessionDataImplBuilder withData(SessionDataJsonBean data) {
            this.data = data;
            return this;
        }

        public SessionDataImplBuilder withLoginDate(ZonedDateTime beginDate) {
            this.beginDate = beginDate;
            return this;
        }

        public SessionDataImplBuilder withLastActiveDate(ZonedDateTime lastActiveDate) {
            this.lastActiveDate = lastActiveDate;
            return this;
        }

        public SessionDataImplBuilder withExpirationDate(ZonedDateTime expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        public SessionDataImplBuilder withExpirationStrategy(SessionExpirationStrategy expirationStrategy) {
            this.expirationStrategy = expirationStrategy;
            return this;
        }

        public SessionDataImplBuilder withExpirationStrategyStr(String expirationStrategy) {
            return this.withExpirationStrategy(parseEnum(expirationStrategy, SessionExpirationStrategy.class));
        }

        @Override
        public SessionDataImpl build() {
            return new SessionDataImpl(this);
        }

    }
}
