/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_CATEGORY_LOOKUP_TYPE;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_PRIORITY_LOOKUP_TYPE;
import org.cmdbuild.calendar.beans.CalendarEventType;
import org.cmdbuild.calendar.beans.CalendarTrigger.TriggerScope;
import org.cmdbuild.calendar.beans.CalendarTriggerImpl;
import org.cmdbuild.calendar.beans.EventEditMode;
import org.cmdbuild.calendar.beans.EventFrequency;
import org.cmdbuild.calendar.beans.PostCardDeleteAction;
import org.cmdbuild.calendar.beans.SequenceEndType;
import org.cmdbuild.calendar.beans.SequenceParamsEditMode;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.participant.ParticipantUtils;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.utils.date.Interval;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenListOfMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

public class WsTriggerData {

    private final String category;
    private final String priority;
    private final String conditionScript;
    private final String content;
    private final String description;
    private final String code;
    private final String timeZone;
    private final String ownerClass;
    private final String ownerAttr;
    private final Integer eventCount;
    private final Integer frequencyMultiplier;
    private final Integer maxActiveEvents;
    private final Interval delay;
    private final EventEditMode eventEditMode;
    private final LocalTime eventTime;
    private final LocalDate lastEvent;
    private final EventFrequency frequency;
    private final List<String> participants;
    private final PostCardDeleteAction onCardDeleteAction;
    private final SequenceParamsEditMode sequenceParamsEditMode;
    private final Boolean showGeneratedEventsPreview;
    private final Boolean isActive;
    private final CalendarEventType eventType;
    private final SequenceEndType endType;
    private final TriggerScope triggerScope;
    @JsonAnySetter
    private final Map<String, Object> values = map();

    public WsTriggerData(@JsonProperty("category") String category,
            @JsonProperty("priority") String priority,
            @JsonProperty("conditionScript") String conditionScript,
            @JsonProperty("content") String content,
            @JsonProperty("code") String code,
            @JsonProperty("description") String description,
            @JsonProperty("timeZone") String timeZone,
            @JsonProperty("eventCount") Integer eventCount,
            @JsonProperty("frequencyMultiplier") Integer frequencyMultiplier,
            @JsonProperty("maxActiveEvents") Integer maxActiveEvents,
            @JsonProperty("delay") String delay,
            @JsonProperty("eventEditMode") String eventEditMode,
            @JsonProperty("eventTime") String eventTime,
            @JsonProperty("frequency") String frequency,
            @JsonProperty("participants") List<String> participants,
            @JsonProperty("_participant_user_id") Long participantUser,
            @JsonProperty("_participant_group_id") Long participantGroup,
            @JsonProperty("onCardDeleteAction") String onCardDeleteAction,
            @JsonProperty("sequenceParamsEditMode") String sequenceParamsEditMode,
            @JsonProperty("showGeneratedEventsPreview") Boolean showGeneratedEventsPreview,
            @JsonProperty("active") Boolean isActive,
            @JsonProperty("eventType") String eventType,
            @JsonProperty("ownerClass") String ownerClass,
            @JsonProperty("ownerAttr") String ownerAttr,
            @JsonProperty("lastEvent") String lastEvent,
            @JsonProperty("endType") String endType,
            @JsonProperty("scope") String triggerScope) {
        this.triggerScope = parseEnumOrNull(triggerScope, TriggerScope.class);
        this.category = category;
        this.code = code;
        this.priority = priority;
        this.conditionScript = conditionScript;
        this.content = content;
        this.description = description;
        this.timeZone = timeZone;
        this.eventCount = eventCount;
        this.frequencyMultiplier = frequencyMultiplier;
        this.maxActiveEvents = maxActiveEvents;
        this.delay = CmDateUtils.toInterval(delay);
        this.eventEditMode = CmConvertUtils.parseEnumOrNull(eventEditMode, EventEditMode.class);
        this.eventTime = CmDateUtils.toTime(eventTime);
        this.frequency = CmConvertUtils.parseEnumOrNull(frequency, EventFrequency.class);
        this.participants = list(firstNotNull(participants, Collections.emptyList())).accept((l) -> {
            if (l.isEmpty()) {
                if (isNotNullAndGtZero(participantUser)) {
                    ParticipantUtils.buildParticipants().addUsers(participantUser).toParticipants().forEach(l::add);
                }
                if (isNotNullAndGtZero(participantGroup)) {
                    ParticipantUtils.buildParticipants().addRoles(participantGroup).toParticipants().forEach(l::add);
                }
            }
        }).immutable();
        this.onCardDeleteAction = CmConvertUtils.parseEnumOrNull(onCardDeleteAction, PostCardDeleteAction.class);
        this.showGeneratedEventsPreview = showGeneratedEventsPreview;
        this.sequenceParamsEditMode = CmConvertUtils.parseEnumOrNull(sequenceParamsEditMode, SequenceParamsEditMode.class);
        this.isActive = isActive;
        this.eventType = CmConvertUtils.parseEnumOrNull(eventType, CalendarEventType.class);
        this.ownerClass = ownerClass;
        this.ownerAttr = ownerAttr;
        this.endType = CmConvertUtils.parseEnumOrNull(endType, SequenceEndType.class);
        this.lastEvent = CmDateUtils.toDate(lastEvent);
    } //TODO improve this

    public CalendarTriggerImpl.CalendarTriggerImplBuilder toTrigger() {
        List<WsNotificationData> notifications = list(unflattenListOfMaps(values, "notifications")).map(m -> fromJson(toJson(m), WsNotificationData.class));
        return CalendarTriggerImpl.builder()
                .withActive(isActive)
                .withCode(code)
                .withCategory(LookupValueImpl.fromCode(CALENDAR_CATEGORY_LOOKUP_TYPE, category)).withConfig((c) -> c
                .withEndType(endType)
                .withConditionScript(conditionScript)
                .withEventCount(eventCount)
                .withEventEditMode(eventEditMode)
                .withFrequency(frequency)
                .withFrequencyMultiplier(frequencyMultiplier)
                .withMaxActiveEvents(maxActiveEvents)
                .withOnCardDeleteAction(onCardDeleteAction)
                .withSequenceParamsEditMode(sequenceParamsEditMode)
                .withShowGeneratedEventsPreview(showGeneratedEventsPreview))
                .withContent(content)
                .withDelay(delay)
                .withDescription(description)
                .withEventTime(eventTime)
                .withNotifications(CmCollectionUtils.list(notifications).map((n) -> n.toTemplate().build()))
                .withOwnerAttr(ownerAttr)
                .withOwnerClass(ownerClass)
                .withParticipants(participants)
                .withPriority(LookupValueImpl.fromCode(CALENDAR_PRIORITY_LOOKUP_TYPE, priority))
                .withTimeZone(timeZone)
                .withLastEvent(lastEvent)
                .withType(eventType)
                .withScope(triggerScope);
    }

}
