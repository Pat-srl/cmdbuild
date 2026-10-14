/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import static com.google.common.base.Objects.equal;
import java.time.LocalTime;
import jakarta.annotation.Nullable;

public interface CalendarConfigCommons extends CalendarSequenceConfig {

    LocalTime getEventTime();

    CalendarSequenceConfig getConfig();

    @Override
    default EventFrequency getFrequency() {
        return getConfig().getFrequency();
    }

    @Override
    default int getFrequencyMultiplier() {
        return getConfig().getFrequencyMultiplier();
    }

    @Override
    @Nullable
    default Integer getMaxActiveEvents() {
        return getConfig().getMaxActiveEvents();
    }

    @Override
    @Nullable
    default Integer getEventCount() {
        return getConfig().getEventCount();
    }

    @Override
    @Nullable
    default String getConditionScript() {
        return getConfig().getConditionScript();
    }

    @Override
    default boolean getShowGeneratedEventsPreview() {
        return getConfig().getShowGeneratedEventsPreview();
    }

    @Override
    default PostCardDeleteAction getOnCardDeleteAction() {
        return getConfig().getOnCardDeleteAction();
    }

    @Override
    default EventEditMode getEventEditMode() {
        return getConfig().getEventEditMode();
    }

    @Override
    default SequenceParamsEditMode getSequenceParamsEditMode() {
        return getConfig().getSequenceParamsEditMode();
    }

    @Override
    default SequenceEndType getEndType() {
        return getConfig().getEndType();
    }

    default boolean hasFrequency(EventFrequency frequency) {
        return equal(frequency, getFrequency());
    }
}
