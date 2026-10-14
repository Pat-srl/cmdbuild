/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.navtree;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Joiner;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.collect.ImmutableList;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.utils.RelationDirectionUtils.serializeRelationDirection;
import org.cmdbuild.navtree.NavTreeNodeImpl.NavTreeNodeImplBuilder;
import static org.cmdbuild.navtree.NavTreeNodeSubclassViewMode.SVM_CARDS;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;

@JsonAutoDetect(getterVisibility = Visibility.NONE, isGetterVisibility = Visibility.NONE)
@JsonInclude(Include.NON_NULL)
@JsonDeserialize(builder = NavTreeNodeImplBuilder.class)
public class NavTreeNodeImpl implements NavTreeNode {

    private final String targetClassName, targetClassDescription, domainName, targetFilter, id, parentId;
    private final boolean showOnlyOne, enableRecursion, subclassViewShowIntermediateNodes;
    private final RelationDirection direction;
    private final List<NavTreeNode> nodes;
    private final NavTreeNodeSubclassViewMode subclassViewMode;
    private final List<String> subclassFilter;
    private final Map<String, String> subclassDescriptions;

    // Used in DEPENDENCYTREE trees only
    private final String borderSource;
    private final String borderColor;
    private final String iconSource;
    private final String iconFont;
    private final String iconColorMode;
    private final String iconColor;
    private final String sourceLookup;
    private final String sourceLookupAttribute;

    private NavTreeNodeImpl(NavTreeNodeImplBuilder builder) {
        this.targetClassName = checkNotBlank(builder.targetClassName);
        this.targetClassDescription = nullToEmpty(builder.targetClassDescription);
        this.domainName = builder.domainName;
        this.targetFilter = builder.targetFilter;
        this.id = checkNotBlank(builder.id);
        this.showOnlyOne = firstNonNull(builder.showOnlyOne, false);
        this.enableRecursion = firstNonNull(builder.enableRecursion, false);
        this.direction = firstNonNull(builder.direction, RD_DIRECT);
        this.parentId = builder.parentId;
        this.nodes = applyOrDefault(builder.nodes, n -> ImmutableList.copyOf(addParent(n, id)), emptyList());
        this.subclassViewMode = firstNotNull(builder.subclassViewMode, SVM_CARDS);
        this.subclassViewShowIntermediateNodes = firstNotNull(builder.subclassViewShowIntermediateNodes, true);
        this.subclassFilter = applyOrDefault(builder.subclassFilter, ImmutableList::copyOf, emptyList());
        switch (this.subclassViewMode) {
            case SVM_CARDS:
                this.subclassDescriptions = emptyMap();
                break;
            case SVM_SUBCLASSES:
                this.subclassDescriptions = applyOrDefault(builder.subclassDescriptions, m -> map(m).immutable(), emptyMap());
                break;
            default:
                throw unsupported("unsupported subclass view mode =< %s >", subclassViewMode);
        }

        // Used in DEPENDENCYTREE trees only
        this.borderSource = builder.borderSource;
        this.borderColor = builder.borderColor;
        this.iconSource = builder.iconSource;
        this.iconFont = builder.iconFont;
        this.iconColorMode = builder.iconColorMode;
        this.iconColor = builder.iconColor;
        this.sourceLookup = builder.sourceLookup;
        this.sourceLookupAttribute = builder.sourceLookupAttribute;
    }

    private static List<NavTreeNode> addParent(List<? extends NavTreeNode> nodes, String parentId) {
        return list(nodes).map(n -> copyOf(n).withParentId(parentId).build());
    }

    @Override
    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @Override
    @Nullable
    public String getParentId() {
        return parentId;
    }

    @Override
    @JsonProperty("targetClass")
    public String getTargetClassName() {
        return targetClassName;
    }

    @Override
    @JsonProperty("description")
    public String getTargetClassDescription() {
        return targetClassDescription;
    }

    @Override
    @Nullable
    @JsonProperty("domain")
    public String getDomainName() {
        return domainName;
    }

    @Override
    @Nullable
    @JsonProperty("filter")
    public String getTargetFilter() {
        return targetFilter;
    }

    @Override
    public RelationDirection getDirection() {
        return direction;
    }

    @JsonProperty("direction")
    public String getDirectionStr() {
        return serializeRelationDirection(direction);
    }

    @Override
    @JsonProperty("showOnlyOne")
    public boolean getShowOnlyOne() {
        return showOnlyOne;
    }

    @Override
    @JsonProperty("enableRecursion")
    public boolean getEnableRecursion() {
        return enableRecursion;
    }

    @Override
    @JsonProperty("nodes")
    public List<NavTreeNode> getChildNodes() {
        return nodes;
    }

    @Override
    public boolean getSubclassViewShowIntermediateNodes() {
        return subclassViewShowIntermediateNodes;
    }

    @JsonProperty("subclassViewShowIntermediateNodes")
    public Boolean getSubclassViewShowIntermediateNodesSerialized() {
        return switch (subclassViewMode) { //TODO improve this
            case SVM_SUBCLASSES ->
                subclassViewShowIntermediateNodes;
            default ->
                null;
        };
    }

