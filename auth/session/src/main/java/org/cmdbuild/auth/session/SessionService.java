package org.cmdbuild.auth.session;

import jakarta.annotation.Nullable;
import org.cmdbuild.auth.login.LoginData;
import org.cmdbuild.auth.login.RequestAuthenticatorResponse;
import org.cmdbuild.auth.session.inner.SessionDataService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.auth.session.model.SessionImpl;
import org.cmdbuild.auth.user.OperationUser;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface SessionService extends SessionDataService {

    String create(LoginData login);

    boolean exists(String id);

    void update(String id, LoginData login);

    RequestAuthenticatorResponse<Void> deleteSession(String sessionId, @Nullable Object request);

    void deleteAll();

    void impersonate(OperationUser user);

    void impersonate(ImpersonateRequest request);

    void deimpersonate();

    @Nullable
    String getCurrentSessionIdOrNull();

    void setCurrent(@Nullable String id);

    boolean sessionExistsAndHasDefaultGroup(String id);

    OperationUser getUser(String sessionId);

    /**
     * mostly the same as impersonate
     *
     * @param id
     * @param user
     */
    void setUser(String id, OperationUser user);

    /**
     *
     * @param sessionId
     * @throws RuntimeException if session is not valid //TODO: throw different
     * exceptions for not found-expired
     */
    void validateSessionId(String sessionId);

    List<Session> getAllSessions();

    /**
     *
     * @param sessionId
     * @return session data
     * @throws RuntimeException if session not found for id
     */
    Session getSessionById(String sessionId);

    /**
     *
     * @param sessionId
     * @return session data, or null if not found
     */
    @Nullable
    Session getSessionByIdOrNull(String sessionId);

    void updateCurrentSession(Function<Session, Session> fun);

    /**
     * get current session; throw exception if no session available
     *
     * @return current session
     * @throws RuntimeException if no valid session is available
     */
    Session getCurrentSession();

    /**
     * return current session, or null if no valid session exists
     *
     * @return current session or null
     */
    @Nullable
    Session getCurrentSessionOrNull();

    int getActiveSessionCount();

    TransientSessionHelper createAndSetTransient(String username);

    default RequestAuthenticatorResponse<Void> deleteSession(String sessionId) {
        return deleteSession(sessionId, null);
    }

    default String createAndSet(LoginData login) {
        String session = create(login);
        setCurrent(session);
        return session;
    }

    default void impersonate(@Nullable String username, @Nullable String group) {
        impersonate(new ImpersonateRequestImpl(username, group));
    }

    default void impersonate(String username) {
        impersonate(username, null);
    }

    default String getCurrentSessionId() {
        return checkNotBlank(getCurrentSessionIdOrNull(), "session id not available");
    }

    default void updateCurrentSessionData(Function<Map, Map> fun) {
        updateCurrentSession((s) -> SessionImpl.copyOf(s).withSessionData(fun.apply(s.getSessionData())).build());
    }

    /**
     *
     * @return true if session existed and was deleted, false otherwise
     */
    default boolean deleteCurrentSessionIfExists() {
        if (hasSession()) {
            deleteSession(getCurrentSessionId());
            return true;
        } else {
            return false;
        }
    }

    default boolean hasSession() {
        return isNotBlank(getCurrentSessionIdOrNull()) && getSessionByIdOrNull(getCurrentSessionId()) != null;
    }

    enum CurrentSessionSetOrUpdateEvent {
        INSTANCE;
    }

    void disableUserMappingMode();

    void enableUserMappingFunctionMode();

    void enableUserMappingClassMode(String className, String attributeName);

}
