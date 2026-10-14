package org.cmdbuild.api.fluent;

import com.google.common.collect.ForwardingObject;

public abstract class ForwardingFluentApi extends ForwardingObject implements FluentApi {

    /**
     * Usable by subclasses only.
     */
    protected ForwardingFluentApi() {
    }

    @Override
    protected abstract FluentApi delegate();

    @Override
    public NewCard newCard(final String className) {
        return delegate().newCard(className);
    }

    @Override
    public ExistingCard existingCard(final CardDescriptor descriptor) {
        return delegate().existingCard(descriptor);
    }

    @Override
    public ExistingCard existingCard(final String className, final long id) {
        return delegate().existingCard(className, id);
    }

    @Override
    public NewRelation newRelation(final String domainName) {
        return delegate().newRelation(domainName);
    }

    @Override
    public ExistingRelation existingRelation(final String domainName) {
        return delegate().existingRelation(domainName);
    }

    @Override
    public QueryClass queryClass(final String className) {
        return delegate().queryClass(className);
    }

    @Override
    public FunctionCall callFunction(final String functionName) {
        return delegate().callFunction(functionName);
    }

    @Override
    public CreateReport createReport(final String title, final String format) {
        return delegate().createReport(title, format);
    }

    @Override
    public ActiveQueryRelations queryRelations(final CardDescriptor descriptor) {
        return delegate().queryRelations(descriptor);
    }

    @Override
    public ActiveQueryRelations queryRelations(final String className, final long id) {
        return delegate().queryRelations(className, id);
    }

    @Override
    public NewProcessInstance newProcessInstance(final String processClassName) {
        return delegate().newProcessInstance(processClassName);
    }

    @Override
    public ExistingProcessInstance existingProcessInstance(final String processClassName, final long processId) {
        return delegate().existingProcessInstance(processClassName, processId);
    }

    @Override
    public QueryAllLookup queryLookup(final String type) {
        return delegate().queryLookup(type);
    }

}
