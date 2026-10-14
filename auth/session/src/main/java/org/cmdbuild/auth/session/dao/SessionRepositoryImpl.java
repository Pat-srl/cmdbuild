/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Lists.transform;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.login.AuthenticationService;
import org.cmdbuild.auth.login.LoginDataImpl;
import org.cmdbuild.auth.multitenant.api.MultitenantService;
import org.cmdbuild.auth.multitenant.api.UserTenantContext;
import org.cmdbuild.auth.role.RoleRepository;
import org.cmdbuild.auth.session.dao.beans.SessionDataJsonBean;
import org.cmdbuild.auth.session.dao.beans.SessionDataJsonBean.AuthenticatedUserJsonBean;
import org.cmdbuild.auth.session.dao.beans.SessionDataJsonBean.OperationUserJsonBean;
import org.cmdbuild.auth.session.dao.beans.SessionDataJsonBean.UserTenantContextJsonBean;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.session.model.SessionImpl;
import org.cmdbuild.auth.user.OperationUserImpl;
import org.cmdbuild.auth.user.OperationUserStack;
import org.cmdbuild.auth.user.OperationUserStackImpl;
import org.cmdbuild.cache.CmCache;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.scheduler.ScheduledJob;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmExecutorUtils.safe;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SessionRepositoryImpl implements SessionRepository {

    public static final String SESSION_CLASS_NAME = "_Session",
            SESSION_ID_ATTRIBUTE = "SessionId",
            DATA_ATTRIBUTE = "Data",
            LAST_ACTIVE_DATE_ATTRIBUTE = "LastActiveDate";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SessionDataRepository repo;
    private final CoreConfiguration configuration;
    private final AuthenticationService authenticationService;
    private final CmCache<Optional<Session>> sessionCacheBySessionId;

    public SessionRepositoryImpl(MultitenantService multitenantService, SessionDataRepository repo, CoreConfiguration configuration, AuthenticationService authenticationService, SessionCacheHelper cacheHelper, RequestContextService requestContextService, RoleRepository groupRepository) {
        logger.info("init");
        this.repo = checkNotNull(repo);
        this.configuration = checkNotNull(configuration);
        this.authenticationService = checkNotNull(authenticationService);
        sessionCacheBySessionId = cacheHelper.getCache();
    }

    private void invalidateCache() {
        sessionCacheBySessionId.invalidateAll();
    }

    @ScheduledJob("0 0 * * * ?")// run once per hour
    public void cleanupSessionStore() {
        logger.debug("delete expired sessions from session store");
        deleteExpiredSessions(Period.seconds(configuration.getSessionTimeoutOrDefault() + configuration.getSessionPersistDelay()));
    }

    @ScheduledJob("0 */30 * * * ?")// run once per hour
    public void refreshSessionStore() {
        logger.debug("refresh sessions from session store");
        refreshActiveSessions(Period.seconds(configuration.getSessionTimeoutOrDefault() + configuration.getSessionPersistDelay()));
    }

    @Override
    public int getActiveSessionCount() {
        return repo.getActiveSessionCount(Period.seconds(configuration.getSessionActivePeriodForStatistics()));
    }

    @Override
    public List<Session> getAllSessions() {
        return list(repo.getAllSessions()).map(safe(this::sessionDataToSession, null)).filter(Objects::nonNull);
    }

    @Nullable
    @Override
    public Session getSessionByIdOrNull(String sessionId) {
        logger.trace("findSessionBySessionId, sessionId = {}", sessionId);
        Optional<Session> session = sessionCacheBySessionId.get(sessionId, () -> {
            SessionData data = repo.getSessionDataByIdOrNull(sessionId);
            if (data == null) {
                return Optional.empty();
            } else {
                return Optional.of(sessionDataToSession(data));
            }
        });
        if (!session.isPresent()) {
            logger.trace("session not found for id = {}", sessionId);
            return null;
        } else if (!isExpired(session.get())) {
            return session.get();
        } else {
            logger.debug("invalid (expired) session found = {}; return null", session.get());
            return null;
        }
    }

    @Override
    public Session createOrUpdateSession(Session session) {
        logger.trace("update session = {}", session);
        if (session.isNew() || session.isDirty() || (ChronoUnit.SECONDS.between(session.getLastSavedDate(), session.getLastActiveDate()) > configuration.getSessionPersistDelay())) {
            session = SessionImpl.copyOf(session).withLastSaveDate(now()).build();
            doUpdateSession(session);
            sessionCacheBySessionId.put(session.getSessionId(), Optional.of(session));
        } else {
            logger.trace("update on db is not required for session = {}", session);
        }
        return session;
    }

    @Override
    public void deleteSession(String sessionId) {
        repo.deleteSession(sessionId);
        sessionCacheBySessionId.invalidate(sessionId);
    }

    @Override
    public void deleteAll() {
        logger.info("delete all sessions");
        repo.deleteAll();
        invalidateCache();
    }

    private boolean isExpired(Session session) {
        return switch (session.getExpirationStrategy()) {
            case ES_DEFAULT ->
                ChronoUnit.SECONDS.between(session.getLastActiveDate(), CmDateUtils.now()) > configuration.getSessionTimeoutOrDefault();
            case ES_EXPIRATIONDATE ->
                session.getExpirationDate().isBefore(CmDateUtils.now());
            case ES_NEVER ->
                false;
            default ->
                throw unsupported("unsupported expiration strategy = %s", session.getExpirationStrategy());
        };
    }

    private void deleteExpiredSessions(Period expireTime) {
        repo.deleteExpiredSessions(expireTime);
    }

    private void refreshActiveSessions(Period expireTime) {
        repo.refreshActiveSessions(expireTime);
    }

    private Session sessionDataToSession(SessionData data) {
        try {
            logger.debug("found session for id = {}, deserializing", data.getSessionId());
            SessionDataJsonBean sessionData = data.getData();
            OperationUserStack operationUser = new OperationUserStackImpl(sessionData.getOperationUserStack().stream().map((operationUserData) -> {

                return OperationUserImpl.copyOf(authenticationService.validateCredentialsAndCreateOperationUser(LoginDataImpl.builder()
                        .withNoPasswordRequired()
                        .allowServiceUser()
                        .withLoginString(operationUserData.getAuthenticatedUser().getUsername())
                        .withGroupName(operationUserData.getGroup())
                        .withIgnoreTenantPolicies(operationUserData.getUserTenantContext().getIgnoreTenantPolicies())
                        .withActiveTenants(operationUserData.getUserTenantContext().getActiveTenantIds())
                        .withDefaultTenant(operationUserData.getUserTenantContext().getDefaultTenantId())
                        .build())).withParams(operationUserData.getParams()).build();

            }).collect(toImmutableList()));
            logger.debug("deserialized session data, returning session for user = {}", operationUser);
            return SessionImpl.builder()
                    .withSessionId(data.getSessionId())
                    .withBeginDate(data.getLoginDate())
                    .withLastActiveDate(toDateTime(data.getLastActiveDate()))
                    .withLastSaveDate(toDateTime(data.getLastActiveDate()))
                    .withOperationUser(operationUser)
                    .withSessionData(sessionData.getSessionData())
                    .withExpirationDate(data.getExpirationDate())
                    .withExpirationStrategy(data.getExpirationStrategy())
                    .build();
        } catch (Exception ex) {
            throw runtime(ex, "error processing session data = %s", data);
        }
    }

    private void doUpdateSession(Session session) {
        logger.debug("update session on db, session = {}", session);
        SessionDataJsonBean sessionData = new SessionDataJsonBean(transform(session.getOperationUser().getOperationUserStack(), (operationUser) -> {
            UserTenantContext userTenantContext = operationUser.getUserTenantContext();
            UserTenantContextJsonBean tenantBean = new UserTenantContextJsonBean(userTenantContext.getActiveTenantIds(), userTenantContext.getDefaultTenantId(), userTenantContext.ignoreTenantPolicies());
            return new OperationUserJsonBean(new SessionDataJsonBean.PrivilegeContextJsonBean(copyOf(operationUser.getPrivilegeContext().getSourceGroups())),
                    operationUser.getDefaultGroupNameOrNull(),
                    new AuthenticatedUserJsonBean("regular", operationUser.getLoginUser().getUsername()),
                    new AuthenticatedUserJsonBean("regular", operationUser.getSponsor().getUsername()),
                    tenantBean,
                    operationUser.getParams()
            );
        }), session.getSessionData());
        repo.createOrUpdateSession(session.getSessionId(), sessionData);
    }

}