    @Override
    @JsonProperty("subclassViewMode")
    public NavTreeNodeSubclassViewMode getSubclassViewMode() {
        return subclassViewMode;
    }

    @Override
    public List<String> getSubclassFilter() {
        return subclassFilter;
    }

    @JsonProperty("subclassFilter")
    @Nullable
    public String getSubclassFilterSerialized() {
        return subclassFilter.isEmpty() ? null : Joiner.on(",").join(subclassFilter);
    }

    @Override
    public Map<String, String> getSubclassDescriptions() {
        return subclassDescriptions;
    }

    @JsonAnyGetter
    public Map<String, String> getSubclassDescriptionsSerialized() {
        return map(subclassDescriptions).mapKeys(k -> format("subclass_%s_description", k));
    }

    @Override
    @Nullable
    @JsonProperty("borderSource")
    public String getBorderSource() {
        return borderSource;
    }

    @Override
    @Nullable
    @JsonProperty("borderColor")
    public String getBorderColor() {
        return borderColor;
    }

    @Override
    @Nullable
    @JsonProperty("iconSource")
    public String getIconSource() {
        return iconSource;
    }

    @Override
    @Nullable
    @JsonProperty("iconFont")
    public String getIconFont() {
        return iconFont;
    }

    @Override
    @Nullable
    @JsonProperty("iconColorMode")
    public String getIconColorMode() {
        return iconColorMode;
    }

    @Override
    @Nullable
    @JsonProperty("iconColor")
    public String getIconColor() {
        return iconColor;
    }

    @Override
    @Nullable
    @JsonProperty("sourceLookup")
    public String getSourceLookup() {
        return sourceLookup;
    }

    @Override
    @Nullable
    @JsonProperty("sourceLookupAttribute")
    public String getSourceLookupAttribute(){
        return sourceLookupAttribute;
    }

    @Override
    public String toString() {
        return "NavTreeNode{id=" + id + ", targetClass=" + targetClassName + ", domain=" + domainName + (isBlank(domainName) ? "" : ", direction=" + serializeEnum(direction)) + '}';
    }

    public static NavTreeNodeImplBuilder builder() {
        return new NavTreeNodeImplBuilder();
    }

    public static NavTreeNodeImplBuilder copyOf(NavTreeNode source) {
        return new NavTreeNodeImplBuilder()
                .withTargetClassName(source.getTargetClassName())
                .withTargetClassDescription(source.getTargetClassDescription())
                .withDomainName(source.getDomainName())
                .withTargetFilter(source.getTargetFilter())
                .withId(source.getId())
                .withParentId(source.getParentId())
                .withShowOnlyOne(source.getShowOnlyOne())
                .withEnableRecursion(source.getEnableRecursion())
                .withDirection(source.getDirection())
                .withChildNodes(source.getChildNodes())
                .withSubclassDescriptions(source.getSubclassDescriptions())
                .withSubclassViewShowIntermediateNodes(source.getSubclassViewShowIntermediateNodes())
                .withSubclassFilter(source.getSubclassFilter())
                .withSubclassViewMode(source.getSubclassViewMode())
                // Used in DEPENDENCYTREE trees only
                .withBorderSource(source.getBorderSource())
                .withBorderColor(source.getBorderColor())
                .withIconSource(source.getIconSource())
                .withIconFont(source.getIconFont())
                .withIconColorMode(source.getIconColorMode())
                .withIconColor(source.getIconColor())
                .withSourceLookup(source.getSourceLookup())
                .withSourceLookupAttribute(source.getSourceLookupAttribute());
    }

    @JsonPOJOBuilder
    public static class NavTreeNodeImplBuilder implements Builder<NavTreeNodeImpl, NavTreeNodeImplBuilder> {

        private String targetClassName;
        private String targetClassDescription;
        private String domainName;
        private String targetFilter;
        private String id, parentId;
        private Boolean showOnlyOne;
        private Boolean enableRecursion, subclassViewShowIntermediateNodes;
        private RelationDirection direction;
        private List<NavTreeNode> nodes;
        private NavTreeNodeSubclassViewMode subclassViewMode;
        private List<String> subclassFilter;
        private Map<String, String> subclassDescriptions;

        // Used in DEPENDENCYTREE trees only
        private String borderSource;
        private String borderColor;
        private String iconSource;
        private String iconFont;
        private String iconColorMode;
        private String iconColor;
        private String sourceLookup;
        private String sourceLookupAttribute;

        @JsonAnySetter
        private final Map<String, Object> any = map();

        @JsonProperty("subclassFilter")
        protected void withSubclassFilter(String subclassFilter) {
            this.subclassFilter = toListOfStrings(subclassFilter);
        }

        @JsonProperty("targetClass")
        public NavTreeNodeImplBuilder withTargetClassName(String targetClassName) {
            this.targetClassName = targetClassName;
            return this;
        }

        @JsonProperty("subclassViewShowIntermediateNodes")
        public NavTreeNodeImplBuilder withSubclassViewShowIntermediateNodes(Boolean subclassViewShowIntermediateNodes) {
            this.subclassViewShowIntermediateNodes = subclassViewShowIntermediateNodes;
            return this;
        }

