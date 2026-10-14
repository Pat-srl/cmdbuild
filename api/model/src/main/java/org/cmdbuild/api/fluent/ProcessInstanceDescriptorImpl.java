package org.cmdbuild.api.fluent;

public class ProcessInstanceDescriptorImpl extends CardDescriptorImpl implements ProcessInstanceDescriptor {

    private final String processInstanceId;

    public ProcessInstanceDescriptorImpl(String className, Long id, String processInstanceId) {
        super(className, id);
        this.processInstanceId = processInstanceId;
    }

    @Override
    public String getProcessInstanceId() {
        return processInstanceId;
    }

}
