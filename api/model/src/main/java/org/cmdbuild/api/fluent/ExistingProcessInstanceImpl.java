package org.cmdbuild.api.fluent;

import java.util.Collection;
import java.util.Set;
import org.cmdbuild.api.fluent.FluentApiExecutor.AdvanceProcess;

public class ExistingProcessInstanceImpl extends AbstractActiveCard implements ExistingProcessInstance {

    public ExistingProcessInstanceImpl(FluentApiExecutor executor, String className, Long processId) {
        super(executor, className, processId);
    }

    @Override
    public ExistingProcessInstanceImpl withProcessInstanceId(String value) {
        super.set("ProcessCode", value);
        return this;
    }

    @Override
    public ExistingProcessInstanceImpl withDescription(String value) {
        super.setDescription(value);
        return this;
    }

    @Override
    public ExistingProcessInstanceImpl with(String name, Object value) {
        return withAttribute(name, value);
    }

    @Override
    public ExistingProcessInstanceImpl withAttribute(String name, Object value) {
        super.set(name, value);
        return this;
    }

    @Override
    public void update() {
        executor().updateProcessInstance(this, AdvanceProcess.NO);
    }

    @Override
    public void advance() {
        executor().updateProcessInstance(this, AdvanceProcess.YES);
    }

    @Override
    public void suspend() {
        executor().suspendProcessInstance(this);
    }

    @Override
    public void resume() {
        executor().resumeProcessInstance(this);
    }

    @Override
    public Attachments attachments() {
        return new AttachmentsImpl(executor(), this);
    }

    @Override
    public void abort() {
        executor().abortProcessInstance(this);
    }

    @Override
    public Collection<Attachment> getAttachments() {
        return attachments.values();
    }

    @Override
    public ExistingProcessInstance with(Attachment attachment) {
        attachments.put(attachment.getName(), attachment);
        return this;
    }

    @Override
    public void delete() {
        abort();
    }

    @Override
    public ExistingCard limitAttributes(String... names) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> getRequestedAttributes() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Card fetch() {
        throw new UnsupportedOperationException();
    }

}
