/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.services.serialization.domain.tree;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;

/**
 *
 * @author afelice
 */
public class DomainNodeData {
    @JsonProperty("_id")
    public String id;

    /**
     * <b>Note</b>: empty in <i>root node</i>.
     */
    public String domain;
    public String description;
    @JsonProperty("_description_translation")
    public String descriptionTranslation;

    public String targetClass;

    @JsonProperty("filter")
    public String cqlFilter = "";

    /**
     * Used internally by DomainPathFinder algorithm
     */
    @JsonIgnore
    public transient String asLeafFilter;

    /**
     * <b>Note</b>: empty in <i>root node</i>.
     */
    public String parent;
    /**
     * <b>Note</b>: empty in <i>root node</i>.
     */
    public String direction;

    @JsonIgnore
    public boolean isRoot() {
        return isBlank(domain);
    }

    @JsonIgnore
    public String getTargetClasseName() {
        return targetClass;
    }
}
