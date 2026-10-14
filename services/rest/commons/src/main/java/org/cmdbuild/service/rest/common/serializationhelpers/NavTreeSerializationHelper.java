/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import com.google.common.base.Joiner;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.inner.EcqlExpressionImpl;
import org.cmdbuild.ecql.utils.EcqlUtils;
import org.cmdbuild.navtree.NavTree;
import org.cmdbuild.navtree.NavTreeNode;
import org.cmdbuild.navtree.NavTreeType;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class NavTreeSerializationHelper {

    private final static Map<NavTreeType, NavTreeSpecificSerializer> SPECIFIC_SERIALIZERS = Map.of(
            NavTreeType.NT_DEPENDENCYTREE, new NavTreeSerializationHelper_DependencyTree()
    );

    public enum TreeMode {
        FLAT, TREE
    }

    public static CmMapUtils.FluentMap serializeTree(NavTree root, TreeMode mode,
            ObjectTranslationService translationService) {
        List nodes = switch (mode) {
            case FLAT ->
                root.getData().getThisNodeAndAllDescendants().stream()
                        .map(n -> serializeNode(root, n, translationService)).collect(toList());
            case TREE -> singletonList(serializeNodeAndDescendants(root, root.getData(), translationService));
            default -> throw new IllegalArgumentException();
        };
        CmMapUtils.FluentMap result = map(
                "_id", root.getName(),
                "name", root.getName(),
                "description", root.getDescription(),
                "_description_translation",
                translationService.translateNavtreeDescription(root.getName(), root.getDescription()),
                "active", root.getActive(),
                "type", serializeEnum(root.getType()),
                "nodes", nodes,
                "icon_font", root.getIconFont()
        );

        // Apply potential specific serializer
        fetchSpecificSerializer(root.getType())
                .ifPresent(specificSerializer
                        -> result.putAll(specificSerializer.serializeTreeSimple(root, translationService))
                );

        return result;
    }

    public static CmMapUtils.FluentMap serializeNodeAndDescendants(NavTree tree, NavTreeNode node,
            ObjectTranslationService translationService) {
        return serializeNode(tree, node, translationService).with("nodes", node.getChildNodes().stream()
                .map(n -> serializeNodeAndDescendants(tree, n, translationService)).collect(toList()));
    }

    public static CmMapUtils.FluentMap serializeNode(NavTree tree, NavTreeNode node, ObjectTranslationService translationService) {
        CmMapUtils.FluentMap result = map(
                "_id", node.getId(),
                "filter", node.getTargetFilter(),
                "targetClass", node.getTargetClassName(),
                "recursionEnabled", node.getEnableRecursion(),
                "domain", node.getDomainName(),
                "showOnlyOne", node.getShowOnlyOne(),
                "subclassViewMode", serializeEnum(node.getSubclassViewMode()),
                "subclassViewShowIntermediateNodes", node.getSubclassViewShowIntermediateNodes(),
                "description", node.getTargetClassDescription(),
                "_description_translation", translationService.translateNavtreeItemDescription(tree.getName(), node.getId(), node.getTargetClassDescription())
        ).accept(m -> {
            if (node.hasFilter()) {
                EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(new EcqlExpressionImpl(node.getTargetFilter()));
                m.put("ecqlFilter", map(
                        "id", EcqlUtils.buildNavTreeEcqlId(tree.getName(), node.getId()),
                        "bindings", map("server", ecqlBindingInfo.getServerBindings(), "client", ecqlBindingInfo.getClientBindings())
                ));
            }
            node.getSubclassDescriptions().forEach((k, v) -> {
                m.put(format("subclass_%s_description", k), nullToEmpty(v));
                m.put(format("_subclass_%s_description_translation", k), translationService.translateNavtreeItemSubclassDescription(tree.getName(), node.getId(), k, nullToEmpty(v)));
            });
        }).skipNullValues().with(
                "parent", node.getParentId(),
                "direction", isBlank(node.getDomainName()) ? null : (node.getDirect() ? "_1" : "_2"),
                "subclassFilter", emptyToNull(Joiner.on(",").join(node.getSubclassFilter()))
        ).then();

        // Apply potential specific serializer
        fetchSpecificSerializer(tree.getType())
                .ifPresent(specificSerializer
                        -> result.putAll(specificSerializer.serializeNode(tree, node, translationService))
                );

        return result;
    }

    public static CmMapUtils.FluentMap serializeTreeSimple(NavTree tree, ObjectTranslationService translationService) {
        CmMapUtils.FluentMap result = map(
                "_id", tree.getName(),
                "description", tree.getDescription(),
                "_description_translation",
                translationService.translateNavtreeDescription(tree.getName(), tree.getDescription()),
                "active", tree.getActive(),
                "type", serializeEnum(tree.getType()));

        // Apply potential specific serializer
        fetchSpecificSerializer(tree.getType())
                .ifPresent(specificSerializer
                        -> result.putAll(specificSerializer.serializeTreeSimple(tree, translationService))
                );

        return result;
    }

    private static Optional<NavTreeSpecificSerializer> fetchSpecificSerializer(NavTreeType curNavTreeType) {
        return Optional.ofNullable(SPECIFIC_SERIALIZERS.get(curNavTreeType));
    }

} // end NavTreeSerializationHelper

interface NavTreeSpecificSerializer {
    public CmMapUtils.FluentMap serializeNode(NavTree tree, NavTreeNode node, ObjectTranslationService translationService);

    public CmMapUtils.FluentMap serializeTreeSimple(NavTree tree, ObjectTranslationService translationService);
}

/**
 * Serialized peculiar <i>properties</i> for <code>DEPENDENCYTREE</code>
 * <i>tree</code>.
 *
 * @author afelice
 */
class NavTreeSerializationHelper_DependencyTree implements NavTreeSpecificSerializer {

    @Override
    public CmMapUtils.FluentMap serializeNode(NavTree tree, NavTreeNode node, ObjectTranslationService translationService) {
        return map(

                "borderSource", node.getBorderSource(),
                "borderColor", node.getBorderColor(),
                "iconSource", node.getIconSource(),
                "iconFont", node.getIconFont(),
                "iconColorMode", node.getIconColorMode(),
                "iconColor", node.getIconColor(),
                "sourceLookup", node.getSourceLookup(),
                "sourceLookupAttribute", node.getSourceLookupAttribute()
        );
    }

    @Override
    public CmMapUtils.FluentMap serializeTreeSimple(NavTree tree, ObjectTranslationService translationService) {
        return map(
                "initialExpansionDepth", tree.getMetadata().getInitialExpansionDepth()
        );
    }
} // end NavTreeSerializationHelper_DependencyTree class
