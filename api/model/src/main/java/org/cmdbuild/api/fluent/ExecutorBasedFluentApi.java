package org.cmdbuild.api.fluent;

import static com.google.common.base.Preconditions.checkNotNull;

public class ExecutorBasedFluentApi implements FluentApi {

    private final FluentApiExecutor executor;

    public ExecutorBasedFluentApi(FluentApiExecutor executor) {
        this.executor = checkNotNull(executor);
    }

    @Override
    public NewCard newCard(String className) {
        return new NewCardImpl(executor, className);
    }

    @Override
    public ExistingCard existingCard(CardDescriptor descriptor) {
        return new ExistingCardImpl(executor, descriptor.getClassName(), descriptor.getId());
    }

    @Override
    public ExistingCard existingCard(String className, long id) {
        return new ExistingCardImpl(executor, className, id);
    }

    @Override
    public NewRelation newRelation(String domainName) {
        return new NewRelationImpl(executor, domainName);
    }

    @Override
    public ExistingRelation existingRelation(String domainName) {
        return new ExistingRelationImpl(executor, domainName);
    }

    @Override
    public QueryClass queryClass(String className) {
        return new QueryClassImpl(executor, className);
    }

    @Override
    public FunctionCall callFunction(String functionName) {
        return new FunctionCallImpl(executor, functionName);
    }

    @Override
    public CreateReport createReport(String title, String format) {
        return new CreateReport(executor, title, format);
    }

    @Override
    public ActiveQueryRelations queryRelations(CardDescriptor descriptor) {
        return new ActiveQueryRelations(executor, descriptor.getClassName(), descriptor.getId());
    }

    @Override
    public ActiveQueryRelations queryRelations(String className, long id) {
        return new ActiveQueryRelations(executor, className, id);
    }

    @Override
    public NewProcessInstance newProcessInstance(String processClassName) {
        return new NewProcessInstanceImpl(executor, processClassName);
    }

    @Override
    public ExistingProcessInstance existingProcessInstance(String processClassName, long processId) {
        return new ExistingProcessInstanceImpl(executor, processClassName, processId);
    }

    @Override
    public QueryAllLookup queryLookup(String type) {
        return new QueryAllLookup(executor, type);
    }
}
