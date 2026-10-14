/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.contextmenu;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

public class ContextMenuItemImpl implements ContextMenuItem {

    private final String label, jsScript, componentId;
    private final Map<String, Object> config;
    private final ContextMenuType type;
    private final boolean active;
    private final ContextMenuVisibility visibility;
    private final ContextMenuOwnerType ownerType;

    private ContextMenuItemImpl(ContextMenuItemImplBuilder builder) {
        this.type = checkNotNull(builder.type);
        this.active = checkNotNull(builder.active);
        this.ownerType = builder.ownerType;
        switch (type) {
            case COMPONENT, CUSTOM -> {
                this.label = checkNotBlank(builder.label);
                this.visibility = checkNotNull(builder.visibility);
            }
            case SEPARATOR -> {
                this.label = "";
                this.visibility = ContextMenuVisibility.ALL;
            }
            default ->
                throw new UnsupportedOperationException("unsupported type = " + type);
        }
        switch (type) {
            case COMPONENT -> {
                componentId = checkNotBlank(builder.componentId, "component id is required for component-based menu itel");
                config = (Map) firstNotNull(map(checkNotNull(builder.config)).immutable(), map());
                jsScript = null;
            }
            case CUSTOM -> {
                componentId = firstNotNull(builder.componentId, randomId());
                config = map();
                jsScript = nullToEmpty(builder.jsScript);
            }
            case SEPARATOR -> {
                componentId = null;
                config = map();
                jsScript = null;
            }
            default ->
                throw new UnsupportedOperationException("unsupported type = " + type);
        }
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public ContextMenuOwnerType getOwnerType() {
        return ownerType;
    }

    @Override
    public String getJsScript() {
        return jsScript;
    }

    @Override
    public String getComponentId() {
        return componentId;
    }

    @Override
    public Map<String, Object> getConfig() {
        return config;
    }

    @Override
    public ContextMenuType getType() {
        return type;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public ContextMenuVisibility getVisibility() {
        return visibility;
    }

    public static ContextMenuItemImplBuilder builder() {
        return new ContextMenuItemImplBuilder();
    }

    public static ContextMenuItemImplBuilder copyOf(ContextMenuItem source) {
        return new ContextMenuItemImplBuilder()
                .withLabel(source.getLabel())
                .withJsScript(source.getJsScript())
                .withComponentId(source.getComponentId())
                .withConfig(source.getConfig())
                .withType(source.getType())
                .withActive(source.isActive())
                .withVisibility(source.getVisibility())
                .withOwnerType(source.getOwnerType());
    }

    public static class ContextMenuItemImplBuilder implements Builder<ContextMenuItemImpl, ContextMenuItemImplBuilder> {

        private String label;
        private String jsScript;
        private String componentId;
        private Map<String, ?> config;
        private ContextMenuType type;
        private boolean active;
        private ContextMenuVisibility visibility;
        private ContextMenuOwnerType ownerType;

        public ContextMenuItemImplBuilder withLabel(String label) {
            this.label = label;
            return this;
        }

        public ContextMenuItemImplBuilder withOwnerType(ContextMenuOwnerType ownerType) {
            this.ownerType = ownerType;
            return this;
        }

        public ContextMenuItemImplBuilder withJsScript(String jsScript) {
            this.jsScript = jsScript;
            return this;
        }

        public ContextMenuItemImplBuilder withComponentId(String componentId) {
            this.componentId = componentId;
            return this;
        }

        public ContextMenuItemImplBuilder withConfig(Map<String, ?> config) {
            this.config = config;
            return this;
        }

        public ContextMenuItemImplBuilder withType(ContextMenuType type) {
            this.type = type;
            return this;
        }

        public ContextMenuItemImplBuilder withActive(boolean active) {
            this.active = active;
            return this;
        }

        public ContextMenuItemImplBuilder withVisibility(ContextMenuVisibility visibility) {
            this.visibility = visibility;
            return this;
        }

        @Override
        public ContextMenuItemImpl build() {
            return new ContextMenuItemImpl(this);
        }

    }
}
