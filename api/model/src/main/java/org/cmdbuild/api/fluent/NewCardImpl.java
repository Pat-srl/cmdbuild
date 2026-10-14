package org.cmdbuild.api.fluent;

public class NewCardImpl extends AbstractActiveCard implements NewCard {

    public NewCardImpl(FluentApiExecutor executor, String className) {
        super(executor, className, null);
    }

    @Override
    public NewCardImpl withCode(String value) {
        super.setCode(value);
        return this;
    }

    @Override
    public NewCardImpl withDescription(String value) {
        super.setDescription(value);
        return this;
    }

    @Override
    public NewCardImpl with(String name, Object value) {
        return withAttribute(name, value);
    }

    @Override
    public NewCardImpl withAttribute(String name, Object value) {
        super.set(name, value);
        return this;
    }

    @Override
    public CardDescriptor create() {
        return executor().create(this);
    }

}
