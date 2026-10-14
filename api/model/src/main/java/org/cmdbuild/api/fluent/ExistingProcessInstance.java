package org.cmdbuild.api.fluent;

public interface ExistingProcessInstance extends ExistingCard<ExistingProcessInstance> {

    ExistingProcessInstance withProcessInstanceId(String value);

    void advance();

    void suspend();

    void resume();

    void abort();

}
