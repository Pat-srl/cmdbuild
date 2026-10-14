package org.cmdbuild.api.fluent;

import java.util.List;
import java.util.Map;

public interface FluentApiExecutor {

    CardDescriptor create(NewCard card);

    void update(ExistingCard card);

    void delete(ExistingCard card);

    Card fetch(ExistingCard card);

    List<Card> fetchCards(QueryClass card);

    void create(NewRelation relation);

    void delete(ExistingRelation relation);

    List<Relation> fetch(RelationsQuery query);

    Map<String, Object> execute(FunctionCall function);

    DownloadedReport download(CreateReport report);

    ProcessInstanceDescriptor createProcessInstance(NewProcessInstance processCard, AdvanceProcess advance);

    void updateProcessInstance(ExistingProcessInstance processCard, AdvanceProcess advance);

    void suspendProcessInstance(ExistingProcessInstance processCard);

    void resumeProcessInstance(ExistingProcessInstance processCard);

    Iterable<Lookup> fetch(QueryAllLookup queryLookup);

    Lookup fetch(QuerySingleLookup querySingleLookup);

    Iterable<AttachmentDescriptor> fetchAttachments(CardDescriptor source);

    void upload(CardDescriptor source, Iterable<? extends Attachment> attachments);

    Iterable<Attachment> download(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments);

    void delete(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments);

    void copy(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination);

    void copyAndMerge(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination);

    void move(CardDescriptor source, Iterable<? extends AttachmentDescriptor> attachments, CardDescriptor destination);

    void abortProcessInstance(ExistingProcessInstance processCard);

    enum AdvanceProcess {
        YES, NO
    }
}
