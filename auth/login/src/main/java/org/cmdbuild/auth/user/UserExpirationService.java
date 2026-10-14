package org.cmdbuild.auth.user;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.auth.config.UserRepositoryConfig;
import org.cmdbuild.scheduler.ScheduledJob;
import static org.cmdbuild.utils.date.CmDateUtils.now;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class UserExpirationService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserRepositoryConfig configuration;
    private final DaoService dao;

    public UserExpirationService(UserRepositoryConfig configuration, DaoService dao) {
        this.configuration = checkNotNull(configuration);
        this.dao = checkNotNull(dao);
    }

    @ScheduledJob("0 0 4 * * ?")// run once per day
    public void deactivateInactiveUsers() {
        if (configuration.expireUnusedUsers()) {
            ZonedDateTime expire = now().minus(configuration.getExpireUnusedUsersAfterDuration().getDuration()).minus(configuration.getExpireUnusedUsersAfterDuration().getPeriod());
            logger.debug("deactivate all users not active after %s ( %s ago )", toIsoDateTimeUtc(expire), configuration.getExpireUnusedUsersAfterDuration());
            dao.getJdbcTemplate().execute(format("UPDATE \"User\" SET \"Active\" = FALSE WHERE \"Status\" = 'A' AND \"Active\" = TRUE AND _cm3_card_creationdate_get('\"User\"',\"Id\") < %s  AND NOT EXISTS (SELECT * FROM \"_EventLog\" WHERE \"Code\" LIKE 'cm_session_%%' AND \"BeginDate\" > %s AND \"SessionUser\" = \"Username\")", systemToSqlExpr(expire), systemToSqlExpr(expire)));
        }
    }

}
