package org.cmdbuild.api.fluent;

import static com.google.common.base.Objects.equal;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class CardDescriptorImpl implements CardDescriptor {

    private final String className;
    private final Long id;

    private final transient Integer hashCode;
    private final transient String toString;

    public CardDescriptorImpl(final String className, final Long id) {
        this.className = className;
        this.id = id;

        this.hashCode = new HashCodeBuilder() //
                .append(className) //
                .append(id) //
                .hashCode();
        this.toString = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE) //
                .append("classname", className) //
                .append("id", id) //
                .toString();
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(final Object object) {
        if (object == this) {
            return true;
        }
        if (!(object instanceof CardDescriptorImpl)) {
            return false;
        }
        final CardDescriptorImpl descriptor = CardDescriptorImpl.class.cast(object);
        return (className.equals(descriptor.className) && equal(id, descriptor.id));
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return toString;
    }

}
