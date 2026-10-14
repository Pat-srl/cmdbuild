/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.dataset.data;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.entrytype.Attribute;

/**
 * Represents a dataset, for data only, for a {@link Attribute}
 * 
 * @author afelice
 */
public class AttributeDataset {
    
    private String name;
    
    public boolean writable; 
    
    public String getName() {
        return name;
    }

    public void setName(String name) {        
        this.name = checkNotNull(name);
    }
    
    static AttributeDataset build(String attributeName) {
        AttributeDataset result = new AttributeDataset();
        result.name = attributeName;
        result.writable = true;
        return result;
    }
    
    static AttributeDataset buildReadOnly(String attributeName) {
        AttributeDataset result = new AttributeDataset();
        result.name = attributeName;
        return result;
    }
    
}
