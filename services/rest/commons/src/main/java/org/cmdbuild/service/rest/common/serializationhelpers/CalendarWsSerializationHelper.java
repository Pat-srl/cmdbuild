/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import com.google.common.collect.ImmutableMap;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.role.RoleInfo;
import org.cmdbuild.auth.user.LoginUserInfo;
import org.cmdbuild.calendar.CalendarService;
import org.cmdbuild.calendar.CalendarTriggerInfo;
import org.cmdbuild.calendar.beans.CalendarEvent;
import org.cmdbuild.calendar.beans.CalendarSequence;
import org.cmdbuild.calendar.beans.CalendarTrigger;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.email.beans.EmailTemplateInlineData;
import org.cmdbuild.email.template.EmailTemplateService;
import org.cmdbuild.participant.ParticipantService;
import org.cmdbuild.report.ReportConfigImpl;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.calendar.beans.CalendarEvent.*;
import static org.cmdbuild.participant.ParticipantUtils.parseParticipants;
import static org.cmdbuild.utils.date.CmDateUtils.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import static org.cmdbuild.utils.lang.CmInlineUtils.flattenMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Component
public class CalendarWsSerializationHelper {

    public static final Map<String, String> CAL_ATTR_MAPPING = ImmutableMap.copyOf(map(
            "begin", EVENT_ATTR_BEGIN,
            "date", EVENT_ATTR_DATE,
            "end", EVENT_ATTR_END,
            "card", EVENT_ATTR_CARD,
            "content", EVENT_ATTR_CONTENT,
            "description", EVENT_ATTR_DESCRIPTION,
            "status", EVENT_ATTR_STATUS,
            "category", EVENT_ATTR_CATEGORY,
            "priority", EVENT_ATTR_PRIORITY,
            "type", EVENT_ATTR_TYPE,
            "completion", EVENT_ATTR_COMPLETION
    ));

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LookupSerializationHelper lookupHelper;
    private final ObjectTranslationService translationService;
    private final DaoService dao;
    private final ParticipantService participantService;

    private final EmailTemplateService templateService;
    private final CalendarService calendarService;

    public CalendarWsSerializationHelper(LookupSerializationHelper lookupHelper, ObjectTranslationService translationService, DaoService dao, ParticipantService participantService, EmailTemplateService templateService, CalendarService calendarService) {
        this.lookupHelper = checkNotNull(lookupHelper);
        this.translationService = checkNotNull(translationService);
        this.dao = checkNotNull(dao);
        this.participantService = checkNotNull(participantService);
        this.templateService = checkNotNull(templateService);
        this.calendarService = checkNotNull(calendarService);
    }

    public Consumer<FluentMap<String, Object>> participantsSerializer(Collection<String> participants) {
        return (m) -> {
            m.put("participants", parseParticipants(participants).stream().map(p -> {
                return switch (p.getParticipantType()) {
                    case PT_USER -> "user." + p.getId();
                    case PT_ROLE -> "group." + p.getId();
                    default ->
                            throw new UnsupportedOperationException("unsupported participant type = " + p.getParticipantType());
                };
            }).collect(toList()));

            List<LoginUserInfo> users = participantService.getUsers(participants);
            m.put("_participant_users", users.stream().map(user -> map(
                    "_id", user.getId(),
                    "username", user.getUsername()
            )).collect(toList()));
            if (users.size() == 1) {
                m.put("_participant_user_id", getOnlyElement(users).getId());
                m.put("_participant_user_username", getOnlyElement(users).getUsername());
            }

            List<RoleInfo> roles = participantService.getRoles(participants);
            m.put("_participant_groups", roles.stream().map(role -> map(
                    "_id", role.getId(),
                    "name", role.getName()
            )).collect(toList()));
            if (roles.size() == 1) {
                m.put("_participant_group_id", getOnlyElement(roles).getId());
                m.put("_participant_group_name", getOnlyElement(roles).getName());
            }
        };
    }

    private Consumer<FluentMap<String, Object>> notificationSerializer(@Nullable CalendarEvent event, Collection<EmailTemplateInlineData> notification) {
        return (m) -> m.putAll(flattenMaps(map("notifications", list(notification).map(n -> serializeNotification(event, n)))));
    }

