/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import java.time.LocalDate;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

public interface CalendarSequence extends CalendarEventCommons, CalendarConfigCommons {

    static final String CALENDAR_ATTR_CARD = "Card";

    @Nullable
    Long getId();

    @Nullable
    Long getTrigger();

    LocalDate getFirstEvent();

    @Nullable
    LocalDate getLastEvent();

    List<CalendarEvent> getEvents();

    @Override
    default EventEditMode getEventEditMode() {
        return getConfig().getEventEditMode();
    }

    @Override
    default PostCardDeleteAction getOnCardDeleteAction() {
        return getConfig().getOnCardDeleteAction();
    }

    default boolean hasLastEvent() {
        return getLastEvent() != null;
    }

    default boolean hasTrigger() {
        return isNotNullAndGtZero(getTrigger());
    }

}
