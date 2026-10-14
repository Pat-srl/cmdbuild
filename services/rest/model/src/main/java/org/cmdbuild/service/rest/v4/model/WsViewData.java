/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import org.cmdbuild.view.join.JoinAttributeGroupImpl;
import org.cmdbuild.view.join.JoinElement;
import org.cmdbuild.view.join.JoinAttributeImpl;
import org.cmdbuild.view.join.JoinType;
import org.cmdbuild.view.join.JoinAttribute;
import org.cmdbuild.view.join.JoinElementImpl;
import org.cmdbuild.view.join.JoinViewConfigImpl;
import org.cmdbuild.view.join.JoinViewConfig;
import org.cmdbuild.view.join.JoinAttributeGroup;
import org.cmdbuild.view.join.JoinViewPrivilegeMode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nullable;
import org.cmdbuild.cleanup.ViewType;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.formstructure.FormStructure;
import org.cmdbuild.formstructure.FormStructureImpl;
import org.cmdbuild.service.rest.common.serializationhelpers.ContextMenuSerializationHelper;
import org.cmdbuild.view.ViewImpl;
import org.cmdbuild.view.join.*;

import java.util.List;

import static org.cmdbuild.dao.utils.CmFilterUtils.parseFilter;
import static org.cmdbuild.dao.utils.CmFilterUtils.serializeFilter;
import static org.cmdbuild.dao.utils.CmSorterUtils.parseSorter;
import static org.cmdbuild.utils.json.CmJsonUtils.nullableToJson;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 *
 * @author ldare
 */
public class WsViewData {

    private final String name;
    private final String description;
    private final String sourceClassName;
    private final String sourceFunction;
    private final ViewType type;
    private final Boolean active, shared;
    private final String masterClass, masterClassAlias;
    private final CmdbFilter filter;
    private final CmdbSorter sorter;
    private final List<WsJoinElement> joinElements;
    private final List<WsJoinAttribute> attributes;
    private final List<WsJoinAttributeGroup> attributeGroups;
    private final JsonNode formStructure;
    private final JoinViewPrivilegeMode privilegeMode;
    private final List<ContextMenuSerializationHelper.WsClassDataContextMenuItem> contextMenuItems;
    private final String iconFont;

    public WsViewData(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("sourceClassName") String sourceClassName,
            @JsonProperty("sourceFunction") String sourceFunction,
            @JsonProperty("filter") String filter,
            @JsonProperty("active") Boolean active,
            @JsonProperty("shared") Boolean shared,
            @JsonProperty("type") String type,
            @JsonProperty("masterClass") String masterClass,
            @JsonProperty("masterClassAlias") String masterClassAlias,
            @JsonProperty("sorter") JsonNode sorter,
            @JsonProperty("join") List<WsJoinElement> joinElements,
            @JsonProperty("attributes") List<WsJoinAttribute> attributes,
            @JsonProperty("attributeGroups") List<WsJoinAttributeGroup> attributeGroups,
            @JsonProperty("formStructure") JsonNode formStructure,
            @JsonProperty("privilegeMode") String privilegeMode,
            @JsonProperty("contextMenuItems") List<ContextMenuSerializationHelper.WsClassDataContextMenuItem> contextMenuItems,
            @JsonProperty("icon_font") String iconFont
    ) {
        this.name = checkNotBlank(name);
        this.description = description;
        this.sourceClassName = sourceClassName;
        this.sourceFunction = sourceFunction;
        this.filter = parseFilter(filter);
        this.active = active;
        this.shared = shared;
        this.type = parseEnum(type, ViewType.class);
        this.masterClass = masterClass;
        this.masterClassAlias = masterClassAlias;
        this.sorter = parseSorter(nullableToJson(sorter));
        this.joinElements = nullToEmpty(joinElements);
        this.attributes = attributes;
        this.attributeGroups = nullToEmpty(attributeGroups);
        this.formStructure = formStructure;
        this.contextMenuItems = contextMenuItems;
        this.iconFont = iconFont;
        this.privilegeMode = parseEnumOrNull(privilegeMode, JoinViewPrivilegeMode.class);
    }

