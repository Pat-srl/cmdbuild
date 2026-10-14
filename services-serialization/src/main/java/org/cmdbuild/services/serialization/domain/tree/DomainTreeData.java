/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.services.serialization.domain.tree;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 *
 * @author afelice
 */
public class DomainTreeData {
    @JsonProperty("_id")
    public String id; 
    
    public String name;
    public String description;
    
    @JsonProperty("_description_translation")
    public String descriptionTranslation;
    
    public boolean active;
    public String type;
    public List<DomainNodeData> nodes;
}
