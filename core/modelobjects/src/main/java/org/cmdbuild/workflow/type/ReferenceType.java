package org.cmdbuild.workflow.type;

import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Objects.equal;
import java.io.Serializable;
import static java.lang.Math.toIntExact;
import jakarta.annotation.Nullable;

import org.cmdbuild.common.annotations.Legacy;
import org.cmdbuild.utils.lang.ToPrimitive;

/**
 * legacy code. Serialized around  within shark data. DO NOT CHANGE CLASS CODE. DO NOT
 * CHANGE CLASS PACKAGE.
 */
@Legacy("Kept for backward compatibility")
public class ReferenceType implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int idClass;
    private String description;

    private Long longId;
    private String code;
    private String className;

    public ReferenceType() {
        id = -1;
        idClass = -1;
        description = "";
        longId = null;
    }

    /**
     * @deprecated use constructor with String className and long id, not int idClass and int id
     */
    @Deprecated
    public ReferenceType(int id, int idClass, String description) {
        super();
        this.id = id;
        this.idClass = idClass;
        this.description = description;
    }

    /**
     * @deprecated use constructor with String className and long id, not int idClass and int id
     */
    @Deprecated
    public ReferenceType(long id, int idClass, String description) {
        this(id, idClass, description, null);
    }

    /**
     * @deprecated use constructor with String className and long id, not int idClass and int id
     */
    @Deprecated
    public ReferenceType(long id, int idClass, String description, String code) {
        super();
        this.id = Integer.MAX_VALUE;
        this.longId = id;
        this.idClass = idClass;
        this.description = description;
        this.code = code;
    }

    public ReferenceType(String className, long cardId) {
        this(className, cardId, null, null);
    }

    public ReferenceType(String className, long cardId, @Nullable String description, @Nullable String code) {
        this.id = Integer.MAX_VALUE;
        this.longId = cardId;
        this.className = className;
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

    @Nullable
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * @deprecated use String className
     */
    @Deprecated
    public int getIdClass() {
        return idClass;
    }

    /**
     * @deprecated use String className
     */
    @Deprecated
    public void setIdClass(long idClass) {
        setIdClass(toIntExact(idClass));
    }

    /**
     * @deprecated use String className
     */
    @Deprecated
    public void setIdClass(int idClass) {
        this.idClass = idClass;
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

    /**
     * @deprecated use isEmpty(), isNotEmpty()
     */
    @Deprecated
    public boolean checkValidity() {
        return !isEmpty();
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        return getId() <= 0;
    }

    @Override
    public String toString() {
        return "ReferenceType{" + "id=" + getId() + ", type=" + className + ", description=" + description + ", code=" + code + '}';
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && obj instanceof ReferenceType && equal(((ReferenceType) obj).getId(), this.getId());
    }
}
