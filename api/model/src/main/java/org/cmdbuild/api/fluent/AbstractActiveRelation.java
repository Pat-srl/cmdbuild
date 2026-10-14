package org.cmdbuild.api.fluent;

public abstract class AbstractActiveRelation extends RelationImpl implements Relation {

    private final FluentApiExecutor executor;

    AbstractActiveRelation(FluentApiExecutor executor, String domainName) {
        super(domainName);
        this.executor = executor;
    }

    protected FluentApiExecutor executor() {
        return executor;
    }

}
