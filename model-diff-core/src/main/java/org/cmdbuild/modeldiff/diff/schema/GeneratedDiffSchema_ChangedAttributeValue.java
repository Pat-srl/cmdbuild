/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.dao.entrytype.Attribute;

/**
 * Represents a <i>diff</i>, a changed {@link Attribute} single value, on
 * modified
 * <i>schema</i>.
 *
 * @author afelice
 */
public class GeneratedDiffSchema_ChangedAttributeValue {

    @JsonProperty("attribute")
    public String attribName;
    
    public Object oldValue;
    public Object newValue;
}
