package org.cmdbuild.api.fluent;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ForwardingObject;

public abstract class ForwardingFluentApiExecutor extends ForwardingObject implements FluentApiExecutor {

    /**
     * Usable by subclasses only.
     */
    protected ForwardingFluentApiExecutor() {
    }

    @Override
    protected abstract FluentApiExecutor delegate();

    @Override
    public CardDescriptor create(final NewCard card) {
        return delegate().create(card);
    }

    @Override
    public void update(final ExistingCard card) {
        delegate().update(card);
    }

    @Override
    public void delete(final ExistingCard card) {
        delegate().delete(card);
    }

    @Override
    public Card fetch(final ExistingCard card) {
        return delegate().fetch(card);
    }

    @Override
    public List<Card> fetchCards(final QueryClass card) {
        return delegate().fetchCards(card);
    }

    @Override
    public void create(final NewRelation relation) {
        delegate().create(relation);
    }

    @Override
    public void delete(final ExistingRelation relation) {
        delegate().delete(relation);
    }

    @Override
    public List<Relation> fetch(final RelationsQuery query) {
        return delegate().fetch(query);
    }

    @Override
    public Map<String, Object> execute(final FunctionCall function) {
        return delegate().execute(function);
    }

    @Override
    public DownloadedReport download(final CreateReport report) {
        return delegate().download(report);
    }

    @Override
    public ProcessInstanceDescriptor createProcessInstance(final NewProcessInstance processCard,
            final AdvanceProcess advance) {
        return delegate().createProcessInstance(processCard, advance);
    }

    @Override
    public void updateProcessInstance(final ExistingProcessInstance processCard, final AdvanceProcess advance) {
        delegate().updateProcessInstance(processCard, advance);
    }

    @Override
    public void suspendProcessInstance(final ExistingProcessInstance processCard) {
        delegate().suspendProcessInstance(processCard);
    }

    @Override
    public void resumeProcessInstance(final ExistingProcessInstance processCard) {
        delegate().resumeProcessInstance(processCard);
    }

    @Override
    public Iterable<Lookup> fetch(final QueryAllLookup queryLookup) {
        return delegate().fetch(queryLookup);
    }

    @Override
    public Lookup fetch(final QuerySingleLookup querySingleLookup) {
        return delegate().fetch(querySingleLookup);
    }

    @Override
    public Iterable<AttachmentDescriptor> fetchAttachments(final CardDescriptor source) {
        return delegate().fetchAttachments(source);
    }

    @Override
    public void upload(final CardDescriptor source, final Iterable<? extends Attachment> attachments) {
        delegate().upload(source, attachments);
    }

    @Override
    public Iterable<Attachment> download(final CardDescriptor source,
            final Iterable<? extends AttachmentDescriptor> attachments) {
        return delegate().download(source, attachments);
    }

    @Override
    public void delete(final CardDescriptor source, final Iterable<? extends AttachmentDescriptor> attachments) {
        delegate().delete(source, attachments);
    }

    @Override
    public void copy(final CardDescriptor source, final Iterable<? extends AttachmentDescriptor> attachments,
            final CardDescriptor destination) {
        delegate().copy(source, attachments, destination);
    }

    @Override
    public void copyAndMerge(final CardDescriptor source, final Iterable<? extends AttachmentDescriptor> attachments,
            final CardDescriptor destination) {
        delegate().copyAndMerge(source, attachments, destination);
    }

    @Override
    public void move(final CardDescriptor source, final Iterable<? extends AttachmentDescriptor> attachments,
            final CardDescriptor destination) {
        delegate().move(source, attachments, destination);
    }

    @Override
    public void abortProcessInstance(final ExistingProcessInstance processCard) {
        delegate().abortProcessInstance(processCard);
    }

}
