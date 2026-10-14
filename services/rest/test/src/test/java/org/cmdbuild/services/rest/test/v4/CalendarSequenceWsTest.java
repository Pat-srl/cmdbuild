/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.calendar.beans.CalendarBuilderWithTimezone;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.CalendarSequenceWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CalendarSequenceWs;
import org.cmdbuild.service.rest.v4.model.WsSequenceData;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class CalendarSequenceWsTest extends WsTestBase {

    private final CalendarSequenceWs instance;

    private final CalendarSequence calendarSequence;
    private final List<CalendarSequence> calendarSequenceList;
    private final LookupValue lookupValue;
    private final WsSequenceData wsSequenceData;
    private final List<CalendarEvent> calendarEventList;

    public CalendarSequenceWsTest() {
        CalendarWsSerializationHelper calendarWsSerializationHelper = mockBuildCalendarWsSerializationHelper();
        CalendarSequenceWsCommand command = new CalendarSequenceWsCommand(calendarService);
        this.instance = new CalendarSequenceWs(calendarWsSerializationHelper, command);

        this.calendarSequence = mockBuildCalendarSequence(A_KNOWN_LONG_ID1);
        this.calendarSequenceList = mockBuildCalendarSequenceList();
        this.lookupValue = mockBuildLookupValue(A_KNOWN_LOOKUP_NAME1, A_KNOWN_LONG_ID1);
        this.wsSequenceData = TestHelper_WSData.mockBuildWsSequenceData(A_KNOWN_CARD_ID1);
        this.calendarEventList = mockBuildCalendarEventList();
    }

    @Test
    public void testReadOne_includeEvents() {
        System.out.println("readOne");

        //arrange:
        when(calendarService.getSequenceIncludeEvents(A_KNOWN_LONG_ID1)).thenReturn(calendarSequence);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.readOne(A_KNOWN_LONG_ID1, INCLUDE);

        //assert:
        verify(calendarService).getSequenceIncludeEvents(A_KNOWN_LONG_ID1);
        verify(calendarService, never()).getSequence(A_KNOWN_LONG_ID1);
        verify(lookupService, times(2)).getLookup(any(Enum.class));
        checkResult("testuser", resultObject, "owner");
    }

    @Test
    public void testReadOne_dontIncludeEvents() {
        System.out.println("readOne");

        //arrange:
        when(calendarService.getSequence(A_KNOWN_LONG_ID1)).thenReturn(calendarSequence);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.readOne(A_KNOWN_LONG_ID1, DONT_INCLUDE);

        //assert:
        verify(calendarService, never()).getSequenceIncludeEvents(A_KNOWN_LONG_ID1);
        verify(calendarService).getSequence(A_KNOWN_LONG_ID1);
        verify(lookupService, times(2)).getLookup(any(Enum.class));
        checkResult("testuser", resultObject, "owner");
    }

    @Test
    public void testReadManyByCard_detailed_includeEvents() {
        System.out.println("readManyByCard_detailed_includeEvents");

        //arrange:
        when(calendarService.getSequencesByCardIncludeEvents(A_KNOWN_CARD_ID1)).thenReturn(calendarSequenceList);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.readManyByCard(A_KNOWN_CARD_ID1, DETAILED, INCLUDE);

        //assert:
        verify(calendarService).getSequencesByCardIncludeEvents(A_KNOWN_CARD_ID1);
        verify(calendarService, never()).getSequencesByCard(anyLong());
        verify(lookupService, times(6)).getLookup(any(Enum.class));
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
    }

    @Test
    public void testReadManyByCard_detailed_dontIncludeEvents() {
        System.out.println("readManyByCard_detailed_dontIncludeEvents");

        //arrange:
        when(calendarService.getSequencesByCard(A_KNOWN_CARD_ID1)).thenReturn(calendarSequenceList);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.readManyByCard(A_KNOWN_CARD_ID1, DETAILED, DONT_INCLUDE);

        //assert:
        verify(calendarService).getSequencesByCard(A_KNOWN_CARD_ID1);
        verify(calendarService, never()).getSequencesByCardIncludeEvents(anyLong());
        verify(lookupService, times(6)).getLookup(any(Enum.class));
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
    }

    @Test
    public void testReadManyByCard_notDetailed_includeEvents() {
        System.out.println("readManyByCard_notDetailed_includeEvents");

        //arrange:
        when(calendarService.getSequencesByCardIncludeEvents(A_KNOWN_CARD_ID1)).thenReturn(calendarSequenceList);

        //act:
        Object resultObject = instance.readManyByCard(A_KNOWN_CARD_ID1, NOT_DETAILED, INCLUDE);

        //assert:
        verify(calendarService).getSequencesByCardIncludeEvents(A_KNOWN_CARD_ID1);
        verify(calendarService, never()).getSequencesByCard(anyLong());
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
    }

    @Test
    public void testReadManyByCard_notDetailed_dontIncludeEvents() {
        System.out.println("readManyByCard_notDetailed_dontIncludeEvents");

        //arrange:
        Map<String, Object> expStructure = map(
                "_id", null,
                "source", null,
                "card", null,
                "owner", null,
                "trigger", null
        );
        when(calendarService.getSequencesByCard(A_KNOWN_CARD_ID1)).thenReturn(calendarSequenceList);

        //act:
        Object resultObject = instance.readManyByCard(A_KNOWN_CARD_ID1, NOT_DETAILED, DONT_INCLUDE);

        //assert:
        verify(calendarService).getSequencesByCard(A_KNOWN_CARD_ID1);
        verify(calendarService, never()).getSequencesByCardIncludeEvents(anyLong());
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
        checkResultStructure(expStructure, (Map) ((List) ((Map) resultObject).get("data")).get(0));
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //assert:
        when(calendarService.fixTimeZone()).thenAnswer(invocation -> (Consumer<CalendarBuilderWithTimezone>) builder -> {
            if (builder.getTimeZone() == null || builder.getTimeZone().isBlank()) {
                builder.withTimeZone("UTC");
            }
        });
        when(calendarService.createSequence(any(CalendarSequence.class))).thenReturn(calendarSequence);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);


        //act:
        Object resultObject = instance.create(wsSequenceData);

        //assert:
        verify(calendarService).fixTimeZone();
        verify(calendarService).createSequence(any(CalendarSequence.class));
        verify(lookupService, times(2)).getLookup(any(Enum.class));
        checkResult(1L, resultObject, "_id");
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(calendarService.getSequence(A_KNOWN_LONG_ID1)).thenReturn(calendarSequence);
        when(calendarService.updateSequence(any(CalendarSequence.class))).thenReturn(calendarSequence);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.update(A_KNOWN_LONG_ID1, wsSequenceData);

        //assert:
        verify(calendarService).getSequence(A_KNOWN_LONG_ID1);
        verify(calendarService).updateSequence(any(CalendarSequence.class));
        verify(lookupService, times(2)).getLookup(any(Enum.class));
        checkResult(1L, resultObject, "_id");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_LONG_ID1);

        //assert:
        verify(calendarService).deleteSequence(A_KNOWN_LONG_ID1);
        checkSuccess(resultObject);
    }

    @Test
    public void testGetEventsPreview() {
        System.out.println("getEventsPreview");

        //arrange:
        when(calendarService.buildEventsFromSequence(any(CalendarSequence.class))).thenReturn(calendarEventList);

        //act:
        Object resultObject = instance.getEventsPreview(wsSequenceData);

        //assert:
        verify(calendarService).buildEventsFromSequence(any(CalendarSequence.class));
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
    }
}
