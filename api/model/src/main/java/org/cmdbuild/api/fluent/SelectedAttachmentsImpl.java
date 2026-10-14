package org.cmdbuild.api.fluent;

import static com.google.common.collect.FluentIterable.from;

import com.google.common.base.Predicate;

class SelectedAttachmentsImpl implements SelectedAttachments {

    private final FluentApiExecutor executor;
    private final CardDescriptor descriptor;
    private final Predicate<AttachmentDescriptor> predicate;

    SelectedAttachmentsImpl(final FluentApiExecutor executor, final CardDescriptor descriptor,
            final Predicate<AttachmentDescriptor> predicate) {
        this.executor = executor;
        this.descriptor = descriptor;
        this.predicate = predicate;
    }

    @Override
    public Iterable<AttachmentDescriptor> selected() {
        return from(executor.fetchAttachments(descriptor)) //
                .filter(predicate);
    }

    @Override
    public Iterable<Attachment> download() {
        return executor.download(this.descriptor, selected());
    }

    @Override
    public void copyTo(final CardDescriptor destination) {
        executor.copy(this.descriptor, selected(), destination);
    }

    @Override
    public void copyToAndMerge(final CardDescriptor destination) {
        executor.copyAndMerge(this.descriptor, selected(), destination);
    }

    @Override
    public void moveTo(final CardDescriptor destination) {
        executor.move(this.descriptor, selected(), destination);
    }

    @Override
    public void delete() {
        executor.delete(this.descriptor, selected());
    }

}
