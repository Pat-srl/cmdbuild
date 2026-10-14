/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_PARENT_SERIALIZATION;
import static org.cmdbuild.utils.lang.CmNullableUtils.isBlank;

/**
 * Represents a <i>diff</i>, a inserted/removed {@link Classe}, on modified
 * <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_Classe extends GeneratedDiffSchema_SchemaItem {

    @JsonProperty("class")
    @Override
    public Map<String, Object> getItemProperties() {
        return itemProperties;
    }

    @JsonProperty("class")
    @Override
    public void setItemProperties(Map<String, Object> itemProperties) {
        this.itemProperties = itemProperties;
    }

    @JsonIgnore
    public String getName() {
        return (String) itemProperties.get(ATTR_NAME_SERIALIZATION);
    }

    @JsonIgnore
    public String getParentOrNull() {
        if (isBlank(itemProperties.get(ATTR_PARENT_SERIALIZATION))) {
            return null;
        }

        return (String) itemProperties.get(ATTR_PARENT_SERIALIZATION);
    }

}
