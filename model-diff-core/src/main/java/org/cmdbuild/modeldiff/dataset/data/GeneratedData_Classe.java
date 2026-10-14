/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;

/**
 * Represents generated JSON for a {@link Classe}, contains the list of attribute values.
 * 
 * @author afelice
 */
public class GeneratedData_Classe extends GeneratedData_Item {

    @JsonProperty("classe")
    public String getName() {
        return name;
    }

    @JsonProperty("classe")
    public void setName(String classe) {
        this.name = classe;
    }

    @JsonProperty("cards")
    public List<Map<String, Object>> getValues() {
        return values;
    }

    @JsonProperty("cards")
    public void setValues(List<Map<String, Object>> values) {
        this.values = values;
    }

}