    private FluentMap<String, Object> serializeNotification(@Nullable CalendarEvent event, EmailTemplateInlineData notification) {
        return (FluentMap) map(
                "_id", notification.getId(),
                "template", notification.getTemplate(),
                "content", notification.getContent(),
                "delay", notification.getDelay(),
                "reports", list(notification.getReportList()).map(r -> ReportConfigImpl.toConfig(r))
        ).accept(m -> {
            if (event != null) {
                try {
                    m.put("_content_preview", calendarService.buildNotificationEmail(event, templateService.getTemplate(notification)).getContentPlaintext());
                } catch (Exception ex) {
                    logger.info(marker(), "error generating content preview for notification =< {} > template =< {} >", notification.getId(), notification.getTemplate(), ex);
                }
            }

        });
    }

    public static FluentMap<String, Object> serializeBasicEvent(CalendarEvent event) {
        return map(
                "_id", event.getId(),
                "owner", event.getOwner(),
                "type", serializeEnum(event.getType()),
                "source", serializeEnum(event.getSource()),
                "begin", toIsoDateTime(event.getBegin()),
                "date", toIsoDate(event.getDate()),
                "end", toIsoDateTime(event.getEnd()),
                "timeZone", event.getTimeZone(),
                "card", event.getCard()
        );
    }

    public FluentMap<String, Object> serializeEvent(CalendarEvent event) {
        return serializeBasicEvent(event)
                .with(
                        "notes", event.getNotes(),
                        "content", event.getContent(),
                        "description", event.getDescription(),
                        "eventEditMode", serializeEnum(event.getEventEditMode()),
                        "onCardDeleteAction", serializeEnum(event.getOnCardDeleteAction()),
                        "sequence", event.getSequence(),
                        "status", serializeEnum(event.getStatus()),
                        "completion", toIsoDateTime(event.getCompleted()),
                        "_can_write", event.isWritable())
                .accept(m -> {
                    lookupHelper.serializeLookupValueDetail("category", event.getCategory(), m::put);
                    lookupHelper.serializeLookupValueDetail("priority", event.getPriority(), m::put);
                });
    }

    public FluentMap<String, Object> serializeDetailedEvent(CalendarEvent event) {
        return serializeBasicEvent(event).with(
                        "notes", event.getNotes(),
                        "content", event.getContent(),
                        "description", event.getDescription(),
                        "eventEditMode", serializeEnum(event.getEventEditMode()),
                        "onCardDeleteAction", serializeEnum(event.getOnCardDeleteAction()),
                        "sequence", event.getSequence(),
                        "status", serializeEnum(event.getStatus()),
                        "completion", toIsoDateTime(event.getCompleted()),
                        "_can_write", event.isWritable()//TODO improve this
                ).accept(notificationSerializer(event, event.getNotifications()))
                .accept(participantsSerializer(event.getParticipants())).accept(m -> {
                    lookupHelper.serializeLookupValueDetail("category", event.getCategory(), m::put);
                    lookupHelper.serializeLookupValueDetail("priority", event.getPriority(), m::put);
                    if (event.hasCard()) {
                        m.put("_card_type", dao.getType(event.getCard()).getName());
                    }
                    if (event.hasCard() && dao.getType(event.getCard()).isDmsModel() && dao.getCard(event.getCard()).get("Card") != null) {
                        m.put("_id_attachment", dao.getCard(event.getCard()).get("DocumentId"));
                        m.put("_origin_type", dao.getCard(dao.getCard(event.getCard()).getLong("Card")).getClassName());
                        m.put("_origin_card", dao.getCard(event.getCard()).get("Card"));
                    }
                });
    }

    public static CmMapUtils.FluentMap<String, Object> serializeBasicSequence(CalendarSequence sequence) {
        return map(
                "_id", sequence.getId(),
                "source", serializeEnum(sequence.getSource()),
                "card", sequence.getCard(),
                "owner", sequence.getOwner(),
                "trigger", sequence.getTrigger()
        );
    }

    public Object serializeDetailedSequence(CalendarSequence sequence) {
        return serializeDetailedSequence(sequence, sequence.getEvents());
    }

