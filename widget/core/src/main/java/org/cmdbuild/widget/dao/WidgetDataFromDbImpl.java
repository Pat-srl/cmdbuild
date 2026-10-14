/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.widget.dao;

import org.cmdbuild.widget.model.WidgetDbData;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.gson.Gson;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.mapType;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.widget.model.WidgetData;
import static org.cmdbuild.widget.utils.WidgetConst.ATTR_DATA;
import static org.cmdbuild.widget.utils.WidgetConst.ATTR_IS_ACTIVE;
import static org.cmdbuild.widget.utils.WidgetConst.ATTR_OWNER;
import static org.cmdbuild.widget.utils.WidgetConst.ATTR_TYPE;
import static org.cmdbuild.widget.utils.WidgetConst.WIDGET_TABLE;

@CardMapping(WIDGET_TABLE)
public class WidgetDataFromDbImpl implements WidgetDbData {

    private final String label, type, id;
    private final String owner;
    private final Map<String, Object> data;
    private final boolean isActive;

    private WidgetDataFromDbImpl(WidgetDataFromDbImplBuilder builder) {
        this.owner = checkNotNull(builder.owner);
        this.label = checkNotNull(builder.label);
        this.type = checkNotBlank(builder.type);
        this.id = checkNotBlank(builder.id);
        this.data = (Map) map(checkNotNull(builder.data)).immutable();
        this.isActive = checkNotNull(builder.isActive);
    }

    @Override
    @CardAttr(ATTR_OWNER)
    public String getOwner() {
        return owner;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getLabel() {
        return label;
    }

    @Override
    @CardAttr(ATTR_TYPE)
    public String getType() {
        return type;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getId() {
        return id;
    }

    @Override
    public Map<String, Object> getData() {
        return data;
    }

    @CardAttr(ATTR_DATA)
    public String getDataAsJson() {
        return new Gson().toJson(data);
    }

    @Override
    @CardAttr(ATTR_IS_ACTIVE)
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return "WidgetDataFromDbImpl{" + "type=" + type + ", id=" + id + ", owner=" + owner + '}';
    }

    public static WidgetDataFromDbImplBuilder builder() {
        return new WidgetDataFromDbImplBuilder();
    }

    public static WidgetDataFromDbImpl from(WidgetData widget) {
        if (widget instanceof WidgetDataFromDbImpl) {
            return (WidgetDataFromDbImpl) widget;
        } else {
            return copyOf(widget).build();
        }
    }

    public static WidgetDataFromDbImplBuilder copyOf(WidgetData source) {
        return new WidgetDataFromDbImplBuilder()
                .withLabel(source.getLabel())
                .withType(source.getType())
                .withId(source.getId())
                .withData(source.getData())
                .withActive(source.isActive());
    }

    public static WidgetDataFromDbImplBuilder copyOf(WidgetDbData source) {
        return new WidgetDataFromDbImplBuilder()
                .withOwner(source.getOwner())
                .withLabel(source.getLabel())
                .withType(source.getType())
                .withId(source.getId())
                .withData(source.getData())
                .withActive(source.isActive());
    }

    public static class WidgetDataFromDbImplBuilder implements Builder<WidgetDataFromDbImpl, WidgetDataFromDbImplBuilder> {

        private String owner;
        private String label;
        private String type;
        private String id;
        private Map<String, ?> data;
        private Boolean isActive;

        public WidgetDataFromDbImplBuilder withOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public WidgetDataFromDbImplBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        public WidgetDataFromDbImplBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public WidgetDataFromDbImplBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public WidgetDataFromDbImplBuilder withData(Map<String, ?> data) {
            this.data = data;
            return this;
        }

        public WidgetDataFromDbImplBuilder withDataAsJson(String jsonData) {
            return this.withData(fromJson(jsonData, mapType(String.class, Object.class)));
        }

        public WidgetDataFromDbImplBuilder withActive(Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        @Override
        public WidgetDataFromDbImpl build() {
            return new WidgetDataFromDbImpl(this);
        }

    }
}
