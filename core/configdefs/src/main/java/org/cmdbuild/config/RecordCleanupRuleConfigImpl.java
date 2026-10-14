/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import org.cmdbuild.cleanup.RecordCleanupRule;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
@ConfigComponent(value = "org.cmdbuild.database.cleanup_rule.default", module = true)
public class RecordCleanupRuleConfigImpl implements RecordCleanupRule {

    private final String code, target;

    @ConfigValue(key = "match", description = "rule matcher, one of always/filter/else", defaultValue = "always")
    private RecordCleanupRuleMatcher match;

    @ConfigValue(key = "filter", description = "rule filter, only for match=filter", defaultValue = "")
    private String filter;

    @ConfigValue(key = "maxRecordsToKeep")
    private Integer maxRecordsToKeep;

    @ConfigValue(key = "maxRecordAgeToKeepSeconds")
    private Long maxRecordAgeToKeepSeconds;

    @ConfigValue(key = "maxSizeMegs")
    private Long maxSizeMegs;

    public RecordCleanupRuleConfigImpl() {
        this("___DUMMY___");
    }

    public RecordCleanupRuleConfigImpl(String code) {
        this.code = checkNotBlank(code);
        Matcher matcher = Pattern.compile("^(.+?)(_.+)?$").matcher(code);
        checkArgument(matcher.matches(), "invalid record cleanup rule code =< %s >", code);
        target = checkNotBlank(matcher.group(1));
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
    public RecordCleanupRuleMatcher getMatcher() {
        return match;
    }

    @Override
    public CmdbFilter getFilter() {
        return parseFilter(filter);
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
}