    public Object serializeDetailedSequence(CalendarSequence sequence, @Nullable List<CalendarEvent> events) {
        return serializeBasicSequence(sequence).with(
                        "content", sequence.getContent(),
                        "description", sequence.getDescription(),
                        "eventCount", sequence.getEventCount(),
                        "eventEditMode", serializeEnum(sequence.getEventEditMode()),
                        "eventTime", toIsoTime(sequence.getEventTime()),
                        "frequencyMultiplier", sequence.getFrequencyMultiplier(),
                        "maxActiveEvents", sequence.getMaxActiveEvents(),
                        "onCardDeleteAction", serializeEnum(sequence.getOnCardDeleteAction()),
                        "sequenceParamsEditMode", serializeEnum(sequence.getSequenceParamsEditMode()),
                        "showGeneratedEventsPreview", sequence.getShowGeneratedEventsPreview(),
                        "timeZone", sequence.getTimeZone(),
                        "eventType", serializeEnum(sequence.getType()),
                        "firstEvent", toIsoDate(sequence.getFirstEvent()),
                        "lastEvent", toIsoDate(sequence.getLastEvent()),
                        "endType", serializeEnum(sequence.getEndType())
                ).accept(notificationSerializer(null, sequence.getNotifications()))
                .accept(participantsSerializer(sequence.getParticipants())).accept(m -> {
                    if (events != null) {
                        m.put("events", events.stream().map(this::serializeDetailedEvent).collect(toList()));
                    }
                    lookupHelper.serializeLookupValueDetail("category", sequence.getCategory(), m::put);
                    lookupHelper.serializeLookupValueDetail("priority", sequence.getPriority(), m::put);
                    lookupHelper.serializeLookupValueDetail("frequency", sequence.getFrequency(), m::put);
                    lookupHelper.serializeLookupValueDetail("endType", sequence.getEndType(), m::put);
                });
    }

    public FluentMap<String, Object> serializeBasicTrigger(CalendarTrigger trigger) {
        return map(
                "_id", trigger.getId(),
                "code", trigger.getCode(),
                "ownerClass", trigger.getOwnerClass(),
                "_ownerClass_description", dao.getClasse(trigger.getOwnerClass()).getDescription(),
                "ownerAttr", trigger.getOwnerAttr(),
                "_ownerAttr_description", dao.getClasse(trigger.getOwnerClass()).getAttribute(trigger.getOwnerAttr()).getDescription(),
                "active", trigger.isActive()
        );
    }

    public FluentMap<String, Object> serializeDetailedTrigger(CalendarTriggerInfo trigger) {
        return serializeDetailedTrigger(calendarService.getTriggerById(trigger.getId()));
    }

    public FluentMap<String, Object> serializeDetailedTrigger(CalendarTrigger trigger) {
        return serializeBasicTrigger(trigger).with(
                        "conditionScript", trigger.getConditionScript(),
                        "code", trigger.getCode(),
                        "content", trigger.getContent(),
                        "_content_translation", translationService.translateCalendarTriggerContent(trigger.getCode(), trigger.getContent()),
                        "delay", toIsoInterval(trigger.getDelay()),
                        "description", trigger.getDescription(),
                        "_description_translation", translationService.translateCalendarTriggerDescription(trigger.getCode(), trigger.getDescription()),
                        "eventCount", trigger.getEventCount(),
                        "eventEditMode", serializeEnum(trigger.getEventEditMode()),
                        "eventTime", toIsoTime(trigger.getEventTime()),
                        "frequencyMultiplier", trigger.getFrequencyMultiplier(),
                        "maxActiveEvents", trigger.getMaxActiveEvents(),
                        "onCardDeleteAction", serializeEnum(trigger.getOnCardDeleteAction()),
                        "sequenceParamsEditMode", serializeEnum(trigger.getSequenceParamsEditMode()),
                        "showGeneratedEventsPreview", trigger.getShowGeneratedEventsPreview(),
                        "timeZone", trigger.getTimeZone(),
                        "lastEvent", toIsoDate(trigger.getLastEvent()),
                        "eventType", serializeEnum(trigger.getType()),
                        "endType", serializeEnum(trigger.getEndType()),
                        "scope", serializeEnum(trigger.getScope())
                ).accept(notificationSerializer(null, trigger.getNotifications()))
                .accept(participantsSerializer(trigger.getParticipants())).accept(m -> {
                    lookupHelper.serializeLookupValueDetail("category", trigger.getCategory(), m::put);
                    lookupHelper.serializeLookupValueDetail("priority", trigger.getPriority(), m::put);
                    lookupHelper.serializeLookupValueDetail("frequency", trigger.getFrequency(), m::put);
                    lookupHelper.serializeLookupValueDetail("endType", trigger.getEndType(), m::put);
                });
    }
}
