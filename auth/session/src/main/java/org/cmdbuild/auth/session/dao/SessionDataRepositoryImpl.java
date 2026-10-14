/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.dao;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.auth.session.SessionExpirationStrategy.ES_DEFAULT;
import static org.cmdbuild.auth.session.dao.SessionRepositoryImpl.SESSION_CLASS_NAME;
import static org.cmdbuild.auth.session.dao.SessionRepositoryImpl.SESSION_ID_ATTRIBUTE;
import org.cmdbuild.auth.session.dao.beans.SessionDataJsonBean;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import org.cmdbuild.requestcontext.RequestContextActiveService;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class SessionDataRepositoryImpl implements SessionDataRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final RequestContextActiveService requestService;

    public SessionDataRepositoryImpl(DaoService dao, RequestContextActiveService requestService) {
        this.dao = checkNotNull(dao);
        this.requestService = checkNotNull(requestService);
    }

    @Override
    public @Nullable
    SessionData getSessionDataByIdOrNull(String sessionId) {
        return dao.selectAll().from(SessionData.class).where(SESSION_ID_ATTRIBUTE, EQ, checkNotBlank(sessionId)).getOneOrNull();
    }

    @Override
    public List<SessionData> getAllSessions() {
        return dao.selectAll().from(SessionData.class).asList();
    }

//    @Override
//    public List<SessionData> getByUsername(String username) {
//		return dao.selectAll().from(SessionData.class).whereExpr("", params).asList();
//    }
    @Override
    public void deleteSession(String sessionId) {
        dao.getJdbcTemplate().update("DELETE FROM \"" + SESSION_CLASS_NAME + "\" WHERE \"" + SESSION_ID_ATTRIBUTE + "\" = ?", checkNotNull(trimToNull(sessionId)));
    }

    @Override
    public void deleteAll() {
        dao.getJdbcTemplate().update("DELETE FROM \"" + SESSION_CLASS_NAME + "\" ");
    }

    @Override
    public int getActiveSessionCount(Period activePeriod) {
        return dao.getJdbcTemplate().queryForObject(format("SELECT COUNT(*) _count FROM \"_Session\" WHERE \"LastActiveDate\" > NOW() - '%s seconds'::interval", activePeriod.getSeconds()), Integer.class);
    }

    @Override
    public SessionData createOrUpdateSession(String sessionId, SessionDataJsonBean sessionData) {
        SessionData current = getSessionDataByIdOrNull(sessionId);
        if (current == null) {
            logger.debug("create session = {}", sessionId);
            return dao.create(SessionDataImpl.builder()
                    .withSessionId(sessionId)
                    .withData(sessionData)
                    .withLoginDate(now())
                    .withLastActiveDate(now())
                    .withExpirationStrategy(ES_DEFAULT)
                    .build());
        } else {
            logger.debug("update session = {}", current);
            return dao.update(SessionDataImpl.copyOf(current)
                    .withData(sessionData)
                    .withLastActiveDate(now())
                    .build());
        }
    }

    @Override
    public void refreshActiveSessions(Period expireTime) {
        logger.debug("refreshing active sessions");
        dao.selectAll().from(SESSION_CLASS_NAME)
                .whereExpr("( \"ExpirationStrategy\" = 'default' AND \"LastActiveDate\" < NOW() - '? seconds'::interval )", expireTime.getSeconds() / 2)
                .asList(SessionData.class).forEach(session -> {
            if (requestService.isRequestContextActiveForSession(session.getSessionId())) {
                dao.getJdbcTemplate().update(format("UPDATE \"_Session\" SET \"LastActiveDate\" = NOW() WHERE \"SessionId\"='%s'", session.getSessionId()));
            }
        });
    }

    @Override
    public void deleteExpiredSessions(Period expireTime) {
        logger.debug("deleting expired sessions (older than {})", expireTime);
        int res = dao.getJdbcTemplate().update(format("DELETE FROM \"_Session\" WHERE"
                + " ( \"ExpirationStrategy\" = 'default' AND \"LastActiveDate\" < NOW() - '%s seconds'::interval )"
                + " OR ( \"ExpirationStrategy\" = 'expirationdate' AND NOW() > \"ExpirationDate\" )", expireTime.getSeconds()));
        if (res > 0) {
            logger.info("deleted {} expired sessions (older than {})", res, expireTime);
        }
    }

}
