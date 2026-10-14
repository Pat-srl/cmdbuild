/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.CalendarViewEventWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CalendarViewEventWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.view.View;
import org.junit.Test;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildCalendarEvent;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildViewCalendarType;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class CalendarViewEventWsTest extends WsTestBase {

    private final CalendarViewEventWs instance;

    private final CalendarEvent calendarEvent;
    private final PagedElements<CalendarEvent> calendarEventPagedElements;
    private final View view;

    public CalendarViewEventWsTest() {
        CalendarWsSerializationHelper calendarWsSerializationHelper = mockBuildCalendarWsSerializationHelper();
        CalendarViewEventWsCommand command = new CalendarViewEventWsCommand(calendarService, viewService);
        this.instance = new CalendarViewEventWs(calendarWsSerializationHelper, command);

        this.calendarEvent = mockBuildCalendarEvent(A_KNOWN_LONG_ID1);
        this.calendarEventPagedElements = new PagedElements<>(list(calendarEvent, mockBuildCalendarEvent(A_KNOWN_LONG_ID2), mockBuildCalendarEvent(A_KNOWN_LONG_ID3)));
        this.view = mockBuildViewCalendarType(A_KNOWN_VIEW_ID);
    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        when(calendarService.getEventById(A_KNOWN_LONG_ID1)).thenReturn(calendarEvent);

        //act:
        Object resultObject = instance.readOne(A_KNOWN_LONG_ID1);

        //assert:
        verify(calendarService).getEventById(A_KNOWN_LONG_ID1);
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
    }

    @Test
    public void testReadMany() {
        System.out.println("readMany");

        //arrange:
        when(viewService.getSharedForCurrentUserByNameOrId(A_KNOWN_VIEW_ID)).thenReturn(view);
        when(calendarService.getUserEvents(any(DaoQueryOptions.class))).thenReturn(calendarEventPagedElements);

        //act:
        Object resultObject = instance.readMany(A_KNOWN_VIEW_ID, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED, A_TEST_FILTER);

        //assert:
        verify(viewService).getSharedForCurrentUserByNameOrId(A_KNOWN_VIEW_ID);
        verify(calendarService).getUserEvents(any(DaoQueryOptions.class));
        checkResultList(list(A_KNOWN_LONG_ID1, A_KNOWN_LONG_ID2, A_KNOWN_LONG_ID3), resultObject, "_id");
    }
}
