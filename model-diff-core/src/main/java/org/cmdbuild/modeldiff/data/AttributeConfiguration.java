/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Map;
import org.cmdbuild.dao.entrytype.Attribute;

/**
 * Represents an attribute configuration, for data only, for a
 * {@link Attribute}
 *
 * @author afelice
 */
public class AttributeConfiguration {

    /**
     * Represents ability to modify an attribute.
     */
    public static final String ATTRIBUTE_WRITABLE_FLAG = "writable";

    /**
     * Represents an attribute which value can be set once, but not more
     * modifiable.
     */
    public static final String ATTRIBUTE_IMMUTABLE_FLAG = "immutable";

    /**
     * Represents grant to create a card for this class.
     */
    public static final String ATTRIBUTE_CAN_CREATE_GRANT = "_can_create";
    /**
     * Represents grant to update a card for this class.
     */
    public static final String ATTRIBUTE_CAN_UPDATE_GRANT = "_can_update";

    /**
     * Represents structural modification of attribute
     */
    public static final String ATTRIBUTE_CAN_MODIFY_GRANT = "_can_modify";

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

    public void overrideCanWriteAttrib(boolean writable) {
        overrideCanDoOnAttribute(ATTRIBUTE_WRITABLE_FLAG, writable);
        // ATTRIBUTE_IMMUTABLE_FLAG represents set once, not not allowed here to alter it
        overrideCanDoOnAttribute(ATTRIBUTE_CAN_CREATE_GRANT, writable);
        overrideCanDoOnAttribute(ATTRIBUTE_CAN_UPDATE_GRANT, writable);
        overrideCanDoOnAttribute(ATTRIBUTE_CAN_MODIFY_GRANT, writable); // represents structural modification, not allowed here to alter it
    }

    private Object overrideCanDoOnAttribute(String grant, boolean writable) {
        return cmdbSerialization.put(grant, (boolean) cmdbSerialization.get(grant) && writable);
    }
}
