/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;

/**
 * Represents a <i>diff</i>, a inserted/removed {@link Domain}, on modified
 * <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_Domain extends GeneratedDiffSchema_SchemaItem {

    @JsonProperty("domain")
    @Override
    public Map<String, Object> getItemProperties() {
        return itemProperties;
    }

    @JsonProperty("domain")
    @Override
    public void setItemProperties(Map<String, Object> itemProperties) {
        this.itemProperties = itemProperties;
    }

    @JsonIgnore
    public String getName() {
        return (String) itemProperties.get(ATTR_NAME_SERIALIZATION);
    }

}
