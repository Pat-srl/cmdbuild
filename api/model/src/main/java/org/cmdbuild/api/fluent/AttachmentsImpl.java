package org.cmdbuild.api.fluent;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.alwaysTrue;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.util.Collection;

import com.google.common.base.Predicate;
import static java.util.Collections.emptyMap;
import static org.cmdbuild.utils.io.CmIoUtils.isUrl;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class AttachmentsImpl implements Attachments {

    private final FluentApiExecutor executor;
    private final CardDescriptor descriptor;

    AttachmentsImpl(FluentApiExecutor executor, CardDescriptor descriptor) {
        this.executor = checkNotNull(executor);
        this.descriptor = checkNotNull(descriptor);
    }

    @Override
    public Iterable<AttachmentDescriptor> fetch() {
        return executor.fetchAttachments(descriptor);
    }

    @Override
    public void upload(Attachment... attachments) {
        executor.upload(descriptor, newArrayList(defaultIfNull(attachments, new Attachment[]{})));
    }

    @Override
    public void upload(String name, String description, String category, Object document) {
        Attachment attachment = document instanceof String && isUrl((String) document)
                ? AttachmentImpl.fromUrl(name, description, category, (String) document, emptyMap())
                : AttachmentImpl.fromData(name, description, category, document, emptyMap());
        executor.upload(descriptor, newArrayList(attachment));
    }

    @Override
    public SelectedAttachments selectByName(String... names) {
        return new SelectedAttachmentsImpl(executor, descriptor, new NamePredicate(newArrayList(defaultIfNull(names, new String[]{}))));
    }

    @Override
    public SelectedAttachments selectByName(String names) {
        return new SelectedAttachmentsImpl(executor, descriptor, new NamePredicate(list(names)));
    }

    @Override
    public SelectedAttachments selectAll() {
        return new SelectedAttachmentsImpl(executor, descriptor, alwaysTrue());
    }

    @Override
    public NewAttachment newAttachment() {
        return new NewAttachmentImpl(executor, descriptor);
    }

    private static class NamePredicate implements Predicate<AttachmentDescriptor> {

        private final Collection<String> allowed;

        public NamePredicate(Iterable<String> names) {
            allowed = newArrayList(names);
        }

        @Override
        public boolean apply(AttachmentDescriptor input) {
            return allowed.contains(input.getName());
        }

    }
}
