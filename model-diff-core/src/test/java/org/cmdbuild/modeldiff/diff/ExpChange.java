/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff;

/**
 *
 * @author afelice
 */
public class ExpChange {

    public String expName;
    public Object expPrevValue;
    public Object expNewValue;
    
    public ExpChange(String expName, Object expPrevValue, Object expNewValue) {
        this.expName = expName;
        this.expPrevValue = expPrevValue;
        this.expNewValue = expNewValue;
    }
}
