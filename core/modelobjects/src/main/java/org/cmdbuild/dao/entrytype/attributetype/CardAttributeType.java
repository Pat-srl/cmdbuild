package org.cmdbuild.dao.entrytype.attributetype;

import static com.google.common.base.Objects.equal;

public interface CardAttributeType<T> {

    AttributeTypeName getName();

    void accept(CMAttributeTypeVisitor visitor);

    default <A extends CardAttributeType> A as(Class<A> classe) {
        return classe.cast(this);
    }

    default boolean isOfType(AttributeTypeName type) {
        return equal(type, getName());
    }
}
