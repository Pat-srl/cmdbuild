/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cleanup.inner;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.cleanup.RecordCleanupRule;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.data.filter.beans.CmdbFilterImpl.noopFilter;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class RecordCleanupRuleImpl implements RecordCleanupRule {

    private final RecordCleanupRuleMatcher matcher;
    private final String code, target;
    private final CmdbFilter filter;
    private final Integer maxRecordsToKeep;
    private final Long maxRecordAgeToKeepSeconds, maxSizeMegs;

    private RecordCleanupRuleImpl(RecordCleanupRuleImplBuilder builder) {
        this.matcher = checkNotNull(builder.matcher);
        this.code = checkNotBlank(builder.code);
        this.target = checkNotBlank(builder.target);
        switch (matcher) {
            case RM_FILTER -> {
                this.filter = checkNotNull(builder.filter);
                checkArgument(!this.filter.isNoop(), "invalid filter");
            }
            default ->
                this.filter = noopFilter();
        }
        this.maxRecordsToKeep = builder.maxRecordsToKeep;
        this.maxRecordAgeToKeepSeconds = builder.maxRecordAgeToKeepSeconds;
        this.maxSizeMegs = builder.maxSizeMegs;
    }

    @Override
    public RecordCleanupRuleMatcher getMatcher() {
        return matcher;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getTarget() {
        return target;
    }

    @Override
    public CmdbFilter getFilter() {
        return filter;
    }

    @Override
    @Nullable
    public Integer getMaxRecordsToKeep() {
        return maxRecordsToKeep;
    }

    @Override
    @Nullable
    public Long getMaxRecordAgeToKeepSeconds() {
        return maxRecordAgeToKeepSeconds;
    }

    @Override
    @Nullable
    public Long getMaxSizeMegs() {
        return maxSizeMegs;
    }

    public static RecordCleanupRuleImplBuilder builder() {
        return new RecordCleanupRuleImplBuilder();
    }

    public static RecordCleanupRuleImplBuilder copyOf(RecordCleanupRule source) {
        return new RecordCleanupRuleImplBuilder()
                .withMatcher(source.getMatcher())
                .withCode(source.getCode())
                .withTarget(source.getTarget())
                .withFilter(source.getFilter())
                .withMaxRecordsToKeep(source.getMaxRecordsToKeep())
                .withMaxRecordAgeToKeepSeconds(source.getMaxRecordAgeToKeepSeconds())
                .withMaxSizeMegs(source.getMaxSizeMegs());
    }

    public static class RecordCleanupRuleImplBuilder implements Builder<RecordCleanupRuleImpl, RecordCleanupRuleImplBuilder> {

        private RecordCleanupRuleMatcher matcher;
        private String code;
        private String target;
        private CmdbFilter filter;
        private Integer maxRecordsToKeep;
        private Long maxRecordAgeToKeepSeconds;
        private Long maxSizeMegs;

        public RecordCleanupRuleImplBuilder withMatcher(RecordCleanupRuleMatcher matcher) {
            this.matcher = matcher;
            return this;
        }

        public RecordCleanupRuleImplBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public RecordCleanupRuleImplBuilder withTarget(String target) {
            this.target = target;
            return this;
        }

        public RecordCleanupRuleImplBuilder withFilter(CmdbFilter filter) {
            this.filter = filter;
            return this;
        }

        public RecordCleanupRuleImplBuilder withMaxRecordsToKeep(Integer maxRecordsToKeep) {
            this.maxRecordsToKeep = maxRecordsToKeep;
            return this;
        }

        public RecordCleanupRuleImplBuilder withMaxRecordAgeToKeepSeconds(Long maxRecordAgeToKeepSeconds) {
            this.maxRecordAgeToKeepSeconds = maxRecordAgeToKeepSeconds;
            return this;
        }

        public RecordCleanupRuleImplBuilder withMaxSizeMegs(Long maxSizeMegs) {
            this.maxSizeMegs = maxSizeMegs;
            return this;
        }

        @Override
        public RecordCleanupRuleImpl build() {
            return new RecordCleanupRuleImpl(this);
        }

    }
}
