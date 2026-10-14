/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cleanup;

import static com.google.common.base.Objects.equal;
import jakarta.annotation.Nullable;
import org.cmdbuild.data.filter.CmdbFilter;

public interface RecordCleanupRule {

    RecordCleanupRuleMatcher getMatcher();

    String getCode();

    String getTarget();

    CmdbFilter getFilter();

    @Nullable
    Integer getMaxRecordsToKeep();

    @Nullable
    Long getMaxRecordAgeToKeepSeconds();

    @Nullable
    Long getMaxSizeMegs();

    default boolean hasMatcher(RecordCleanupRuleMatcher matcher) {
        return equal(matcher, getMatcher());
    }

    enum RecordCleanupRuleMatcher {
        RM_ALWAYS, RM_FILTER, RM_ELSE
    }

}
