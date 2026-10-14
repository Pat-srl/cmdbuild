/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cmdbuild.calendar.beans.CalendarEventConfigImpl.CalendarEventConfigImplBuilder;
import static org.cmdbuild.calendar.beans.EventEditMode.EEM_WRITE;
import static org.cmdbuild.calendar.beans.PostCardDeleteAction.PCDA_CLEAR;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

@JsonDeserialize(builder = CalendarEventConfigImplBuilder.class)
public class CalendarEventConfigImpl implements CalendarEventConfig {

    private final PostCardDeleteAction onCardDeleteAction;
    private final EventEditMode eventEditMode;

    private CalendarEventConfigImpl(CalendarEventConfigImplBuilder builder) {
        this.eventEditMode = firstNotNull(builder.eventEditMode, EEM_WRITE);
        this.onCardDeleteAction = firstNotNull(builder.onCardDeleteAction, PCDA_CLEAR);
    }

    @Override
    public PostCardDeleteAction getOnCardDeleteAction() {
        return onCardDeleteAction;
    }

    @Override
    public EventEditMode getEventEditMode() {
        return eventEditMode;
    }

    public static CalendarEventConfigImplBuilder builder() {
        return new CalendarEventConfigImplBuilder();
    }

    public static CalendarEventConfigImplBuilder copyOf(CalendarEventConfig source) {
        return new CalendarEventConfigImplBuilder()
                .withOnCardDeleteAction(source.getOnCardDeleteAction())
                .withEventEditMode(source.getEventEditMode());
    }

    public static class CalendarEventConfigImplBuilder implements Builder<CalendarEventConfigImpl, CalendarEventConfigImplBuilder> {

        private PostCardDeleteAction onCardDeleteAction;
        private EventEditMode eventEditMode;

        public CalendarEventConfigImplBuilder withOnCardDeleteAction(PostCardDeleteAction onCardDeleteAction) {
            this.onCardDeleteAction = onCardDeleteAction;
            return this;
        }

        public CalendarEventConfigImplBuilder withEventEditMode(EventEditMode eventEditMode) {
            this.eventEditMode = eventEditMode;
            return this;
        }

        @Override
        public CalendarEventConfigImpl build() {
            return new CalendarEventConfigImpl(this);
        }

    }
}
