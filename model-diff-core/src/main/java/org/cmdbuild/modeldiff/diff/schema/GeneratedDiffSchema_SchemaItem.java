/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;

/**
 * Placeholder for <i>schema item</i> of type:
 * <ul>
 * <li>{@link Classe};
 * <li>{@link LookupType};
 * <li>{@link Process};
 * <li>{@link Domain};
 * <li><i>dms model</i>;
 * <li><i>dms category</i>.
 * </ul>
 *
 * @author afelice
 */
public class GeneratedDiffSchema_SchemaItem {

    protected Map<String, Object> itemProperties;
    protected List<Map<String, Object>> itemAttributes;

    public Map<String, Object> getItemProperties() {
        return itemProperties;
    }

    public void setItemProperties(Map<String, Object> itemProperties) {
        this.itemProperties = itemProperties;
    }

    @JsonProperty("attributes")
    public List<Map<String, Object>> getItemAttributes() {
        return itemAttributes;
    }

    @JsonProperty("attributes")
    public void setItemAttributes(List<Map<String, Object>> itemAttributes) {
        this.itemAttributes = itemAttributes;
    }

}
