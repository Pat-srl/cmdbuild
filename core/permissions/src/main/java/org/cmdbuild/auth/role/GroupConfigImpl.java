/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.role;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import static com.google.common.base.MoreObjects.firstNonNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.auth.role.GroupConfigImpl.GroupConfigImplBuilder;
import org.cmdbuild.utils.lang.Builder;

@JsonDeserialize(builder = GroupConfigImplBuilder.class)
public class GroupConfigImpl implements GroupConfig {

    private final boolean processWidgetAlwaysEnabled;
    private final String startingClass;
    private final Boolean bulkUpdate;
    private final Boolean bulkDelete;
    private final Boolean bulkAbort;
    private final Boolean fullTextSearch;

    private GroupConfigImpl(GroupConfigImplBuilder builder) {
        this.processWidgetAlwaysEnabled = firstNonNull(builder.processWidgetAlwaysEnabled, false);
        this.startingClass = builder.startingClass;
        this.bulkUpdate = builder.bulkUpdate;
        this.bulkDelete = builder.bulkDelete;
        this.bulkAbort = builder.bulkAbort;
        this.fullTextSearch = builder.fullTextSearch;
    }

    @Override
    public boolean getProcessWidgetAlwaysEnabled() {
        return processWidgetAlwaysEnabled;
    }

    @Override
    @Nullable
    public String getStartingClass() {
        return startingClass;
    }

    @Override
    @Nullable
    public Boolean getBulkUpdate() {
        return bulkUpdate;
    }

    @Override
    @Nullable
    public Boolean getBulkDelete() {
        return bulkDelete;
    }

    @Override
    @Nullable
    public Boolean getBulkAbort() {
        return bulkAbort;
    }

    @Override
    @Nullable
    public Boolean getFullTextSearch() {
        return fullTextSearch;
    }

    public static GroupConfigImplBuilder builder() {
        return new GroupConfigImplBuilder();
    }

    public static GroupConfigImplBuilder copyOf(GroupConfig source) {
        return new GroupConfigImplBuilder()
                .withProcessWidgetAlwaysEnabled(source.getProcessWidgetAlwaysEnabled())
                .withStartingClass(source.getStartingClass())
                .withBulkUpdate(source.getBulkUpdate())
                .withBulkDelete(source.getBulkDelete())
                .withBulkAbort(source.getBulkAbort())
                .withFullTextSearch(source.getFullTextSearch());
    }

    public static class GroupConfigImplBuilder implements Builder<GroupConfigImpl, GroupConfigImplBuilder> {

        private Boolean processWidgetAlwaysEnabled;
        private String startingClass;
        private Boolean bulkUpdate;
        private Boolean bulkDelete;
        private Boolean bulkAbort;
        private Boolean fullTextSearch;

        public GroupConfigImplBuilder withProcessWidgetAlwaysEnabled(Boolean processWidgetAlwaysEnabled) {
            this.processWidgetAlwaysEnabled = processWidgetAlwaysEnabled;
            return this;
        }

        public GroupConfigImplBuilder withStartingClass(String startingClass) {
            this.startingClass = startingClass;
            return this;
        }

        public GroupConfigImplBuilder withBulkUpdate(Boolean bulkUpdate) {
            this.bulkUpdate = bulkUpdate;
            return this;
        }

        public GroupConfigImplBuilder withBulkDelete(Boolean bulkDelete) {
            this.bulkDelete = bulkDelete;
            return this;
        }

        public GroupConfigImplBuilder withBulkAbort(Boolean bulkAbort) {
            this.bulkAbort = bulkAbort;
            return this;
        }

        public GroupConfigImplBuilder withFullTextSearch(Boolean fullTextSearch) {
            this.fullTextSearch = fullTextSearch;
            return this;
        }

        @Override
        public GroupConfigImpl build() {
            return new GroupConfigImpl(this);
        }

    }
}
