/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.test;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static java.util.Collections.emptySet;
import java.util.List;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.calendar.beans.CalendarEvent;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_CATEGORY_LOOKUP_TYPE;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_PRIORITY_LOOKUP_TYPE;
import static org.cmdbuild.calendar.beans.CalendarEventType.CT_DATE;
import static org.cmdbuild.calendar.beans.CalendarEventType.CT_INSTANT;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import org.cmdbuild.calendar.beans.CalendarTriggerImpl;
import static org.cmdbuild.calendar.beans.EventEditMode.EEM_READ;
import static org.cmdbuild.calendar.beans.EventEditMode.EEM_WRITE;
import static org.cmdbuild.calendar.utils.CalendarUtils.sequenceToEvents;
import static org.cmdbuild.calendar.utils.CalendarUtils.triggerToSequence;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import org.junit.Test;
import static org.cmdbuild.calendar.beans.PostCardDeleteAction.PCDA_DELETE;
import static org.cmdbuild.calendar.beans.PostCardDeleteAction.PCDA_CLEAR;
import static org.cmdbuild.calendar.beans.SequenceParamsEditMode.SPEM_READ;
import static org.cmdbuild.calendar.beans.SequenceParamsEditMode.SPEM_WRITE;
import static org.cmdbuild.calendar.beans.EventFrequency.EF_DAILY;
import static org.cmdbuild.calendar.beans.EventFrequency.EF_MONTHLY;
import static org.cmdbuild.calendar.beans.EventFrequency.EF_YEARLY;
import static org.cmdbuild.calendar.beans.SequenceEndType.SET_NEVER;
import static org.cmdbuild.calendar.beans.SequenceEndType.SET_NUMBER;
import static org.cmdbuild.calendar.utils.CalendarUtils.getDate;
import static org.cmdbuild.dao.beans.LookupValueImpl.fromCode;
import org.cmdbuild.email.beans.EmailTemplateInlineData;
import org.cmdbuild.email.beans.EmailTemplateInlineDataImpl;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDate;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class CalendarTest {

    @Test
    public void testZoneId() {
        assertEquals("Europe/Rome", ZoneId.of("Europe/Rome").getId());
    }

    @Test
    public void testTimezone1() {
        ZoneId zone = ZoneId.of("Etc/GMT+12");
        ZonedDateTime time = toDateTime("2020-01-30T12:00:00Z");
        assertThat(toIsoDateTime(time), startsWith("2020-01-30T12:00:00"));
        assertEquals("2020-01-30", toIsoDate(getDate(time, zone)));
        time = time.withZoneSameInstant(zone);
        assertThat(toIsoDateTime(time), startsWith("2020-01-30T00:00:00"));
        assertEquals("2020-01-30", toIsoDate(getDate(time, zone)));
    }

    @Test
    public void testTriggerToSequenceToEvents1() {
        CalendarTrigger trigger = CalendarTriggerImpl.builder()
                .withId(123l)
                .withCode("testCode")
                .withCategory(fromCode(CALENDAR_CATEGORY_LOOKUP_TYPE, "default"))
                .withPriority(fromCode(CALENDAR_PRIORITY_LOOKUP_TYPE, "default"))
                .withContent("My Content")
                .withDescription("My Description")
                .withEventTime(LocalTime.of(14, 30))
                .withNotifications(list(EmailTemplateInlineDataImpl.build("not1"), EmailTemplateInlineDataImpl.build("not2")))
                .withParticipants("user.1", "group.2")
                .withOwnerAttr("MyAttr")
                .withOwnerClass("MyClass")
                .withTimeZone("Asia/Tokyo")
                .withType(CT_INSTANT)
                .withConfig(c -> c
                .withEventCount(2)
                .withEventEditMode(EEM_READ)
                .withFrequency(EF_MONTHLY)
                .withFrequencyMultiplier(5)
                .withOnCardDeleteAction(PCDA_DELETE)
                .withSequenceParamsEditMode(SPEM_READ)
                .withShowGeneratedEventsPreview(true)
                .withEndType(SET_NUMBER)
                .build()).build();

        CalendarSequence sequence = triggerToSequence(trigger, 4l, toDate("1974-05-22"));

        assertEquals("default", sequence.getCategory().getCode());
        assertEquals("default", sequence.getPriority().getCode());
        assertEquals("My Content", sequence.getContent());
        assertEquals("My Description", sequence.getDescription());
        assertEquals("14:30:00", toIsoTime(sequence.getEventTime()));
        assertEquals(list("not1", "not2"), list(sequence.getNotifications()).map(EmailTemplateInlineData::getTemplate));
        assertEquals(set("user.1", "group.2"), set(sequence.getParticipants()));
        assertEquals("Asia/Tokyo", sequence.getTimeZone());
        assertEquals(CT_INSTANT, sequence.getType());
        assertEquals(EEM_READ, sequence.getEventEditMode());
        assertEquals(EF_MONTHLY, sequence.getFrequency());
        assertEquals(5, sequence.getFrequencyMultiplier());
        assertEquals(PCDA_DELETE, sequence.getOnCardDeleteAction());
        assertEquals(SPEM_READ, sequence.getSequenceParamsEditMode());
        assertEquals(true, sequence.getShowGeneratedEventsPreview());
        assertEquals(123l, (long) sequence.getTrigger());
        assertEquals(4l, (long) sequence.getCard());

        List<CalendarEvent> events = sequenceToEvents(sequence);

        events.forEach(event -> {
            assertEquals("default", event.getCategory().getCode());
            assertEquals("default", event.getPriority().getCode());
            assertEquals("My Content", event.getContent());
            assertEquals("My Description", event.getDescription());
            assertEquals(list("not1", "not2"), list(sequence.getNotifications()).map(EmailTemplateInlineData::getTemplate));
            assertEquals(set("user.1", "group.2"), set(event.getParticipants()));
            assertEquals("Asia/Tokyo", event.getTimeZone());
            assertEquals(CT_INSTANT, event.getType());
            assertEquals(EEM_READ, event.getEventEditMode());
        });

        assertEquals("1974-05-22T05:30:00Z,1974-10-22T05:30:00Z", events.stream().map(e -> toIsoDateTimeUtc(e.getBegin())).collect(joining(",")));
    }

    @Test
    public void testTriggerToSequenceToEvents2() {
        CalendarTrigger trigger = CalendarTriggerImpl.builder()
                .withCategory(fromCode(CALENDAR_CATEGORY_LOOKUP_TYPE, "default"))
                .withPriority(fromCode(CALENDAR_PRIORITY_LOOKUP_TYPE, "default"))
                .withContent("My Content")
                .withDescription("My Description")
                .withOwnerAttr("MyAttr")
                .withOwnerClass("MyClass")
                .withCode("testCode2")
                .withTimeZone("America/New_York")
                .withType(CT_DATE)
                .withConfig(c -> c
                .withEventEditMode(EEM_WRITE)
                .withFrequency(EF_DAILY)
                .withFrequencyMultiplier(30)
                .withOnCardDeleteAction(PCDA_CLEAR)
                .withSequenceParamsEditMode(SPEM_WRITE)
                .withShowGeneratedEventsPreview(false)
                .withMaxActiveEvents(4)
                .withEndType(SET_NEVER)
                .build()).build();

        CalendarSequence sequence = triggerToSequence(trigger, 4l, toDate("2012-05-22"));

        assertEquals("default", sequence.getCategory().getCode());
        assertEquals("default", sequence.getPriority().getCode());
        assertEquals("My Content", sequence.getContent());
        assertEquals("My Description", sequence.getDescription());
        assertEquals("00:00:00", toIsoTime(sequence.getEventTime()));
        assertEquals(emptySet(), set(sequence.getNotifications()));
        assertEquals(emptySet(), set(sequence.getParticipants()));
        assertEquals("America/New_York", sequence.getTimeZone());
        assertEquals(CT_DATE, sequence.getType());
        assertEquals(EEM_WRITE, sequence.getEventEditMode());
        assertEquals(EF_DAILY, sequence.getFrequency());
        assertEquals(30, sequence.getFrequencyMultiplier());
        assertEquals(PCDA_CLEAR, sequence.getOnCardDeleteAction());
        assertEquals(SPEM_WRITE, sequence.getSequenceParamsEditMode());
        assertEquals(false, sequence.getShowGeneratedEventsPreview());

        List<CalendarEvent> events = sequenceToEvents(sequence);

        events.forEach(event -> {
            assertEquals("default", event.getCategory().getCode());
            assertEquals("default", event.getPriority().getCode());
            assertEquals("My Content", event.getContent());
            assertEquals("My Description", event.getDescription());
            assertEquals(emptySet(), set(event.getNotifications()));
            assertEquals(emptySet(), set(event.getParticipants()));
            assertEquals("America/New_York", event.getTimeZone());
            assertEquals(CT_DATE, event.getType());
            assertEquals(EEM_WRITE, event.getEventEditMode());
        });

        assertEquals("2012-05-22T04:00:00Z,2012-06-21T04:00:00Z,2012-07-21T04:00:00Z,2012-08-20T04:00:00Z", events.stream().map(e -> toIsoDateTimeUtc(e.getBegin())).collect(joining(",")));

        events = sequenceToEvents(sequence, list(events.get(3)));

        events.forEach(event -> {
            assertEquals("default", event.getCategory().getCode());
            assertEquals("default", event.getPriority().getCode());
            assertEquals("My Content", event.getContent());
            assertEquals("My Description", event.getDescription());
            assertEquals(emptySet(), set(event.getNotifications()));
            assertEquals(emptySet(), set(event.getParticipants()));
            assertEquals("America/New_York", event.getTimeZone());
            assertEquals(CT_DATE, event.getType());
            assertEquals(EEM_WRITE, event.getEventEditMode());
        });

        assertEquals("2012-09-19T04:00:00Z,2012-10-19T04:00:00Z,2012-11-18T05:00:00Z", events.stream().map(e -> toIsoDateTimeUtc(e.getBegin())).collect(joining(",")));
    }

    @Test
    public void testTriggerToSequenceToEvents3() {
        CalendarTrigger trigger = CalendarTriggerImpl.builder()
                .withCategory(fromCode(CALENDAR_CATEGORY_LOOKUP_TYPE, "default"))
                .withPriority(fromCode(CALENDAR_PRIORITY_LOOKUP_TYPE, "default"))
                .withOwnerAttr("MyAttr")
                .withCode("testCode3")
                .withOwnerClass("MyClass")
                .withTimeZone("Europe/Rome")
                .withConfig(c -> c
                .withFrequency(EF_YEARLY)
                .withEventCount(6)
                .withEndType(SET_NUMBER)
                .build()).build();

        CalendarSequence sequence = triggerToSequence(trigger, 4l, toDate("2000-05-22"));

        assertEquals("default", sequence.getCategory().getCode());
        assertEquals("default", sequence.getPriority().getCode());
        assertEquals("", sequence.getContent());
        assertEquals("", sequence.getDescription());
        assertEquals("00:00:00", toIsoTime(sequence.getEventTime()));
        assertEquals(emptySet(), set(sequence.getNotifications()));
        assertEquals(emptySet(), set(sequence.getParticipants()));
        assertEquals("Europe/Rome", sequence.getTimeZone());
        assertEquals(CT_INSTANT, sequence.getType());
        assertEquals(EEM_WRITE, sequence.getEventEditMode());
        assertEquals(EF_YEARLY, sequence.getFrequency());
        assertEquals(1, sequence.getFrequencyMultiplier());
        assertEquals(PCDA_CLEAR, sequence.getOnCardDeleteAction());
        assertEquals(SPEM_WRITE, sequence.getSequenceParamsEditMode());
        assertEquals(false, sequence.getShowGeneratedEventsPreview());
        assertEquals(SET_NUMBER, sequence.getEndType());

        List<CalendarEvent> events = sequenceToEvents(sequence);

        events.forEach(event -> {
            assertEquals("default", event.getCategory().getCode());
            assertEquals("default", event.getPriority().getCode());
            assertEquals("", event.getContent());
            assertEquals("", event.getDescription());
            assertEquals(emptySet(), set(event.getNotifications()));
            assertEquals(emptySet(), set(event.getParticipants()));
            assertEquals("Europe/Rome", event.getTimeZone());
            assertEquals(CT_INSTANT, event.getType());
            assertEquals(EEM_WRITE, event.getEventEditMode());
        });

        assertEquals("2000-05-21T22:00:00Z,2001-05-21T22:00:00Z,2002-05-21T22:00:00Z,2003-05-21T22:00:00Z,2004-05-21T22:00:00Z,2005-05-21T22:00:00Z", events.stream().map(e -> toIsoDateTimeUtc(e.getBegin())).collect(joining(",")));
    }
}
