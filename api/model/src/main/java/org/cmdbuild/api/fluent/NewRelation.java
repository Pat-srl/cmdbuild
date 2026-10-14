package org.cmdbuild.api.fluent;

public interface NewRelation extends Relation {

    NewRelation withCard1(String className, long cardId);

    NewRelation withCard2(String className, long cardId);

    NewRelation withAttribute(String attributeName, Object attributeValue);

    void create();

    default NewRelation with(String name, Object value) {
        return withAttribute(name, value);
    }
}
