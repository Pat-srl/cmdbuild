/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core;

import org.cmdbuild.lookup.LookupType;

/**
 *
 * @author afelice
 */
public class LookupInfo {
    
    LookupType rawLookupType = null;

    public void setType(LookupType lookupType) {
        this.rawLookupType = lookupType;
    }

    public LookupType getType() {
        return rawLookupType;
    }   
    
    public boolean hasType() {
        return rawLookupType != null;
    }
    
}
