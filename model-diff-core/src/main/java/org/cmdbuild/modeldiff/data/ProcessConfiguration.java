/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Represents {@link Process} model configuration.
 *
 * <p>
 * create/delete and modification of related {@link Flow}s are always disabled,
 * so are read-only.
 *
 * @author afelice
 */
public class ProcessConfiguration extends ClasseConfiguration {

    /**
     * 
     * @param name {@link Process} name.
     */
    public ProcessConfiguration(String name) {
        super(name);
    }
    
    @JsonProperty("process")
    @Override
    public void setCmdbSerialization(Map<String, Object> cmdbSerialization) {    
        super.setCmdbSerialization(cmdbSerialization);

        // Make read-only
        overrideCanWriteCards(false);
    }
    
    @JsonProperty("process")
    @Override
    public Map<String, Object> getCmdbSerialization() {
        return super.getCmdbSerialization();
    }

    @Override
    public void overrideCanWriteCards(boolean writable) {
        overrideCanDoOnCard(CLASS_CAN_CREATE_CARDS_GRANT, false);
        overrideCanDoOnCard(CLASS_CAN_UPDATE_CARDS_GRANT, false);
        overrideCanDoOnCard(CLASS_CAN_CLONE_CARDS_GRANT, false);
        overrideCanDoOnCard(CLASS_CAN_DELETE_CARDS_GRANT, false);
        overrideCanDoOnCard(CLASS_CAN_MODIFY_CARDS_GRANT, false); // represents structural modification, not allowed
    }

}
