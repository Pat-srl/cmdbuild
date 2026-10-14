package org.cmdbuild.api.fluent;

public interface NewProcessInstance extends Card {

    NewProcessInstance withDescription(String value);

    NewProcessInstance with(String name, Object value);

    NewProcessInstance withAttribute(String name, Object value);

    ProcessInstanceDescriptor start();

    ProcessInstanceDescriptor startAndAdvance();
}
