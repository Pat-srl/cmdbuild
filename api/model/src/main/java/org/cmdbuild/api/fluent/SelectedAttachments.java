package org.cmdbuild.api.fluent;

public interface SelectedAttachments {

    Iterable<AttachmentDescriptor> selected();

    Iterable<Attachment> download();

    void copyTo(CardDescriptor destination);

    void copyToAndMerge(CardDescriptor destination);

    void moveTo(CardDescriptor destination);

    void delete();

}
