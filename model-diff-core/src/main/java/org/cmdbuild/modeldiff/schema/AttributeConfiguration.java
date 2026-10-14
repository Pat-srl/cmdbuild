/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;

/**
 * Represents an item attribute configuration, for schema, for any attribute
 * contained in a <i>schema item</i>.
 *
 * @author afelice
 */
public class AttributeConfiguration {
    
    private String name;
    private Map<String, Object> cmdbSerialization;    
    
    public boolean writable;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {        
        this.name = checkNotNull(name);
    }
    
    public void setCmdbSerialization(Map<String, Object> cmdbSerialization) {    
        this.cmdbSerialization = cmdbSerialization;
    }
    
    public Map<String, Object> getCmdbSerialization() {
        return cmdbSerialization;
    }

}