    public ViewImpl.ViewImplBuilder toView() {
        return ViewImpl.builder()
                .withName(name)
                .withDescription(description)
                .withSourceClass(sourceClassName)
                .withSourceFunction(sourceFunction)
                .withType(type)
                .withActive(active)
                .withShared(shared)
                .withIconFont(iconFont)
                .accept(b -> {
                    switch (type) {
                        case VT_JOIN -> b.withJoinConfig(toJoinConfig());
                        default -> b.withFilter(serializeFilter(filter));
                    }
                });
    }

    public JoinViewConfig toJoinConfig() {
        return JoinViewConfigImpl.builder()
                .withFilter(filter)
                .withMasterClass(masterClass)
                .withMasterClassAlias(masterClassAlias)
                .withSorter(sorter)
                .withAttributes(list(attributes).map(WsJoinAttribute::toJoinAttribute))
                .withAttributeGroups(list(attributeGroups).map(WsJoinAttributeGroup::toJoinAttributeGroup))
                .withJoinElements(list(joinElements).map(WsJoinElement::toJoinElement))
                .withPrivilegeMode(privilegeMode)
                .build();
    }

    public Boolean getShared() {
        return this.shared;
    }

    @Nullable
    public FormStructure getFormStructure() {
        return formStructure == null ? null : new FormStructureImpl(toJson(formStructure));
    }

    @Nullable
    public List<ContextMenuSerializationHelper.WsClassDataContextMenuItem> getContextMenuItems() {
        return firstNotNull(contextMenuItems, list());
    }

    public static class WsJoinElement {

        private final String source, domain, targetType, domainAlias, targetAlias;
        private final RelationDirection direction;
        private final JoinType joinType;

        public WsJoinElement(
                @JsonProperty("source") String source,
                @JsonProperty("domain") String domain,
                @JsonProperty("targetType") String targetType,
                @JsonProperty("domainAlias") String domainAlias,
                @JsonProperty("targetAlias") String targetAlias,
                @JsonProperty("direction") String direction,
                @JsonProperty("joinType") String joinType) {
            this.source = source;
            this.domain = domain;
            this.targetType = targetType;
            this.domainAlias = domainAlias;
            this.targetAlias = targetAlias;
            this.direction = parseEnum(direction, RelationDirection.class);
            this.joinType = parseEnum(joinType, JoinType.class);
        }

        public JoinElement toJoinElement() {
            return JoinElementImpl.builder()
                    .withDirection(direction)
                    .withDomain(domain)
                    .withDomainAlias(domainAlias)
                    .withJoinType(joinType)
                    .withSource(source)
                    .withTargetAlias(targetAlias)
                    .withTargetType(targetType)
                    .build();
        }

    }

    public static class WsJoinAttribute {

        private final String expr, name, description, group;
        private final Boolean showInGrid, showInReducedGrid;

        public WsJoinAttribute(
                @JsonProperty("expr") String expr,
                @JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("group") String group,
                @JsonProperty("showInGrid") Boolean showInGrid,
                @JsonProperty("showInReducedGrid") Boolean showInReducedGrid) {
            this.expr = expr;
            this.name = name;
            this.description = description;
            this.group = group;
            this.showInGrid = showInGrid;
            this.showInReducedGrid = showInReducedGrid;
        }

        public JoinAttribute toJoinAttribute() {
            return JoinAttributeImpl.builder()
                    .withExpr(expr)
                    .withDescription(description)
                    .withGroup(group)
                    .withName(name)
                    .withShowInGrid(showInGrid)
                    .withShowInReducedGrid(showInReducedGrid)
                    .build();
        }
    }

    public static class WsJoinAttributeGroup {

        private final String name, description, defaultDisplayMode;

        public WsJoinAttributeGroup(@JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("defaultDisplayMode") String defaultDisplayMode) {
            this.name = name;
            this.description = description;
            this.defaultDisplayMode = defaultDisplayMode;
        }

        public JoinAttributeGroup toJoinAttributeGroup() {
            return JoinAttributeGroupImpl.builder().withName(name).withDescription(description).withDefaultDisplayMode(defaultDisplayMode).build();
        }
    }
}
