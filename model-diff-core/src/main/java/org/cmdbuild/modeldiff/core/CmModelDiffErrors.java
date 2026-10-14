/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 *
 * @author afelice
 */
public class CmModelDiffErrors extends CmModelDiffError {
    
    @JsonIgnore
    public List<CmModelDiffError> errorColl = list();
    
    public CmModelDiffErrors(String errMSg) {
        super(errMSg);
    }

    public void add(String errMsg) {
        errorColl.add(new CmModelDiffError(errMsg));
    }

    public void concat(CmModelDiffErrors otherExceptions) {
        errorColl.addAll(otherExceptions.errorColl);
    }

    @JsonIgnore
    public boolean isPresent() {
        return !errorColl.isEmpty();
    }

    public int size() {
        return toErrMsgSet().size();
    }
    
    @JsonProperty("errorMsgs")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<String> getErrors() {
        return isPresent() ? list(toErrMsgSet()) : null; // not using toJson() on errorColl directly because CmModelDiffErrors.errMsg is Json ignored to not serialize the CmModelDiffErrors collective message
    }    
    
    private Set<String> toErrMsgSet() {
        return errorColl.stream()
                        .map(e -> e.errMsg) 
                        .collect(toSet());
    }
}
