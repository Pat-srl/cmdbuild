/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import org.cmdbuild.navtree.NavTreeImpl;
import org.cmdbuild.navtree.NavTreeMetadataImpl;
import org.cmdbuild.navtree.NavTreeType;
import static org.cmdbuild.navtree.NavTreeType.NT_DEFAULT;
import static org.cmdbuild.navtree.NavTreeType.NT_DEPENDENCYTREE;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 *
 * @author schursin
 */
public class WsTreeData {

    private final String name, description, iconFont;
    private final WsTreeNodeData data;
    private final boolean active;
    private final NavTreeType type;

    // Used in DEPENDENCYTREE trees only
    private final Integer initialExpansionDepth;

    public WsTreeData(@JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("nodes") List<WsTreeNodeData> nodes,
            @JsonProperty("active") boolean active,
            @JsonProperty("type") String type,
            @JsonProperty("initialExpansionDepth") Integer initialExpansionDepth, // used only in trees NavTreeType.NT_DEPENDENCYTREE
            @JsonProperty("icon_font") String iconFont) {
        this.name = checkNotBlank(name, "nav tree name cannot be null");
        this.description = nullToEmpty(description);
        this.data = checkNotNull(getOnlyElement(nodes, null), "a nav tree must have a root node");
        this.active = active;
        this.type = parseEnumOrDefault(type, NT_DEFAULT);
        this.iconFont = iconFont;

        // Used in DEPENDENCYTREE trees only
        this.initialExpansionDepth = initialExpansionDepth;
    }

    public NavTreeImpl.NavTreeDataImplBuilder toTreeNode() {
        NavTreeImpl.NavTreeDataImplBuilder builder = NavTreeImpl.builder()
                .withName(name)
                .withDescription(description)
                .withData(data.toTreeNode(type))
                .withActive(active)
                .withType(type)
                .withIconFont(iconFont);

        switch (type) {
            case NT_DEPENDENCYTREE -> {
                // From additional property to metadata property
                builder.withMetadataAsObj(
                        NavTreeMetadataImpl.builder()
                                .withInitialExpansionDepth(initialExpansionDepth).build()
                );
            }
            default -> {
                // nothing to add in metadata;
            }
        } //end switch

        return builder;
    }
}
