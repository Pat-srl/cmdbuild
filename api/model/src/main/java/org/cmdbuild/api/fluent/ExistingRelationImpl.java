package org.cmdbuild.api.fluent;

public class ExistingRelationImpl extends AbstractActiveRelation implements ExistingRelation {

    ExistingRelationImpl(FluentApiExecutor executor, String domainName) {
        super(executor, domainName);
    }

    @Override
    public ExistingRelationImpl withCard1(String className, long cardId) {
        super.setCard1(className, cardId);
        return this;
    }

    @Override
    public ExistingRelationImpl withCard2(String className, long cardId) {
        super.setCard2(className, cardId);
        return this;
    }

    @Override
    public void delete() {
        executor().delete(this);
    }

}
