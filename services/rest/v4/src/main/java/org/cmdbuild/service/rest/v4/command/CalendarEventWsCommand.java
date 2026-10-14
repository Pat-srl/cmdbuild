/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.base.Splitter;
import jakarta.activation.DataHandler;
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.classe.access.CardHistoryService;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.report.SysReportService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.model.WsEventData;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Consumer;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_TABLE;
import static org.cmdbuild.calendar.data.CalendarEventRepositoryImpl.addCalendarEventUserFilter;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import static org.cmdbuild.report.utils.ReportExtUtils.reportExtFromString;
import static org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper.CAL_ATTR_MAPPING;
import static org.cmdbuild.service.rest.v4.command.ClassPrintWsCommand.buildQueryOptions;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class CalendarEventWsCommand {

    private final CalendarService calendarService;
    private final OperationUserSupplier operationUserSupplier;
    private final CardHistoryService cardHistoryService;
    private final DaoService daoService;
    private final SysReportService sysReportService;

    public CalendarEventWsCommand(CalendarService calendarService, OperationUserSupplier operationUserSupplier, CardHistoryService cardHistoryService, DaoService daoService, SysReportService sysReportService) {
        this.calendarService = checkNotNull(calendarService);
        this.operationUserSupplier = checkNotNull(operationUserSupplier);
        this.cardHistoryService = checkNotNull(cardHistoryService);
        this.daoService = checkNotNull(daoService);
        this.sysReportService = checkNotNull(sysReportService);
    }

    public CalendarEvent doReadOne(Long eventId) {
        return calendarService.getUserEvent(eventId);
    }

    public PagedElements<CalendarEvent> doReadMany(DaoQueryOptions queryOptions) {
        return calendarService.getUserEvents(queryOptions);
    }

    public CalendarEvent doCreateUserEvent(WsEventData data) {
        CalendarEvent event = data.buildEvent().accept((Consumer) calendarService.fixTimeZone()).withOwner(operationUserSupplier.getUsername()).build();
        checkArgument(!event.hasSequence(), "cannot create standalone sequence event");
        return calendarService.createUserEvent(event);//TODO access control, etc
    }

    public CalendarEvent doUpdate(Long eventId, WsEventData data) {
        CalendarEvent event = calendarService.updateEvent(data.buildEvent().accept((Consumer) calendarService.fixTimeZone()).withId(eventId).build());//TODO access control, etc
        return event;
    }

    public void doDelete(Long eventId) {
        calendarService.deleteEvent(eventId);
    }

    public PagedElements<DatabaseRecord> doGetHistory(Long eventId, WsQueryOptions wsQueryOptions, String types) {
        DaoQueryOptionsImpl query = DaoQueryOptionsImpl.copyOf(wsQueryOptions.getQuery())
                .withPaging(wsQueryOptions.getOffset(), wsQueryOptions.getLimit())
                .withFilter(wsQueryOptions.getQuery().getFilter())
                .accept(q -> {
                    if (wsQueryOptions.getQuery().getSorter().isNoop()) {
                        q.withSorter(CmdbSorterImpl.sorter(ATTR_BEGINDATE, DESC));
                    }
                }).build();
        List<CardHistoryService.HistoryElement> historyTypes = Splitter.on(",").splitToList(types).stream().map(e -> parseEnumOrNull(e, CardHistoryService.HistoryElement.class)).collect(toList());
        return cardHistoryService.getHistoryElements(CalendarEvent.EVENT_TABLE, eventId, query, historyTypes);
    }

    public Card doGetHistoryRecord(Long eventId, Long recordId) {
        Card record = cardHistoryService.getHistoryRecord(CalendarEvent.EVENT_TABLE, recordId);
        checkArgument(equal(record.getCurrentId(), eventId));
        return record;
    }

    public DataHandler doPrintCalendarEventReport(WsQueryOptions wsQueryOptions, String extension, String attributes) {
        Classe classe = daoService.getClasse(EVENT_TABLE);
        DaoQueryOptions queryOptions = buildQueryOptions(classe, wsQueryOptions, attributes).mapAttrNames(CAL_ATTR_MAPPING);
        return sysReportService.executeUserClassReport(classe, reportExtFromString(extension), queryOptions, () -> daoService.selectAll().from(classe).withOptions(queryOptions).accept(addCalendarEventUserFilter(operationUserSupplier.getUser())).getCards().stream());
    }
}
