package org.cmdbuild.api.fluent;

public class NewRelationImpl extends AbstractActiveRelation implements NewRelation {

    NewRelationImpl(FluentApiExecutor executor, String domainName) {
        super(executor, domainName);
    }

    @Override
    public NewRelationImpl withCard1(String className, long cardId) {
        super.setCard1(className, cardId);
        return this;
    }

    @Override
    public NewRelationImpl withCard2(String className, long cardId) {
        super.setCard2(className, cardId);
        return this;
    }

    @Override
    public NewRelationImpl withAttribute(String attributeName, Object attributeValue) {
        super.setAttribute(attributeName, attributeValue);
        return this;
    }

    @Override
    public void create() {
        executor().create(this);
    }

}
