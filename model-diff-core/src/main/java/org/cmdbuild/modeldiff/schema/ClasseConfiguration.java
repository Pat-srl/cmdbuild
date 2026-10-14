/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Classe;

/**
 * Represents {@link Classe} model configuration.
 *
 * @author afelice
 */
public class ClasseConfiguration extends SchemaItemConfiguration {

    @JsonCreator
    public ClasseConfiguration() {
        super();
    }

    /**
     *
     * @param name {@link Classe} name. Is <i>Json ignored</i>, can't be in
     * <i>Json creator</i>.
     */
    public ClasseConfiguration(String name) {
        super(name);
    }

    @Override
    @JsonProperty("class")
    public void setCmdbSerialization(Map<String, Object> cmdbSerialization) {
        super.setCmdbSerialization(cmdbSerialization);
    }

    @Override
    @JsonProperty("class")
    public Map<String, Object> getCmdbSerialization() {
        return super.getCmdbSerialization();
    }

}
