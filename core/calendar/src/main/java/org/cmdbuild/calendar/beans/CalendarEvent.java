/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.calendar.beans.CalendarEventStatus.CST_ACTIVE;
import static org.cmdbuild.calendar.beans.EventEditMode.EEM_WRITE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

public interface CalendarEvent extends CalendarEventCommons {

    public static final String EVENT_TABLE = "_CalendarEvent",
            EVENT_ATTR_SEQUENCE = "Sequence",
            EVENT_ATTR_BEGIN = "EventBegin",
            EVENT_ATTR_DATE = "EventDate",
            EVENT_ATTR_END = "EventEnd",
            EVENT_ATTR_CARD = "Card",
            EVENT_ATTR_CONTENT = "Content",
            EVENT_ATTR_DESCRIPTION = ATTR_DESCRIPTION,
            EVENT_ATTR_STATUS = "EventStatus",
            EVENT_ATTR_CATEGORY = "Category",
            EVENT_ATTR_PRIORITY = "Priority",
            EVENT_ATTR_TYPE = "EventType",
            EVENT_ATTR_COMPLETION = "EventCompletion",
            CALENDAR_CATEGORY_LOOKUP_TYPE = "CalendarCategory",
            CALENDAR_PRIORITY_LOOKUP_TYPE = "CalendarPriority";

    @Nullable
    Long getId();

    @Nullable
    Long getSequence();

    @Nullable
    String getNotes();

    CalendarEventStatus getStatus();

    ZonedDateTime getBegin();

    ZonedDateTime getEnd();

    @Nullable
    ZonedDateTime getCompleted();

    LocalDate getDate();

    CalendarEventConfig getConfig();

    List<String> getProcessedNotifications();

    @Override
    default EventEditMode getEventEditMode() {
        return getConfig().getEventEditMode();
    }

    @Override
    default PostCardDeleteAction getOnCardDeleteAction() {
        return getConfig().getOnCardDeleteAction();
    }

    default boolean isWritable() {
        return equal(getEventEditMode(), EEM_WRITE);
    }

    default boolean hasSequence() {
        return getSequence() != null;
    }

    default boolean isActive() {
        return equal(getStatus(), CST_ACTIVE);
    }

    default boolean hasCard() {
        return isNotNullAndGtZero(getCard());
    }

}
