package org.cmdbuild.workflow.type;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import java.io.Serializable;

import org.cmdbuild.common.annotations.Legacy;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import org.cmdbuild.utils.lang.ToPrimitive;

/**
 * legacy code. Probably serialized around. DO NOT CHANGE CLASS CODE. DO NOT
 * CHANGE CLASS PACKAGE.
 */
@Legacy("Kept for backward compatibility")
public class LookupType implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    int id;
    String type;
    String description;
    String code;

    private Long longId;

    public LookupType() {
        id = -1;
        type = "";
        description = "";
        code = "";
        longId = null;
    }

    public LookupType(int id, String type, String description, String code) {
        super();
        this.id = id;
        this.type = type;
        this.description = description;
        this.code = code;
    }

    public LookupType(long id, String type, String description, String code) {
        super();
        this.id = Integer.MAX_VALUE;
        this.longId = id;
        this.type = type;
        this.description = description;
        this.code = code;
    }

    @ToPrimitive(primary = true)
    public long getId() {
        return firstNonNull(longId, (long) id);
    }

    public void setId(long id) {
        this.id = Integer.MAX_VALUE;
        this.longId = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @ToPrimitive
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean checkValidity() {
        return getId() > 0;
    }

    @Override
    public String toString() {
        return "LookupType{" + "id=" + getId() + ", type=" + type + ", description=" + description + ", code=" + code + '}';
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj != null && obj instanceof LookupType && equal(obj.toString(), this.toString());
    }

    public boolean isEmpty() {
        return isNullOrLtEqZero(getId());
    }
}
