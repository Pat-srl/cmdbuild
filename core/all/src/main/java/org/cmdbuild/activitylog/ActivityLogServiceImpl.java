/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.activitylog;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import java.util.Map;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.config.ActivityLogConfiguration;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
public class ActivityLogServiceImpl implements ActivityLogService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final SessionService sessionService;
    private final RequestContextService requestContext;

    public ActivityLogServiceImpl(ActivityLogConfiguration config, DaoService dao, SessionService sessionService, RequestContextService requestContext, EventBusService eventBus) {
        this.dao = checkNotNull(dao);
        this.sessionService = checkNotNull(sessionService);
        this.requestContext = checkNotNull(requestContext);
        eventBus.getActivityLogEventBus().register(new Object() {
            @Subscribe
            public void handleActivity(ActivityLogInfo record) {
                String type = (String) record.getMetadata().get("Type");
                if (type != null && config.getEntries().containsKey(type) && config.getEntries().get(type).contains(record.getCode())) {
                    storeActivity(record);
                }
            }
        });
    }

    @Override
    public ActivityLogRecord store(ActivityLogRecord record) {
        return dao.create(record);
    }

    private ActivityLogRecord store(ActivityLogAction code, Map<String, Object> metadata) {
        Session session = sessionService.getCurrentSessionOrNull();
        return store(ActivityLogRecordImpl.builder()
                .withEventId(randomId())
                .withCode(code)
                .withSessionId(applyOrNull(session, Session::getSessionId))
                .withSessionUser(applyOrNull(session, s -> s.getOperationUser().getUsername()))
                .withRequestId(requestContext.getRequestContextIdOrNull())
                .withTimestamp(CmDateUtils.now())
                .withMetadata(metadata)
                .build());
    }

    private void storeActivity(ActivityLogInfo record) {
        checkNotNull(record);
        try {
            logger.debug("store activity = {}", record);
            store(record.getCode(), record.getMetadata());
        } catch (Exception ex) {
            logger.error(marker(), "error storing application event log record = {} with metadata = {}", record, mapToLoggableStringInline(record.getMetadata()), ex);
        }
    }
}
