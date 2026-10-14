package org.cmdbuild.api.fluent;

public interface ExistingRelation extends Relation {

    ExistingRelation withCard1(String className, long cardId);

    ExistingRelation withCard2(String className, long cardId);

    void delete();

}
