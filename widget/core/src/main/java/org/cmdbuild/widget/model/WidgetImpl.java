/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.model;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class WidgetImpl implements Widget {

    private final String type, label, id;
    private final boolean isActive;
    private final Map<String, Object> data, context;

    protected WidgetImpl(WidgetImplBuilder builder) {
        this.type = checkNotBlank(builder.type, "widget type is null");
        this.label = checkNotNull(builder.label, "widget label is null");
        this.id = checkNotBlank(builder.id, "widget id is null");
        this.isActive = builder.isActive;
        this.data = map(checkNotNull(builder.data)).immutable();
        this.context = map(checkNotNull(builder.context)).immutable();
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public final Map<String, Object> getData() {
        return data;
    }

    @Override
    public Map<String, Object> getContext() {
        return context;
    }

    @Override
    public String toString() {
        return "WidgetImpl{" + "type=" + type + ", id=" + id + '}';
    }

    public static WidgetImplBuilder builder() {
        return new WidgetImplBuilder();
    }

    public static WidgetImplBuilder copyOf(Widget source) {
        return new WidgetImplBuilder()
                .withType(source.getType())
                .withLabel(source.getLabel())
                .withId(source.getId())
                .withActive(source.isActive())
                .withData(source.getData())
                .withContext(source.getContext());
    }

    public static class WidgetImplBuilder implements Builder<WidgetImpl, WidgetImplBuilder>, Widget {

        private String type;
        private String label;
        private String id;
        private Boolean isActive;
        private Map<String, Object> data, context;

        public WidgetImplBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public WidgetImplBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        public WidgetImplBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public WidgetImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public WidgetImplBuilder withData(Map<String, Object> data) {
            this.data = data;
            return this;
        }

        public WidgetImplBuilder withContext(Map<String, Object> context) {
            this.context = context;
            return this;
        }

        public WidgetImplBuilder with(String key, Object value) {
            if (data == null) {
                data = map();
            } else {
                data = map(data);
            }
            data.put(key, value);
            return this;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public String getLabel() {
            return label;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public boolean isActive() {
            return isActive;
        }

        @Override
        public Map<String, Object> getData() {
            return data;
        }

        @Override
        public Map<String, Object> getContext() {
            return context;
        }

        @Override
        public WidgetImpl build() {
            return new WidgetImpl(this);
        }

    }
}
