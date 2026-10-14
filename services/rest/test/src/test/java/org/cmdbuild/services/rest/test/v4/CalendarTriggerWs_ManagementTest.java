/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.CalendarWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.CalendarTriggerWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.CalendarTriggerWs_Management;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkSuccess;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class CalendarTriggerWs_ManagementTest extends WsTestBase {

    private final CalendarTriggerWs_Management instance;

    private final List<CalendarTrigger> calendarTriggerList;
    private final Classe classe;
    private final AttributeWithoutOwner attribute1;
    private final AttributeWithoutOwner attribute2;
    private final AttributeWithoutOwner attribute3;
    private final LookupValue lookupValue;
    private final CalendarSequence calendarSequence;
    private final WsQueryOptions wsQueryOptions;
    private final PagedElements<Card> cardPagedElements;

    public CalendarTriggerWs_ManagementTest() {
        CalendarWsSerializationHelper calendarWsSerializationHelper = mockBuildCalendarWsSerializationHelper();
        CalendarTriggerWsCommand command = new CalendarTriggerWsCommand(calendarService, userCardService);
        this.instance = new CalendarTriggerWs_Management(calendarWsSerializationHelper, command);

        this.calendarTriggerList = mockBuildCalendarTriggerList();
        this.attribute1 = mockBuildAttributeWithoutOwner("TestAttr");
        this.attribute2 = mockBuildAttributeWithoutOwner(A_KNOWN_NAME2);
        this.attribute3 = mockBuildAttributeWithoutOwner(A_KNOWN_NAME3);
        this.classe = mockBuildClasse(A_KNOWN_NAME1, list(attribute1, attribute2, attribute3));
        this.lookupValue = mockBuildLookupValue(A_KNOWN_LOOKUP_NAME1, A_KNOWN_LOOKUP_ID1);
        this.calendarSequence = mockBuildCalendarSequence(A_KNOWN_LONG_ID1);
        this.wsQueryOptions = TestHelper_WSData.mockBuildWsQueryOptions(DETAILED);
        this.cardPagedElements = new PagedElements<>(list(mockBuildCard(A_KNOWN_CARD_ID1, classe, map())));

    }

    @Test
    public void testReadOne() {
        System.out.println("readOne");

        //arrange:
        when(calendarService.getTriggerById(A_KNOWN_LONG_ID1)).thenReturn(calendarTriggerList.get(0));
        when(daoService.getClasse(anyString())).thenReturn(classe);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.readOne(A_KNOWN_LONG_ID1);

        //assert:
        verify(calendarService).getTriggerById(A_KNOWN_LONG_ID1);
        verify(daoService, times(2)).getClasse(anyString());
        checkResult(A_KNOWN_NAME1, resultObject, "code");
    }

    @Test
    public void testGetSequencePreview() {
        System.out.println("getSequencePreview");

        //arrange:
        when(calendarService.buildSequenceFromTrigger(A_KNOWN_LONG_ID1, toDate("2026-05-20"))).thenReturn(calendarSequence);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.getSequencePreview(A_KNOWN_LONG_ID1, "2026-05-20");

        //assert:
        verify(calendarService).buildSequenceFromTrigger(A_KNOWN_LONG_ID1, toDate("2026-05-20"));
        verify(lookupService, times(2)).getLookup(any(Enum.class));
        checkResult(A_KNOWN_LONG_ID1, resultObject, "_id");
    }

    @Test
    public void testCreateEvents_creatableTrigger() {
        System.out.println("createEvents_creatableTrigger");

        //arrange:
        when(calendarService.getTriggerById(anyLong())).thenReturn(calendarTriggerList.get(0));
        when(userCardService.getUserCards(anyString(), any(DaoQueryOptions.class))).thenReturn(cardPagedElements);

        //act:
        Object resultObject = instance.createEvents("123", wsQueryOptions);

        //assert:
        verify(calendarService).getTriggerById(anyLong());
        verify(calendarService, never()).getTriggerByCode(anyString());
        verify(userCardService).getUserCards(anyString(), any(DaoQueryOptions.class));
        verify(calendarService).createSequenceFromTrigger(anyLong(), any(Card.class));
        checkSuccess(resultObject);
    }

    @Test
    public void testCreateEvents_nonCreatableTrigger() {
        System.out.println("createEvents_creatableTrigger");

        //arrange:
        when(calendarService.getTriggerByCode(anyString())).thenReturn(calendarTriggerList.get(0));
        when(userCardService.getUserCards(anyString(), any(DaoQueryOptions.class))).thenReturn(cardPagedElements);

        //act:
        Object resultObject = instance.createEvents("nonCreatableTrigger", wsQueryOptions);

        //assert:
        verify(calendarService, never()).getTriggerById(anyLong());
        verify(calendarService).getTriggerByCode(anyString());
        verify(userCardService).getUserCards(anyString(), any(DaoQueryOptions.class));
        verify(calendarService).createSequenceFromTrigger(anyLong(), any(Card.class));
        checkSuccess(resultObject);
    }
}
