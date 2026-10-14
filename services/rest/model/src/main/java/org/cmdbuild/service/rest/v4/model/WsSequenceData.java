/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.cmdbuild.calendar.CalendarService;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_CATEGORY_LOOKUP_TYPE;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_PRIORITY_LOOKUP_TYPE;
import org.cmdbuild.calendar.beans.CalendarEventType;
import org.cmdbuild.calendar.beans.CalendarSequenceImpl;
import org.cmdbuild.calendar.beans.EventEditMode;
import org.cmdbuild.calendar.beans.EventFrequency;
import org.cmdbuild.calendar.beans.PostCardDeleteAction;
import org.cmdbuild.calendar.beans.SequenceEndType;
import org.cmdbuild.calendar.beans.SequenceParamsEditMode;
import org.cmdbuild.dao.beans.LookupValueImpl;
import static org.cmdbuild.participant.ParticipantUtils.buildParticipants;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.date.CmDateUtils.toTime;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenListOfMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.utils.lang.CmNullableUtils;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

public class WsSequenceData {

    private final Long trigger;
    private final Long card;
    private final String category;
    private final String priority;
    private final String content;
    private final String description;
    private final String timeZone;
    private final Integer eventCount;
    private final Integer frequencyMultiplier;
    private final Integer maxActiveEvents;
    private final EventEditMode eventEditMode;
    private final LocalTime eventTime;
    private final EventFrequency frequency;
    private final List<String> participants;
    private final PostCardDeleteAction onCardDeleteAction;
    private final SequenceParamsEditMode sequenceParamsEditMode;
    private final Boolean showGeneratedEventsPreview;
    private final CalendarEventType eventType;
    private final List<WsEventData> events;
    private final LocalDate firstEvent;
    private final LocalDate lastEvent;
    private final SequenceEndType endType;
    @JsonAnySetter
    private final Map<String, Object> values = map();

    public WsSequenceData(
            @JsonProperty(value = "category") String category,
            @JsonProperty(value = "priority") String priority,
            @JsonProperty(value = "card") Long card,
            @JsonProperty(value = "content") String content,
            @JsonProperty(value = "description") String description,
            @JsonProperty(value = "timeZone") String timeZone,
            @JsonProperty(value = "title") String title,
            @JsonProperty(value = "eventCount") Integer eventCount,
            @JsonProperty(value = "frequencyMultiplier") Integer frequencyMultiplier,
            @JsonProperty(value = "maxActiveEvents") Integer maxActiveEvents,
            @JsonProperty(value = "eventEditMode") String eventEditMode,
            @JsonProperty(value = "eventTime") String eventTime,
            @JsonProperty(value = "frequency") String frequency,
            @JsonProperty(value = "participants") List<String> participants,
            @JsonProperty(value = "_participant_user_id") Long participantUser,
            @JsonProperty(value = "_participant_group_id") Long participantGroup,
            @JsonProperty(value = "onCardDeleteAction") String onCardDeleteAction,
            @JsonProperty(value = "sequenceParamsEditMode") String sequenceParamsEditMode,
            @JsonProperty(value = "showGeneratedEventsPreview") Boolean showGeneratedEventsPreview,
            @JsonProperty(value = "eventType") String eventType,
            @JsonProperty(value = "firstEvent") String firstEvent,
            @JsonProperty(value = "lastEvent") String lastEvent,
            @JsonProperty(value = "trigger") Long trigger,
            @JsonProperty(value = "endType") String endType,
            @JsonProperty(value = "events") List<WsEventData> events) {
        this.category = category;
        this.priority = priority;
        this.card = card;
        this.trigger = trigger;
        this.content = content;
        this.description = description;
        this.timeZone = timeZone;
        this.eventCount = eventCount;
        this.frequencyMultiplier = frequencyMultiplier;
        this.maxActiveEvents = maxActiveEvents;
        this.eventEditMode = parseEnumOrNull(eventEditMode, EventEditMode.class);
        this.eventTime = toTime(eventTime);
        this.frequency = parseEnumOrNull(frequency, EventFrequency.class);
        this.participants = list(CmNullableUtils.firstNotNull(participants, Collections.emptyList())).accept((l) -> {
            if (isNotNullAndGtZero(participantUser)) {
                buildParticipants().addUsers(participantUser).toParticipants().forEach(l::add);
            }
            if (isNotNullAndGtZero(participantGroup)) {
                buildParticipants().addRoles(participantGroup).toParticipants().forEach(l::add);
            }
        }).immutable();
        this.onCardDeleteAction = parseEnumOrNull(onCardDeleteAction, PostCardDeleteAction.class);
        this.showGeneratedEventsPreview = showGeneratedEventsPreview;
        this.sequenceParamsEditMode = parseEnumOrNull(sequenceParamsEditMode, SequenceParamsEditMode.class);
        this.eventType = parseEnumOrNull(eventType, CalendarEventType.class);
        this.events = ImmutableList.copyOf(CmNullableUtils.firstNotNull(events, Collections.emptyList()));
        this.firstEvent = toDate(firstEvent);
        this.lastEvent = toDate(lastEvent);
        this.endType = parseEnum(endType, SequenceEndType.class);
    } //TODO improve this

    public CalendarSequenceImpl.CalendarSequenceImplBuilder toSequence(CalendarService service) {
        List<WsNotificationData> notifications = list(unflattenListOfMaps(values, "notifications")).map(m -> fromJson(toJson(m), WsNotificationData.class));
        return CalendarSequenceImpl.builder()
                .withCategory(LookupValueImpl.fromCode(CALENDAR_CATEGORY_LOOKUP_TYPE, category))
                .withConfig((c) -> c.withEndType(endType).withEventCount(eventCount).withEventEditMode(eventEditMode).withFrequency(frequency).withFrequencyMultiplier(frequencyMultiplier).withMaxActiveEvents(maxActiveEvents).withOnCardDeleteAction(onCardDeleteAction).withSequenceParamsEditMode(sequenceParamsEditMode).withShowGeneratedEventsPreview(showGeneratedEventsPreview))
                .withContent(content)
                .withDescription(description)
                .withEventTime(eventTime)
                .withCard(card)
                .withNotifications(CmCollectionUtils.list(notifications).map((n) -> n.toTemplate().build()))
                .withParticipants(participants)
                .withPriority(LookupValueImpl.fromCode(CALENDAR_PRIORITY_LOOKUP_TYPE, priority))
                .withTimeZone(timeZone)
                .withType(eventType)
                .withFirstEvent(firstEvent)
                .withLastEvent(lastEvent)
                .withTrigger(trigger)
                .withEvents(events.stream().map((e) -> e.buildEvent().accept((Consumer) service.fixTimeZone()).build()).collect(Collectors.toList()));
    }

}
