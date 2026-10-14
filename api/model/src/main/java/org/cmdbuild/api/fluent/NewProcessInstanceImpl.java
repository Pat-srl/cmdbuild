package org.cmdbuild.api.fluent;

import org.cmdbuild.api.fluent.FluentApiExecutor.AdvanceProcess;

public class NewProcessInstanceImpl extends AbstractActiveCard implements NewProcessInstance {

    NewProcessInstanceImpl(FluentApiExecutor executor, String className) {
        super(executor, className, null);
    }

    @Override
    public NewProcessInstanceImpl withDescription(String value) {
        super.setDescription(value);
        return this;
    }

    @Override
    public NewProcessInstanceImpl with(String name, Object value) {
        return withAttribute(name, value);
    }

    @Override
    public NewProcessInstanceImpl withAttribute(String name, Object value) {
        super.set(name, value);
        return this;
    }

    @Override
    public ProcessInstanceDescriptor start() {
        return executor().createProcessInstance(this, AdvanceProcess.NO);
    }

    @Override
    public ProcessInstanceDescriptor startAndAdvance() {
        return executor().createProcessInstance(this, AdvanceProcess.YES);
    }
}
