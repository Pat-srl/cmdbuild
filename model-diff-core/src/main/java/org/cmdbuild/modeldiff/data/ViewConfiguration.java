/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Represents {@link View} model configuration.
 *
 * <p>
 * create/delete and modification of related {@link Card}s are always disabled,
 * so are read-only.
 *
 * @author afelice
 */
public class ViewConfiguration extends ClasseConfiguration {

    /**
     * 
     * @param name {@link View} name.
     */
    public ViewConfiguration(String name) {
        super(name);
    }
    
    @JsonProperty("view")
    @Override
    public void setCmdbSerialization(Map<String, Object> cmdbSerialization) {    
        super.setCmdbSerialization(cmdbSerialization);

        // Make read-only
        overrideCanWriteCards(false);
    }
    
    @JsonProperty("view")
    @Override
    public Map<String, Object> getCmdbSerialization() {
        return super.getCmdbSerialization();
    }

    @Override
    @JsonIgnore
    public String getFilter() {
        return "";
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
