package org.cmdbuild.view;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;
import org.cmdbuild.cleanup.ViewType;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.dao.utils.CmFilterUtils.checkFilterSyntax;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNumber;
import static org.cmdbuild.view.ViewImpl.VIEW_CLASS_NAME;
import org.cmdbuild.view.join.JoinViewConfig;

@CardMapping(VIEW_CLASS_NAME)
public class ViewImpl implements View {

    public static final String VIEW_CLASS_NAME = "_View";

    private final Long id, userId;
    private final String name;
    private final String description;
    private final String sourceClassName;
    private final String sourceFunction;
    private final String filter;
    private final ViewType type;
    private final boolean active, shared;
    private final JoinViewConfig joinConfig;
    private final String iconFont;

    private ViewImpl(ViewImplBuilder builder) {
        this.id = builder.id;
        this.name = checkNotBlank(builder.name);
        checkNotNumber(name);
        this.description = nullToEmpty(builder.description);
        this.type = checkNotNull(builder.type);
        this.shared = firstNotNull(builder.shared, true);
        if (shared) {
            this.userId = null;
            this.active = firstNotNull(builder.active, true);
        } else {
            this.userId = checkNotNull(builder.userId, "shared filters must have a owner user id");
            this.active = true;
        }
        switch (type) {
            case VT_FILTER -> {
                this.sourceClassName = checkNotBlank(builder.sourceClass);
                this.sourceFunction = null;
                this.filter = checkFilterSyntax(checkNotBlank(builder.filter));
                this.joinConfig = null;
            }
            case VT_CALENDAR -> {
                this.sourceClassName = null;
                this.sourceFunction = null;
                this.filter = checkFilterSyntax(checkNotBlank(builder.filter));
                this.joinConfig = null;
            }
            case VT_SQL -> {
                this.sourceFunction = checkNotBlank(builder.sourceFunction);
                this.sourceClassName = null;
                this.filter = null;
                this.joinConfig = null;
            }
            case VT_JOIN -> {
                this.joinConfig = checkNotNull(builder.joinConfig);
                this.sourceClassName = joinConfig.getMasterClass();
                this.sourceFunction = null;
                this.filter = null;
            }
            default ->
                throw unsupported("unsupported view type = %s", type);
        }
        this.iconFont = builder.iconFont;
    }

    @Override
    @Nullable
    @CardAttr(ATTR_ID)
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getName() {
        return name;
    }

    @Override
    @CardAttr(ATTR_DESCRIPTION)
    public String getDescription() {
        return description;
    }

    @Override
    @Nullable
    @CardAttr("IdSourceClass")
    public String getSourceClass() {
        return sourceClassName;
    }

    @Override
    @Nullable
    @CardAttr
    public String getSourceFunction() {
        return sourceFunction;
    }

    @Override
    @Nullable
    @CardAttr
    public String getFilter() {
        return filter;
    }

    @Override
    @CardAttr
    public ViewType getType() {
        return type;
    }

    @Override
    @CardAttr("Active")
    public boolean isActive() {
        return active;
    }

    @Override
    @CardAttr(ATTR_SHARED)
    public boolean isShared() {
        return shared;
    }

    @Override
    @CardAttr(ATTR_USER_ID)
    @Nullable
    public Long getUserId() {
        return userId;
    }

    @Override
    @Nullable
    @CardAttr("Config")
    public JoinViewConfig getJoinConfig() {
        return joinConfig;
    }

    @Override
    public String getPrivilegeId() {
        return privilegeId(PS_VIEW, getId());
    }

    @Nullable
    @Override
    @CardAttr("IconFont")
    public String getIconFont() { return iconFont;}

    @Override
    public String toString() {
        return "View{" + "id=" + id + ", name=" + name + ", type=" + type + '}';
    }

    public static ViewImplBuilder builder() {
        return new ViewImplBuilder();
    }

    public static ViewImplBuilder copyOf(View source) {
        return new ViewImplBuilder()
                .withId(source.getId())
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withSourceClass(source.getSourceClass())
                .withSourceFunction(source.getSourceFunction())
                .withFilter(source.getFilter())
                .withActive(source.isActive())
                .withShared(source.isShared())
                .withUserId(source.getUserId())
                .withType(source.getType())
                .withJoinConfig(source.getJoinConfig())
                .withIconFont(source.getIconFont());
    }

    public static class ViewImplBuilder implements Builder<ViewImpl, ViewImplBuilder> {

        private Long id;
        private String name;
        private String description;
        private String sourceClass;
        private String sourceFunction;
        private String filter;
        private ViewType type;
        private Boolean active;
        private JoinViewConfig joinConfig;
        private Boolean shared;
        private Long userId;
        private String iconFont;

        public ViewImplBuilder withShared(Boolean shared) {
            this.shared = shared;
            return this;
        }

        public ViewImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public ViewImplBuilder withUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public ViewImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public ViewImplBuilder withJoinConfig(JoinViewConfig joinConfig) {
            this.joinConfig = joinConfig;
            return this;
        }

        public ViewImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ViewImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public ViewImplBuilder withSourceClass(String sourceClass) {
            this.sourceClass = sourceClass;
            return this;
        }

        public ViewImplBuilder withSourceFunction(String sourceFunction) {
            this.sourceFunction = sourceFunction;
            return this;
        }

        public ViewImplBuilder withFilter(String filter) {
            this.filter = filter;
            return this;
        }

        public ViewImplBuilder withType(ViewType type) {
            this.type = type;
            return this;
        }

        public ViewImplBuilder withIconFont(String iconFont) {
            this.iconFont = iconFont;
            return this;
        }

        @Override
        public ViewImpl build() {
            return new ViewImpl(this);
        }

    }
}
