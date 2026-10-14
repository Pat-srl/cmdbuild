/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import org.cmdbuild.utils.json.JsonBean;

@JsonBean(CalendarSequenceConfigImpl.class)
public interface CalendarSequenceConfig {

    SequenceParamsEditMode getSequenceParamsEditMode();

    EventEditMode getEventEditMode();

    PostCardDeleteAction getOnCardDeleteAction();

    boolean getShowGeneratedEventsPreview();

    SequenceEndType getEndType();

    @Nullable
    String getConditionScript();

    EventFrequency getFrequency();

    int getFrequencyMultiplier();

    @Nullable
    Integer getEventCount();

    @Nullable
    Integer getMaxActiveEvents();

    default boolean hasEventCount() {
        return isNotNullAndGtZero(getEventCount());
    }

    default boolean hasMaxActiveEvents() {
        return isNotNullAndGtZero(getMaxActiveEvents());
    }

}
