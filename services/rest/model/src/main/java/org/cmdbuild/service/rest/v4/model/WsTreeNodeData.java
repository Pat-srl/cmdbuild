/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeNodeImpl;
import org.cmdbuild.navtree.NavTreeNodeSubclassViewMode;
import org.cmdbuild.navtree.NavTreeType;
import static org.cmdbuild.navtree.NavTreeType.NT_DEPENDENCYTREE;
import static org.cmdbuild.utils.json.CmJsonUtils.*;
import static org.cmdbuild.utils.lang.CmConvertUtils.*;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrDefault;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;

/**
 *
 * @author schursin
 */
public class WsTreeNodeData {

    private final String id;
    private final String filter, targetClass, description, domain, direction;
    private final Boolean recursionEnabled, showOnlyOne, subclassViewShowIntermediateNodes;
    private final List<WsTreeNodeData> nodes;
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

    @JsonCreator
    public WsTreeNodeData(Map<String, Object> values) {
        this.id = toStringOrNull(values.get("_id"));
        this.filter = toStringOrNull(values.get("filter"));
        this.targetClass = toStringOrNull(values.get("targetClass"));
        this.description = toStringOrNull(values.get("description"));
        this.domain = toStringOrNull(values.get("domain"));
        this.direction = toStringOrDefault(values.get("direction"), "_1");
        this.recursionEnabled = toBooleanOrNull(values.get("recursionEnabled"));
        this.showOnlyOne = toBooleanOrNull(values.get("showOnlyOne"));
        this.nodes = fromJson(toJson(values.get("nodes")), collectionType(WsTreeNodeData.class));
        this.subclassViewMode = parseEnumOrNull(toStringOrNull(values.get("subclassViewMode")),
                NavTreeNodeSubclassViewMode.class);
        this.subclassViewShowIntermediateNodes = toBooleanOrNull(values.get("subclassViewShowIntermediateNodes"));
        this.subclassFilter = toListOfStrings(toStringOrNull(values.get("subclassFilter")));
        this.subclassDescriptions = map(unflattenMap(values, "subclass")).mapValues(CmStringUtils::toStringOrNull)
                .mapKeys(k -> k.replaceFirst("_description$", "")).immutable();

        // Used in DEPENDENCYTREE trees only
        this.borderSource = toStringOrNull(values.get("borderSource"));
        this.borderColor = toStringOrNull(values.get("borderColor"));
        this.iconSource = toStringOrNull(values.get("iconSource"));
        this.iconFont = toStringOrNull(values.get("iconFont"));
        this.iconColorMode = toStringOrNull(values.get("iconColorMode"));
        this.iconColor = toStringOrNull(values.get("iconColor"));
        this.sourceLookup = toStringOrNull(values.get("sourceLookup"));
        this.sourceLookupAttribute = toStringOrNull(values.get("sourceLookupAttribute"));
    }

    public NavTreeNode toTreeNode(NavTreeType type) {
        NavTreeNodeImpl.NavTreeNodeImplBuilder builder = NavTreeNodeImpl.builder()
                .withId(firstNotBlank(id, randomId()))
                .withTargetFilter(filter)
                .withTargetClassName(targetClass)
                .withTargetClassDescription(description)
                .withDomainName(domain)
                .withDirection(parseDirection(direction) ? RD_DIRECT : RD_INVERSE)
                .withEnableRecursion(recursionEnabled)
                .withShowOnlyOne(showOnlyOne)
                .withSubclassViewMode(subclassViewMode)
                .withSubclassViewShowIntermediateNodes(subclassViewShowIntermediateNodes)
                .withSubclassFilter(subclassFilter)
                .withSubclassDescriptions(subclassDescriptions)
                .withChildNodes(nodes.stream().map(n -> n.toTreeNode(type)).collect(toImmutableList()));

        switch (type) {
            case NT_DEPENDENCYTREE -> {
                builder.withBorderSource(borderSource)
                        .withBorderColor(borderColor)
                        .withIconSource(iconSource)
                        .withIconFont(iconFont)
                        .withIconColorMode(iconColorMode)
                        .withIconColor(iconColor)
                        .withSourceLookup(sourceLookup)
                        .withSourceLookupAttribute(sourceLookupAttribute);
            }
            default -> {
                // nothing further to add;
            }
        } //end switch

        return builder.build();
    }

    private static boolean parseDirection(String direction) {
        return switch (nullToEmpty(direction)) {
            case "_1" -> true;
            case "_2" -> false;
            default -> throw new IllegalArgumentException("invalid direction = " + direction);
        };
    }
}
