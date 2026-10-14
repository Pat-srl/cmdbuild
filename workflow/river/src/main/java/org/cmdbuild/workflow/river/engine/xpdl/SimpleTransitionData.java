/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.river.engine.xpdl;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SimpleTransitionData {

    private final String from, to, flagId;
    private final String conditionScript, conditionScriptType;
    private final boolean hasCondition, hasOtherwiseCondition;

    private SimpleTransitionData(SimpleTransitionDataBuilder builder) {
        this.from = checkNotBlank(builder.from);
        this.to = checkNotBlank(builder.to);
        this.flagId = checkNotBlank(builder.flagId);
        this.conditionScript = (builder.conditionScript);
        this.conditionScriptType = (builder.conditionScriptType);
        this.hasCondition = (builder.hasCondition);
        this.hasOtherwiseCondition = (builder.hasOtherwiseCondition);
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getFlagId() {
        return flagId;
    }

    public String getConditionScript() {
        return conditionScript;
    }

    public String getConditionScriptType() {
        return conditionScriptType;
    }

    public boolean hasCondition() {
        return hasCondition;
    }

    public boolean hasOtherwiseCondition() {
        return hasOtherwiseCondition;
    }

    public boolean hasNoCondition() {
        return !hasCondition;
    }

    public static SimpleTransitionDataBuilder builder() {
        return new SimpleTransitionDataBuilder();
    }

    public static SimpleTransitionDataBuilder copyOf(SimpleTransitionData source) {
        return new SimpleTransitionDataBuilder()
                .withFrom(source.getFrom())
                .withTo(source.getTo())
                .withFlagId(source.getFlagId())
                .withConditionScript(source.getConditionScript())
                .withConditionScriptType(source.getConditionScriptType())
                .withHasCondition(source.hasCondition())
                .withHasOtherwiseCondition(source.hasOtherwiseCondition());
    }

    public static class SimpleTransitionDataBuilder implements Builder<SimpleTransitionData, SimpleTransitionDataBuilder> {

        private String from;
        private String to;
        private String flagId;
        private String conditionScript = null;
        private String conditionScriptType = null;
        private boolean hasCondition = false;
        private boolean hasOtherwiseCondition = false;

        public SimpleTransitionDataBuilder withFrom(String from) {
            this.from = from;
            return this;
        }

        public SimpleTransitionDataBuilder withTo(String to) {
            this.to = to;
            return this;
        }

        public SimpleTransitionDataBuilder withFlagId(String flagId) {
            this.flagId = flagId;
            return this;
        }

        public SimpleTransitionDataBuilder withConditionScript(String conditionScript) {
            this.conditionScript = conditionScript;
            return this;
        }

        public SimpleTransitionDataBuilder withConditionScriptType(String conditionScriptType) {
            this.conditionScriptType = conditionScriptType;
            return this;
        }

        public SimpleTransitionDataBuilder withHasCondition(boolean hasCondition) {
            this.hasCondition = hasCondition;
            return this;
        }

        public SimpleTransitionDataBuilder withHasOtherwiseCondition(boolean hasOtherwiseCondition) {
            this.hasOtherwiseCondition = hasOtherwiseCondition;
            return this;
        }

        @Override
        public SimpleTransitionData build() {
            return new SimpleTransitionData(this);
        }

    }
}