        @JsonProperty("subclassViewMode")
        public NavTreeNodeImplBuilder withSubclassViewMode(String subclassViewMode) {
            return withSubclassViewMode(parseEnumOrNull(subclassViewMode, NavTreeNodeSubclassViewMode.class));
        }

        public NavTreeNodeImplBuilder withSubclassViewMode(NavTreeNodeSubclassViewMode subclassViewMode) {
            this.subclassViewMode = subclassViewMode;
            return this;
        }

        public NavTreeNodeImplBuilder withSubclassFilter(List<String> subclassFilter) {
            this.subclassFilter = subclassFilter;
            return this;
        }

        public NavTreeNodeImplBuilder withSubclassDescriptions(Map<String, String> subclassDescriptions) {
            this.subclassDescriptions = subclassDescriptions;
            return this;
        }

        @JsonProperty("description")
        public NavTreeNodeImplBuilder withTargetClassDescription(String targetClassDescription) {
            this.targetClassDescription = targetClassDescription;
            return this;
        }

        @JsonProperty("domain")
        public NavTreeNodeImplBuilder withDomainName(String domainName) {
            this.domainName = domainName;
            return this;
        }

        @JsonProperty("filter")
        public NavTreeNodeImplBuilder withTargetFilter(String targetFilter) {
            this.targetFilter = targetFilter;
            return this;
        }

        @JsonProperty("_id")
        public NavTreeNodeImplBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public NavTreeNodeImplBuilder withParentId(String parentId) {
            this.parentId = parentId;
            return this;
        }

        @JsonProperty("showOnlyOne")
        public NavTreeNodeImplBuilder withShowOnlyOne(Boolean showOnlyOne) {
            this.showOnlyOne = showOnlyOne;
            return this;
        }

        @JsonProperty("enableRecursion")
        public NavTreeNodeImplBuilder withEnableRecursion(Boolean enableRecursion) {
            this.enableRecursion = enableRecursion;
            return this;
        }

        @JsonProperty("direction")
        public NavTreeNodeImplBuilder withDirection(String direction) {
            return this.withDirection(parseEnumOrNull(direction, RelationDirection.class));
        }

        public NavTreeNodeImplBuilder withDirection(RelationDirection direction) {
            this.direction = direction;
            return this;
        }

        @JsonProperty("nodes")
        protected void withNodes(List<NavTreeNodeImpl> nodes) {
            withChildNodes((List) nodes);
        }

        public NavTreeNodeImplBuilder withChildNodes(List<NavTreeNode> nodes) {
            this.nodes = nodes;
            return this;
        }

        /**
         * Used in DEPENDENCYTREE trees only.
         *
         * @param borderSource
         * @return
         */
        public NavTreeNodeImplBuilder withBorderSource(String borderSource) {
            this.borderSource = borderSource;
            return this;
        }

        /**
         * Used in DEPENDENCYTREE trees only.
         *
         * @param borderColor
         * @return
         */
        public NavTreeNodeImplBuilder withBorderColor(String borderColor) {
            this.borderColor = borderColor;
            return this;
        }

        /**
         * Used in DEPENDENCYTREE trees only.
         *
         * @param iconSource
         * @return
         */
        public NavTreeNodeImplBuilder withIconSource(String iconSource) {
            this.iconSource = iconSource;
            return this;
        }

        /**
         * Used in DEPENDENCYTREE trees only.
         *
         * @param iconFont
         * @return
         */
        public NavTreeNodeImplBuilder withIconFont(String iconFont) {
            this.iconFont = iconFont;
            return this;
        }

        /**
         * Used in DEPENDENCYTREE trees only.
         *
         * @param iconColorMode
         * @return
         */
        public NavTreeNodeImplBuilder withIconColorMode(String iconColorMode) {
            this.iconColorMode = iconColorMode;
            return this;
        }

        /**
         * Used in DEPENDENCYTREE trees only.
         *
         * @param iconColor
         * @return
         */
        public NavTreeNodeImplBuilder withIconColor(String iconColor) {
            this.iconColor = iconColor;
            return this;
        }

        /**
         * Used in DEPENDENCYTREE trees only.
         *
         * @param sourceLookup
         * @return
         */
        public NavTreeNodeImplBuilder withSourceLookup(String sourceLookup) {
            this.sourceLookup = sourceLookup;
            return this;
        }

         /**
         * Used in DEPENDENCYTREE trees only.
         *
         * @param sourceLookupAttribute
         * @return
         */
        public NavTreeNodeImplBuilder withSourceLookupAttribute(String sourceLookupAttribute) {
            this.sourceLookupAttribute = sourceLookupAttribute;
            return this;
        }

        @Override
        public NavTreeNodeImpl build() {
            this.subclassDescriptions = firstNotNull(subclassDescriptions, map(unflattenMap(any, "subclass")).mapValues(CmStringUtils::toStringOrNull).mapKeys(k -> k.replaceFirst("_description$", "")).immutable());
            return new NavTreeNodeImpl(this);
        }

    }
}
