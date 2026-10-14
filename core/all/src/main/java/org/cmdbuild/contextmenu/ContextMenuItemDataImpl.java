/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.gson.Gson;
import jakarta.annotation.Nullable;
import java.util.Map;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.json.CmJsonUtils.mapType;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_ContextMenu")
public class ContextMenuItemDataImpl implements ContextMenuItemData {

    private final String classId, label, jsScript, componentId, type, visibility;
    private final Map<String, Object> config;
    private final boolean active;
    private final int index;
    private final Long id;
    private final ContextMenuOwnerType ownerType;

    private ContextMenuItemDataImpl(ContextMenuItemDataImplBuilder builder) {
        this.id = builder.id;
        this.label = nullToEmpty(builder.label);
        this.jsScript = builder.jsScript;
        this.componentId = builder.componentId;
        this.config = (Map) map(checkNotNull(builder.config)).immutable();
        this.type = checkNotBlank(builder.type);
        this.visibility = checkNotBlank(builder.visibility);
        this.active = builder.active;
        this.index = builder.index;
        this.classId = checkNotNull(builder.classId);
        this.ownerType = builder.ownerType;
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr("Owner")
    public String getClassId() {
        return classId;
    }

    @Override
    @CardAttr("OwnerType")
    public ContextMenuOwnerType getOwnerType() {
        return ownerType;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getLabel() {
        return label;
    }

    @Override
    @CardAttr("Script")
    public String getJsScript() {
        return jsScript;
    }

    @Override
    @CardAttr
    public String getComponentId() {
        return componentId;
    }

    @Override
    public Map<String, Object> getConfig() {
        return config;
    }

    @CardAttr("Config")
    public String getConfigAsJson() {
        return new Gson().toJson(config);
    }

    @Override
    @CardAttr
    public String getType() {
        return type;
    }

    @Override
    @CardAttr
    public String getVisibility() {
        return visibility;
    }

    @Override
    @CardAttr
    public boolean isActive() {
        return active;
    }

    @Override
    @CardAttr
    public int getIndex() {
        return index;
    }

    public static ContextMenuItemDataImplBuilder builder() {
        return new ContextMenuItemDataImplBuilder();
    }

    public static ContextMenuItemDataImplBuilder copyOf(ContextMenuItemData source) {
        return new ContextMenuItemDataImplBuilder()
                .withId(source.getId())
                .withLabel(source.getLabel())
                .withJsScript(source.getJsScript())
                .withComponentId(source.getComponentId())
                .withConfig(source.getConfig())
                .withType(source.getType())
                .withVisibility(source.getVisibility())
                .withActive(source.isActive())
                .withClassId(source.getClassId())
                .withIndex(source.getIndex())
                .withOwnerType(source.getOwnerType());
    }

    public static class ContextMenuItemDataImplBuilder implements Builder<ContextMenuItemDataImpl, ContextMenuItemDataImplBuilder> {

        private String label;
        private String jsScript;
        private String componentId;
        private Map<String, Object> config;
        private String type;
        private String classId, visibility;
        private Boolean active;
        private Integer index;
        private Long id;
        private ContextMenuOwnerType ownerType;

        public ContextMenuItemDataImplBuilder withClassId(String classId) {
            this.classId = classId;
            return this;
        }

        public ContextMenuItemDataImplBuilder withOwnerType(ContextMenuOwnerType ownerType) {
            this.ownerType = ownerType;
            return this;
        }

        public ContextMenuItemDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ContextMenuItemDataImplBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        public ContextMenuItemDataImplBuilder withJsScript(String jsScript) {
            this.jsScript = jsScript;
            return this;
        }

        public ContextMenuItemDataImplBuilder withComponentId(String componentId) {
            this.componentId = componentId;
            return this;
        }

        public ContextMenuItemDataImplBuilder withConfig(Map<String, Object> config) {
            this.config = config;
            return this;
        }

        public ContextMenuItemDataImplBuilder withConfigAsJson(String jsonData) {
            return this.withConfig(fromJson(jsonData, mapType(String.class, Object.class)));
        }

        public ContextMenuItemDataImplBuilder withType(String type) {
            this.type = type;
            return this;
        }

        public ContextMenuItemDataImplBuilder withVisibility(String visibility) {
            this.visibility = visibility;
            return this;
        }

        public ContextMenuItemDataImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public ContextMenuItemDataImplBuilder withIndex(Integer index) {
            this.index = index;
            return this;
        }

        @Override
        public ContextMenuItemDataImpl build() {
            return new ContextMenuItemDataImpl(this);
        }

    }
}
