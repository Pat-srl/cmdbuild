/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.calendar.beans.CalendarBuilderWithTimezone;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.HistorySerializationHelper;
import org.cmdbuild.service.rest.v4.command.CalendarEventWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CalendarEventWs;
import org.cmdbuild.service.rest.v4.model.WsEventData;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.Map;
import java.util.function.Consumer;

import static org.cmdbuild.calendar.beans.CalendarEvent.EVENT_TABLE;
import static org.cmdbuild.classe.access.CardHistoryService.HistoryElement.HE_CARDS;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class CalendarEventWsTest extends WsTestBase {

    private final CalendarEventWsCommand command;
    private final CalendarEventWs instance;

    private final CalendarEvent calendarEvent;
    private final CalendarEvent calendarEvent2;
    private final CalendarEvent calendarEvent3;
    private final PagedElements<CalendarEvent> calendarEventPagedElements;
    private final WsEventData wsEventData;
    private final WsQueryOptions wsQueryOptions;
    private final Classe classe;
    private final Card card1;
    private final Card card2;
    private final Card card3;
    private final PagedElements<Card> cardPagedElements;
    private final DataHandler dataHandler;
    private final OperationUser operationUser;

    public CalendarEventWsTest() {
        CalendarWsSerializationHelper calendarWsSerializationHelper = mockBuildCalendarWsSerializationHelper();
        HistorySerializationHelper historySerializationHelper = mockBuildHistorySerializationHelper();

        this.command = new CalendarEventWsCommand(calendarService, operationUserSupplier, cardHistoryService, daoService, sysReportService);
        this.instance = new CalendarEventWs(calendarWsSerializationHelper, historySerializationHelper, cardMapperService, daoService, command);

        this.calendarEvent = mockBuildCalendarEvent(A_KNOWN_LONG_ID1);
        this.calendarEvent2 = mockBuildCalendarEvent(A_KNOWN_LONG_ID2);
        this.calendarEvent3 = mockBuildCalendarEvent(A_KNOWN_LONG_ID3);
        this.calendarEventPagedElements = new PagedElements<>(list(calendarEvent, calendarEvent2, calendarEvent3), 3);
        this.wsEventData = TestHelper_WSData.mockBuildWsEventData(A_KNOWN_LONG_ID1);
        this.wsQueryOptions = TestHelper_WSData.mockBuildWsQueryOptions(DETAILED);
        this.classe = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME1);

        Map<String, Object> attributes = map(
                "attr1", "val1",
                "attr2", 1L
        );
        this.card1 = mockBuildCard(A_KNOWN_CARD_ID1, classe, attributes);
        this.card2 = mockBuildCard(A_KNOWN_CARD_ID2, classe, attributes);
        this.card3 = mockBuildCard(A_KNOWN_CARD_ID3, classe, attributes);
        this.cardPagedElements = new PagedElements<>(list(card1, card2, card3), 3L);
        this.dataHandler = mockBuildDataHandler(A_TEST_DATAHANDLER_TYPE);
        this.operationUser = mockBuildOperationUser();
    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        when(calendarService.getUserEvent(A_KNOWN_LONG_ID1)).thenReturn(calendarEvent);

        //act:
        Object resultObject = instance.readOne(A_KNOWN_LONG_ID1, false);

        //assert:
        verify(calendarService).getUserEvent(A_KNOWN_LONG_ID1);
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
    }

    @Test
    public void testReadMany() {
        System.out.println("readMany");

        //arrange:
        when(calendarService.getUserEvents(any(DaoQueryOptions.class))).thenReturn(calendarEventPagedElements);

        //act:
        Object resultObject = instance.readMany(A_TEST_FILTER, A_TEST_SORT, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED, 0L, true);

        //assert:
        verify(calendarService).getUserEvents(any(DaoQueryOptions.class));
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
    }

    @Test
    public void testCreateUserEvent() {
        System.out.println("createUserEvent");

        //arrange;
        when(operationUserSupplier.getUsername()).thenReturn("name");
        when(calendarService.createUserEvent(any(CalendarEvent.class))).thenReturn(calendarEvent);
        when(calendarService.fixTimeZone()).thenAnswer(invocation ->
                (Consumer<CalendarBuilderWithTimezone>) builder -> {
                    if (builder.getTimeZone() == null || builder.getTimeZone().isBlank()) {
                        builder.withTimeZone("UTC");
                    }
                });

        //act;
        Object resultObject = instance.createUserEvent(wsEventData);

        //assert:
        verify(operationUserSupplier).getUsername();
        verify(calendarService).createUserEvent(any(CalendarEvent.class));
        verify(calendarService).fixTimeZone();
        checkResult(1L, resultObject, "_id");
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(calendarService.updateEvent(any(CalendarEvent.class))).thenReturn(calendarEvent);
        when(calendarService.fixTimeZone()).thenAnswer(invocation ->
                (Consumer<CalendarBuilderWithTimezone>) builder -> {
                    if (builder.getTimeZone() == null || builder.getTimeZone().isBlank()) {
                        builder.withTimeZone("UTC");
                    }
                });

        //act:
        Object resultObject = instance.update(A_KNOWN_LONG_ID1, wsEventData);

        //assert:
        verify(calendarService).updateEvent(any(CalendarEvent.class));
        verify(calendarService).fixTimeZone();
        checkResult(1L, resultObject, "_id");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_LONG_ID1);

        //assert:
        verify(calendarService).deleteEvent(A_KNOWN_LONG_ID1);
        checkSuccess(resultObject);
    }

    @Test
    public void testGetHistory() {
        System.out.println("getHistory");

        //arrange:
        when(cardHistoryService.getHistoryElements(eq(EVENT_TABLE), eq(A_KNOWN_LONG_ID1), any(DaoQueryOptions.class), anyList())).thenReturn(cardPagedElements);

        //act:
        Object resultObject = instance.getHistory(A_KNOWN_LONG_ID1, wsQueryOptions, HE_CARDS.toString());

        //assert:
        verify(cardHistoryService).getHistoryElements(eq(EVENT_TABLE), eq(A_KNOWN_LONG_ID1), any(DaoQueryOptions.class), anyList());
        checkResultList(list(A_KNOWN_CARD_ID1, A_KNOWN_CARD_ID2, A_KNOWN_CARD_ID3), resultObject, "_id");
    }

    @Test
    public void testGetHistoryRecord() {
        System.out.println("getHistoryRecord");

        //arrange:
        when(cardHistoryService.getHistoryRecord(EVENT_TABLE, A_KNOWN_LONG_ID1)).thenReturn(card1);
        when(cardMapperService.cardToObject(any(Card.class))).thenReturn(calendarEvent);

        //act:
        Object resultObject = instance.getHistoryRecord(A_KNOWN_CARD_ID1, A_KNOWN_LONG_ID1);

        //assert:
        verify(cardHistoryService).getHistoryRecord(EVENT_TABLE, A_KNOWN_LONG_ID1);
        verify(cardMapperService).cardToObject(any(Card.class));
        checkResult(1L, resultObject, "_id");
    }

    @Test
    public void testPrintCalendarEventReport() {
        System.out.println("printCalendarEventReport");

        //arrange:
        when(daoService.getClasse(EVENT_TABLE)).thenReturn(classe);
        when(sysReportService.executeUserClassReport(any(Classe.class), any(ReportFormat.class), any(DaoQueryOptions.class), any())).thenReturn(dataHandler);

        //act:
        Object resultObject = instance.printCalendarEventReport(wsQueryOptions, "csv", "[\"attr1\",\"attr2\"]");

        //assert:
        verify(daoService).getClasse(EVENT_TABLE);
        verify(sysReportService).executeUserClassReport(any(Classe.class), any(ReportFormat.class), any(DaoQueryOptions.class), any());
        checkDataHandler("text/plain", resultObject);
    }
}
