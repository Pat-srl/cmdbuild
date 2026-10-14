/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.eventlog;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.Nullable;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.GT;
import static org.cmdbuild.eventlog.EventLogRecord.EVENT_LOG_ATTR_CARD;
import org.cmdbuild.fault.FaultEventCollector;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.fault.FaultEventsData;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EventLogServiceImpl implements EventLogService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final SessionService sessionService;
    private final RequestContextService requestContext;
    private final FaultEventCollectorService errorAndWarningCollectorService;

    public EventLogServiceImpl(FaultEventCollectorService errorAndWarningCollectorService, DaoService dao, SessionService sessionService, RequestContextService requestContext, EventLogEventBusService eventBusService) {
        this.dao = checkNotNull(dao);
        this.sessionService = checkNotNull(sessionService);
        this.requestContext = checkNotNull(requestContext);
        this.errorAndWarningCollectorService = checkNotNull(errorAndWarningCollectorService);
        eventBusService.getEventBus().register(new Object() {

            @Subscribe
            public void handleEventLogInfo(EventLogInfo info) {
                storeEvent(info);
            }
        });
    }

    @Override
    public EventLogRecord store(EventLogRecord event) {
        return dao.create(event);
    }

    @Override
    public boolean hasEvent(String code, long cardId) {
        return dao.selectCount().from(EventLogRecord.class).where(ATTR_CODE, EQ, checkNotBlank(code)).where(EVENT_LOG_ATTR_CARD, EQ, cardId).getCount() > 0;
    }

    @Override
    public EventLogRecord store(String code, @Nullable Long cardId, Map<String, Object> data) {
        Session session = sessionService.getCurrentSessionOrNull();
        return store(EventLogRecordImpl.builder()
                .withEventId(randomId())
                .withCode(code)
                .withCard(cardId)
                .withData(data)
                .withSessionId(applyOrNull(session, Session::getSessionId))
                .withUsername(applyOrNull(session, s -> s.getOperationUser().getUsername()))
                .withRequestId(requestContext.getRequestContextIdOrNull())
                .withTimestamp(CmDateUtils.now())
                .withErrorsData(FaultEventsData.fromErrorsAndWarningEvents(errorAndWarningCollectorService.getCurrentRequestEventCollectorIfExists().map(FaultEventCollector::getCollectedEvents).orElse(emptyList())))
                .build());
    }

    @Override
    public void storeEvent(EventLogInfo event) {
        checkNotNull(event);
        try {
            logger.debug("store event = {}", event);
            store(event.getCode(), event.getCard(), event.getData());
        } catch (Exception ex) {
            logger.error(marker(), "error storing application event log record = {} with card = {} data = {}", event, event.getCard(), mapToLoggableStringInline(event.getData()), ex);
        }
    }

    @Override
    public List<EventLogRecord> getEvents(String code, ZonedDateTime since) {
        return dao.selectAll().from(EventLogRecord.class).where(ATTR_CODE, EQ, checkNotBlank(code)).where(ATTR_BEGINDATE, GT, checkNotNull(since)).asList();
    }

}
