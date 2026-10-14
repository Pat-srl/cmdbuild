/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import static org.cmdbuild.modeldiff.core.CmSerializationHelper.ATTR_NAME_SERIALIZATION;

/**
 * Represents a <i>diff</i>, a inserted/removed <i>dms model</i> (a special type
 * of {@link Classe}), on modified <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_DmsModel extends GeneratedDiffSchema_Classe {

    @JsonProperty("dmsModel")
    @Override
    public Map<String, Object> getItemProperties() {
        return itemProperties;
    }

    @JsonProperty("dmsModel")
    @Override
    public void setItemProperties(Map<String, Object> itemProperties) {
        this.itemProperties = itemProperties;
    }

    @JsonIgnore
    public String getName() {
        return (String) itemProperties.get(ATTR_NAME_SERIALIZATION);
    }

}
