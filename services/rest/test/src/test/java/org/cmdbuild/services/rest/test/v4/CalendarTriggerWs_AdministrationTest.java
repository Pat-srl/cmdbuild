/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.calendar.beans.CalendarBuilderWithTimezone;
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
import org.cmdbuild.service.rest.v4.endpoint.CalendarTriggerWs_Administration;
import org.cmdbuild.service.rest.v4.model.WsTriggerData;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class CalendarTriggerWs_AdministrationTest extends WsTestBase {

    private final CalendarTriggerWs_Administration instance;

    private final List<CalendarTrigger> calendarTriggerList;
    private final Classe classe;
    private final AttributeWithoutOwner attribute1;
    private final AttributeWithoutOwner attribute2;
    private final AttributeWithoutOwner attribute3;
    private final LookupValue lookupValue;
    private final CalendarSequence calendarSequence;
    private final WsQueryOptions wsQueryOptions;
    private final PagedElements<Card> cardPagedElements;
    private final WsTriggerData wsTriggerData;

    public CalendarTriggerWs_AdministrationTest() {
        CalendarWsSerializationHelper calendarWsSerializationHelper = mockBuildCalendarWsSerializationHelper();
        CalendarTriggerWsCommand command = new CalendarTriggerWsCommand(calendarService, userCardService);
        this.instance = new CalendarTriggerWs_Administration(calendarWsSerializationHelper, command);

        this.calendarTriggerList = mockBuildCalendarTriggerList();
        this.attribute1 = mockBuildAttributeWithoutOwner("TestAttr");
        this.attribute2 = mockBuildAttributeWithoutOwner(A_KNOWN_NAME2);
        this.attribute3 = mockBuildAttributeWithoutOwner(A_KNOWN_NAME3);
        this.classe = mockBuildClasse(A_KNOWN_NAME1, list(attribute1, attribute2, attribute3));
        this.lookupValue = mockBuildLookupValue(A_KNOWN_LOOKUP_NAME1, A_KNOWN_LOOKUP_ID1);
        this.calendarSequence = mockBuildCalendarSequence(A_KNOWN_LONG_ID1);
        this.wsQueryOptions = TestHelper_WSData.mockBuildWsQueryOptions(DETAILED);
        this.cardPagedElements = new PagedElements<>(list(mockBuildCard(A_KNOWN_CARD_ID1, classe, map())));
        this.wsTriggerData = TestHelper_WSData.mockBuildWsTriggerData(A_KNOWN_CODE1);
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
        verify(lookupService, times(2)).getLookup(any(Enum.class));
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

    @Test
    public void testReadMany() {
        System.out.println("readMany");

        //arrange:
        when(calendarService.getAllTriggers()).thenReturn(calendarTriggerList);
        when(daoService.getClasse(anyString())).thenReturn(classe);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.readMany(wsQueryOptions);

        //assert:
        verify(calendarService).getAllTriggers();
        verify(daoService, times(12)).getClasse(anyString());
        verify(lookupService, times(6)).getLookup(any(Enum.class));
        checkResultList(list(A_KNOWN_NAME1, A_KNOWN_NAME2, A_KNOWN_NAME3), resultObject, "code");
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        when(calendarService.fixTimeZone()).thenAnswer(invocation -> (Consumer<CalendarBuilderWithTimezone>) builder -> {
            if (builder.getTimeZone() == null || builder.getTimeZone().isBlank()) {
                builder.withTimeZone("UTC");
            }
        });
        when(calendarService.createTrigger(any(CalendarTrigger.class))).thenReturn(calendarTriggerList.get(0));
        when(daoService.getClasse(anyString())).thenReturn(classe);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.create(wsTriggerData);

        //assert:
        verify(calendarService).fixTimeZone();
        verify(calendarService).createTrigger(any(CalendarTrigger.class));
        verify(daoService, times(2)).getClasse(anyString());
        verify(lookupService, times(2)).getLookup(any(Enum.class));
        checkResult(A_KNOWN_NAME1, resultObject, "code");
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        when(calendarService.getTriggerById(A_KNOWN_LONG_ID1)).thenReturn(calendarTriggerList.get(0));
        when(calendarService.updateTrigger(any(CalendarTrigger.class))).thenReturn(calendarTriggerList.get(0));
        when(daoService.getClasse(anyString())).thenReturn(classe);
        when(lookupService.getLookup(any(Enum.class))).thenReturn(lookupValue);

        //act:
        Object resultObject = instance.update(A_KNOWN_LONG_ID1, wsTriggerData);

        //assert:
        verify(calendarService).getTriggerById(A_KNOWN_LONG_ID1);
        verify(calendarService).updateTrigger(any(CalendarTrigger.class));
        verify(daoService, times(2)).getClasse(anyString());
        verify(lookupService, times(2)).getLookup(any(Enum.class));
        checkResult(A_KNOWN_NAME1, resultObject, "code");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_LONG_ID1);

        //assert:
        verify(calendarService).deleteTrigger(A_KNOWN_LONG_ID1);
        checkSuccess(resultObject);
    }


}
