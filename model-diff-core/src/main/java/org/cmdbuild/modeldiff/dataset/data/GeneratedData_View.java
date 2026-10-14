/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

/**
 * Represents generated JSON for a {@link Process}, contains the list of
 * attribute values.
 * * 
 * @author afelice
 */
public class GeneratedData_View extends GeneratedData_Classe {

    @JsonProperty("view")
    @Override
    public String getName() {
        return name;
    }

    @JsonProperty("view")
    @Override
    public void setName(String classe) {
        this.name = classe;
    }

    @JsonProperty("cards")
    @Override
    public List<Map<String, Object>> getValues() {
        return values;
    }

    @JsonProperty("cards")
    @Override
    public void setValues(List<Map<String, Object>> values) {
        this.values = values;
    }
}
