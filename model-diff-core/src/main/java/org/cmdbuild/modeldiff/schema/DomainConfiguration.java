/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Domain;

/**
 * Represents {@link Domain} model configuration.
 *
 * @author afelice
 */
public class DomainConfiguration extends SchemaItemConfiguration {

    @JsonCreator
    public DomainConfiguration() {
        super();
    }    
    
    /**
     *
     * @param name {@link Domain} name.
     */
    public DomainConfiguration(String name) {
        super(name);
    }

    @Override
    @JsonProperty("domain")
    public void setCmdbSerialization(Map<String, Object> cmdbSerialization) {
        super.setCmdbSerialization(cmdbSerialization);
    }

    @Override
    @JsonProperty("domain")
    public Map<String, Object> getCmdbSerialization() {
        return super.getCmdbSerialization();
    }

}
