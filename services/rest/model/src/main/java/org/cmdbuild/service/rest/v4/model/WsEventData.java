/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_CATEGORY_LOOKUP_TYPE;
import static org.cmdbuild.calendar.beans.CalendarEvent.CALENDAR_PRIORITY_LOOKUP_TYPE;
import org.cmdbuild.calendar.beans.CalendarEventImpl;
import org.cmdbuild.calendar.beans.CalendarEventSource;
import org.cmdbuild.calendar.beans.CalendarEventStatus;
import org.cmdbuild.calendar.beans.CalendarEventType;
import org.cmdbuild.calendar.beans.EventEditMode;
import org.cmdbuild.calendar.beans.PostCardDeleteAction;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.participant.ParticipantUtils;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenListOfMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

public class WsEventData {

    private final Long card;
    private final Long sequence;
    private final String category;
    private final String priority;
    private final String content;
    private final String description;
    private final String timeZone;
    private final String owner;
    private final String notes;
    private final EventEditMode eventEditMode;
    private final List<String> participants;
    private final PostCardDeleteAction onCardDeleteAction;
    private final CalendarEventType type;
    private final ZonedDateTime begin;
    private final ZonedDateTime end, completion;
    private final CalendarEventStatus status;
    private final CalendarEventSource source;
    @JsonAnySetter
    private final Map<String, Object> values = map();

    public WsEventData(
            @JsonProperty("category") String category,
            @JsonProperty("priority") String priority,
            @JsonProperty("card") Long card,
            @JsonProperty("sequence") Long sequence,
            @JsonProperty("content") String content,
            @JsonProperty("description") String description,
            @JsonProperty("timeZone") String timeZone,
            @JsonProperty("eventEditMode") String eventEditMode,
            @JsonProperty("participants") List<String> participants,
            @JsonProperty("_participant_user_id") Long participantUser,
            @JsonProperty("_participant_group_id") Long participantGroup,
            @JsonProperty("onCardDeleteAction") String onCardDeleteAction,
            @JsonProperty("type") String type,
            @JsonProperty("begin") String begin,
            @JsonProperty("end") String end,
            @JsonProperty("completion") String completion,
            @JsonProperty("owner") String owner,
            @JsonProperty("status") String status,
            @JsonProperty("source") String source,
            @JsonProperty("notes") String notes) {
        this.category = category;
        this.priority = priority;
        this.card = card;
        this.notes = notes;
        this.content = content;
        this.description = description;
        this.timeZone = timeZone;
        this.owner = owner;
        this.sequence = sequence;
        this.eventEditMode = CmConvertUtils.parseEnumOrNull(eventEditMode, EventEditMode.class);
        this.participants = list(firstNotNull(participants, emptyList())).accept(l -> {
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
        this.type = CmConvertUtils.parseEnumOrNull(type, CalendarEventType.class);
        this.begin = CmDateUtils.toDateTime(begin);
        this.end = CmDateUtils.toDateTime(end);
        this.completion = CmDateUtils.toDateTime(completion);
        this.status = CmConvertUtils.parseEnum(status, CalendarEventStatus.class);
        this.source = CmConvertUtils.parseEnum(source, CalendarEventSource.class);
    }

    public CalendarEventImpl.CalendarEventImplBuilder buildEvent() {
        List<WsNotificationData> notifications = list(unflattenListOfMaps(values, "notifications")).map(m -> fromJson(toJson(m), WsNotificationData.class));
        return CalendarEventImpl.builder()
                .withBegin(begin)
                .withCard(card)
                .withCategory(LookupValueImpl.fromCode(CALENDAR_CATEGORY_LOOKUP_TYPE, category))
                .withConfig((c) -> c.withEventEditMode(eventEditMode).withOnCardDeleteAction(onCardDeleteAction))
                .withContent(content)
                .withDescription(description)
                .withEnd(end)
                .withCompleted(completion)
                .withNotifications(list(notifications).map(n -> n.toTemplate().build()))
                .withOwner(owner)
                .withParticipants(participants)
                .withPriority(LookupValueImpl.fromCode(CALENDAR_PRIORITY_LOOKUP_TYPE, priority))
                .withSequence(sequence)
                .withSource(source)
                .withStatus(status)
                .withTimeZone(timeZone)
                .withType(type)
                .withNotes(notes);
    }

}
