/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.navtree;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import java.util.Map;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import static org.cmdbuild.navtree.NavTreeType.NT_DEFAULT;
import org.cmdbuild.utils.json.JsonBean;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_NavTree")
public class NavTreeImpl implements NavTree {

    private final String name, description, iconFont;
    private final Long id;
    private final NavTreeNode data;
    private final boolean active;
    private final NavTreeType type;

    private NavTreeMetadata metadata;

    private NavTreeImpl(NavTreeDataImplBuilder builder) {
        this.name = checkNotBlank(builder.name);
        this.description = nullToEmpty(builder.description);
        this.id = builder.id;
        this.data = checkNotNull(builder.data);
        this.active = firstNotNull(builder.active, true);
        this.type = firstNotNull(builder.type, NT_DEFAULT);
        this.iconFont = nullToEmpty(builder.iconFont);

        this.metadata = new NavTreeMetadataImpl(builder.metadata);
    }

    @Override
    @CardAttr(ATTR_CODE)
    public String getName() {
        return name;
    }

    @Nullable
    @CardAttr(ATTR_DESCRIPTION)
    @Override
    public String getDescription() {
        return description;
    }

    @Nullable
    @CardAttr(ATTR_ID)
    @Override
    public Long getId() {
        return id;
    }

    @CardAttr
    @Override
    public NavTreeNode getData() {
        return data;
    }

    @Override
    @CardAttr("Active")
    public boolean getActive() {
        return active;
    }

    @Override
    @CardAttr
    public NavTreeType getType() {
        return type;
    }

    @Nullable
    @Override
    @CardAttr("IconFont")
    public String getIconFont() {
        return iconFont;
    }

    @Override
    public NavTreeMetadata getMetadata() {
        return metadata;
    }

    @JsonBean
    /**
     * @CardAttr("Metadata") not used because it seems to not find method
     * <code>withMetadata(Map<String, String>)</code> in NavTreeImplBuilder
     *
     * with the following error while scanning in {@link CardMapperServiceImpl}
     * for <i>bean constructors</i>.      <pre>
     * Caused by: java.lang.IllegalArgumentException: missing card mapper setter methods for attributes = [Metadata] while parsing class org.cmdbuild.navtree.NavTreeImpl
     *	at org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument(CmExceptionUtils.java:84)
     *	at org.cmdbuild.utils.lang.CmPreconditions.checkArgument(CmPreconditions.java:44)
     *  at org.cmdbuild.dao.orm.services.CardMapperServiceImpl$CardMapperImpl.<init>(CardMapperServiceImpl.java:244)
     * </pre>
     */
    @CardAttr("Metadata")
    public Map<String, String> getMetadataAsMap() {
        return metadata.asMap();
    }

    @Override
    public String toString() {
        return "NavTree{" + "name=" + name + ", id=" + id + '}';
    }

    public static NavTreeDataImplBuilder builder() {
        return new NavTreeDataImplBuilder();
    }

    public static NavTreeDataImplBuilder copyOf(NavTree source) {
        return new NavTreeDataImplBuilder()
                .withName(source.getName())
                .withDescription(source.getDescription())
                .withData(source.getData())
                .withId(source.getId())
                .withActive(source.getActive())
                .withType(source.getType())
                .withIconFont(source.getIconFont())
                .withMetadataAsObj(source.getMetadata());
    }

    public static class NavTreeDataImplBuilder implements Builder<NavTreeImpl, NavTreeDataImplBuilder> {

        private String name;
        private String description;
        private Long id;
        private NavTreeNode data;
        private Boolean active;
        private NavTreeType type;
        private String iconFont;
        private final Map<String, String> metadata = map();

        public NavTreeDataImplBuilder withType(NavTreeType type) {
            this.type = type;
            return this;
        }

        public NavTreeDataImplBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public NavTreeDataImplBuilder withData(NavTreeNode data) {
            this.data = data;
            return this;
        }

        public NavTreeDataImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public NavTreeDataImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public NavTreeDataImplBuilder withActive(Boolean active) {
            this.active = active;
            return this;
        }

        public NavTreeDataImplBuilder withIconFont(String iconFont) {
            this.iconFont = iconFont;
            return this;
        }

        public NavTreeDataImplBuilder withMetadataAsObj(NavTreeMetadata metadata) {
            return withMetadataAsMap(metadata.asMap());
        }

        /**
         * Should be used when returning back from json e.g.
         * <code>{"cm_initialExpansionDepth": "12"}</code> stored in persistence
         * in field <code>MetadataAswMap</code>.
         *
         * @param metadata
         * @return
         */
        public NavTreeDataImplBuilder withMetadataAsMap(Map<String, String> metadata) {
            this.metadata.clear();
            this.metadata.putAll(nullToEmpty(metadata));
            return this;
        }

        @Override
        public NavTreeImpl build() {
            return new NavTreeImpl(this);
        }

    }
}
