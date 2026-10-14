/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.fluent;

import java.util.List;
import java.util.Map;

public enum DummyFluentApiExecutor implements FluentApiExecutor {
    INSTANCE;

    @Override
    public CardDescriptor create(NewCard card) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void update(ExistingCard card) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void delete(ExistingCard card) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public Card fetch(ExistingCard card) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public List<Card> fetchCards(QueryClass card) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void create(NewRelation relation) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void delete(ExistingRelation relation) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public List<Relation> fetch(RelationsQuery query) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public Map<String, Object> execute(FunctionCall function) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public DownloadedReport download(CreateReport report) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public ProcessInstanceDescriptor createProcessInstance(NewProcessInstance processCard, FluentApiExecutor.AdvanceProcess advance) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void updateProcessInstance(ExistingProcessInstance processCard, FluentApiExecutor.AdvanceProcess advance) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void suspendProcessInstance(ExistingProcessInstance processCard) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void resumeProcessInstance(ExistingProcessInstance processCard) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public Iterable<Lookup> fetch(QueryAllLookup queryLookup) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public Lookup fetch(QuerySingleLookup querySingleLookup) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public Iterable<AttachmentDescriptor> fetchAttachments(CardDescriptor source) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void upload(CardDescriptor source, Iterable<? extends Attachment> attachments) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public Iterable<Attachment> download(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void delete(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void copy(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void copyAndMerge(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void move(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }

    @Override
    public void abortProcessInstance(ExistingProcessInstance processCard) {
        throw new UnsupportedOperationException("Dummy Executor, operation not supported");
    }
}
