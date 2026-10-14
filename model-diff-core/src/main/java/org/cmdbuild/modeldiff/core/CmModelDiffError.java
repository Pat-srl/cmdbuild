/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 *
 * @author afelice
 */
public class CmModelDiffError {
        
    @JsonIgnore
    protected final String errMsg; 
    
    public CmModelDiffError(String errMsg) {
        this.errMsg = errMsg;
    }
    
    @JsonIgnore
    public String getErrMsg() {
        return errMsg;
    }
}
