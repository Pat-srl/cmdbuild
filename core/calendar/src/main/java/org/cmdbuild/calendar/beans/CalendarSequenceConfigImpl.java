/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar.beans;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.Preconditions.checkArgument;
import static org.cmdbuild.calendar.beans.EventEditMode.EEM_WRITE;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.calendar.beans.PostCardDeleteAction.PCDA_CLEAR;
import static org.cmdbuild.calendar.beans.SequenceParamsEditMode.SPEM_WRITE;
import static org.cmdbuild.calendar.beans.EventFrequency.EF_ONCE;
import static org.cmdbuild.calendar.beans.SequenceEndType.SET_OTHER;

@JsonDeserialize(builder = CalendarSequenceConfigImpl.CalendarSequenceConfigImplBuilder.class)
public class CalendarSequenceConfigImpl implements CalendarSequenceConfig {

    private final SequenceParamsEditMode sequenceParamsEditMode;
    private final EventEditMode eventEditMode;
    private final PostCardDeleteAction onCardDeleteAction;
    private final boolean showGeneratedEventsPreview;
    private final String conditionScript;
    private final EventFrequency frequency;
    private final Integer eventCount, maxActiveEvents;
    private final int frequencyMultiplier;
    private final SequenceEndType endType;

    private CalendarSequenceConfigImpl(CalendarSequenceConfigImplBuilder builder) {
        this.sequenceParamsEditMode = firstNotNull(builder.sequenceParamsEditMode, SPEM_WRITE);
        this.eventEditMode = firstNotNull(builder.eventEditMode, EEM_WRITE);
        this.onCardDeleteAction = firstNotNull(builder.onCardDeleteAction, PCDA_CLEAR);
        this.showGeneratedEventsPreview = firstNotNull(builder.showGeneratedEventsPreview, true);
        this.conditionScript = builder.conditionScript;
        this.frequency = firstNotNull(builder.frequency, EF_ONCE);
        this.eventCount = ltEqZeroToNull(builder.eventCount);
        this.maxActiveEvents = ltEqZeroToNull(builder.maxActiveEvents);
        this.frequencyMultiplier = firstNotNull(builder.frequencyMultiplier, 1);
        this.endType = firstNotNull(builder.endType, SET_OTHER);
        checkArgument(frequencyMultiplier > 0);
    }

    @Override
    public int getFrequencyMultiplier() {
        return frequencyMultiplier;
    }

    @Override
    public SequenceParamsEditMode getSequenceParamsEditMode() {
        return sequenceParamsEditMode;
    }

    @Override
    public EventEditMode getEventEditMode() {
        return eventEditMode;
    }

    @Override
    public PostCardDeleteAction getOnCardDeleteAction() {
        return onCardDeleteAction;
    }

    @Override
    public EventFrequency getFrequency() {
        return frequency;
    }

    @Override
    public boolean getShowGeneratedEventsPreview() {
        return showGeneratedEventsPreview;
    }

    @Override
    @Nullable
    public String getConditionScript() {
        return conditionScript;
    }

    @Override
    @Nullable
    public Integer getEventCount() {
        return eventCount;
    }

    @Override
    @Nullable
    public Integer getMaxActiveEvents() {
        return maxActiveEvents;
    }

    @Override
    public SequenceEndType getEndType() {
        return endType;
    }

    public static CalendarSequenceConfigImplBuilder builder() {
        return new CalendarSequenceConfigImplBuilder();
    }

    public static CalendarSequenceConfigImplBuilder copyOf(CalendarSequenceConfig source) {
        return new CalendarSequenceConfigImplBuilder()
                .withSequenceParamsEditMode(source.getSequenceParamsEditMode())
                .withEventEditMode(source.getEventEditMode())
                .withOnCardDeleteAction(source.getOnCardDeleteAction())
                .withShowGeneratedEventsPreview(source.getShowGeneratedEventsPreview())
                .withConditionScript(source.getConditionScript())
                .withFrequency(source.getFrequency())
                .withEventCount(source.getEventCount())
                .withMaxActiveEvents(source.getMaxActiveEvents())
                .withFrequencyMultiplier(source.getFrequencyMultiplier())
                .withEndType(source.getEndType());
    }

    public static class CalendarSequenceConfigImplBuilder implements Builder<CalendarSequenceConfigImpl, CalendarSequenceConfigImplBuilder> {

        private SequenceParamsEditMode sequenceParamsEditMode;
        private EventEditMode eventEditMode;
        private PostCardDeleteAction onCardDeleteAction;
        private boolean showGeneratedEventsPreview;
        private String conditionScript;
        private EventFrequency frequency;
        private Integer eventCount;
        private Integer maxActiveEvents;
        private Integer frequencyMultiplier;
        private SequenceEndType endType;

        public CalendarSequenceConfigImplBuilder withSequenceParamsEditMode(SequenceParamsEditMode sequenceParamsEditMode) {
            this.sequenceParamsEditMode = sequenceParamsEditMode;
            return this;
        }

        public CalendarSequenceConfigImplBuilder withEventEditMode(EventEditMode eventEditMode) {
            this.eventEditMode = eventEditMode;
            return this;
        }

        public CalendarSequenceConfigImplBuilder withFrequencyMultiplier(Integer frequencyMultiplier) {
            this.frequencyMultiplier = frequencyMultiplier;
            return this;
        }

        public CalendarSequenceConfigImplBuilder withOnCardDeleteAction(PostCardDeleteAction postCardDeleteAction) {
            this.onCardDeleteAction = postCardDeleteAction;
            return this;
        }

        public CalendarSequenceConfigImplBuilder withFrequency(EventFrequency frequency) {
            this.frequency = frequency;
            return this;
        }

        public CalendarSequenceConfigImplBuilder withShowGeneratedEventsPreview(boolean showGeneratedEventsPreview) {
            this.showGeneratedEventsPreview = showGeneratedEventsPreview;
            return this;
        }

        public CalendarSequenceConfigImplBuilder withConditionScript(String conditionScript) {
            this.conditionScript = conditionScript;
            return this;
        }

        public CalendarSequenceConfigImplBuilder withEventCount(Integer eventCount) {
            this.eventCount = eventCount;
            return this;
        }

        public CalendarSequenceConfigImplBuilder withMaxActiveEvents(Integer maxGeneratedEventCount) {
            this.maxActiveEvents = maxGeneratedEventCount;
            return this;
        }

        public CalendarSequenceConfigImplBuilder withEndType(SequenceEndType endType) {
            this.endType = endType;
            return this;
        }

        @Override
        public CalendarSequenceConfigImpl build() {
            return new CalendarSequenceConfigImpl(this);
        }

    }
}
