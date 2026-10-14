/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.activitylog;

import static java.util.Collections.emptyMap;
import java.util.Map;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
public class ActivityLogInfoImpl implements ActivityLogInfo {

    private final ActivityLogAction code;
    private final Map<String, Object> metadata;

    private ActivityLogInfoImpl(ActivityLogInfoImplBuilder builder) {
        this.code = checkNotNull(builder.code);
        this.metadata = map(firstNotNull(builder.metadata, emptyMap())).immutable();
    }

    @Override
    public ActivityLogAction getCode() {
        return code;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "EventLogInfoImpl{" + "code=" + code + '}';
    }

    public static ActivityLogInfoImplBuilder builder() {
        return new ActivityLogInfoImplBuilder();
    }

    public static ActivityLogInfoImplBuilder copyOf(ActivityLogInfo source) {
        return new ActivityLogInfoImplBuilder()
                .withCode(source.getCode())
                .withMetadata(source.getMetadata());
    }

    public static ActivityLogInfoImpl build(ActivityLogAction code, String entryType) {
        return new ActivityLogInfoImplBuilder()
                .withCode(code)
                .withEntryTypeName(entryType)
                .build();
    }

    public static ActivityLogInfoImpl build(ActivityLogAction code, String entryType, Long entryId) {
        return new ActivityLogInfoImplBuilder()
                .withCode(code)
                .withEntryTypeName(entryType)
                .withCardId(entryId)
                .build();
    }

    public static class ActivityLogInfoImplBuilder implements Builder<ActivityLogInfo, ActivityLogInfoImplBuilder> {

        private ActivityLogAction code;
        private Map<String, Object> metadata = map();

        public ActivityLogInfoImplBuilder withCode(ActivityLogAction code) {
            this.code = code;
            return this;
        }

        public ActivityLogInfoImplBuilder withMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public ActivityLogInfoImplBuilder withEntryTypeName(String entryTypeName) {
            this.metadata = map(metadata).with("Type", entryTypeName);
            return this;
        }

        public ActivityLogInfoImplBuilder withCardId(Long id) {
            this.metadata = map(metadata).with("Id", id);
            return this;
        }

        @Override
        public ActivityLogInfoImpl build() {
            return new ActivityLogInfoImpl(this);
        }

    }
}
