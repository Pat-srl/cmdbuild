package org.cmdbuild.api.fluent;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableSet;

import java.util.Set;

import com.google.common.collect.Sets;
import java.util.Collection;

public class ExistingCardImpl extends AbstractActiveCard implements ExistingCard {

    private final Set<String> requestedAttributes;
    private final Set<String> unmodifiableRequestedAttributes; 

    public ExistingCardImpl(FluentApiExecutor executor, String className, Long id) {
        super(executor, className, id);
        requestedAttributes = Sets.newHashSet();
        unmodifiableRequestedAttributes = unmodifiableSet(requestedAttributes); 
    }

    @Override
    public ExistingCardImpl with(String name, Object value) {
        return withAttribute(name, value);
    }

    @Override
    public ExistingCardImpl withAttribute(String name, Object value) {
        super.set(name, value);
        return this;
    }

    @Override
    public ExistingCardImpl limitAttributes(String... names) {
        requestedAttributes.addAll(asList(names));
        return this;
    }

    @Override
    public Set<String> getRequestedAttributes() {
        return unmodifiableRequestedAttributes;
    }

    @Override
    public Collection<Attachment> getAttachments() {
        return attachments.values();
    }

    @Override
    public ExistingCardImpl with(Attachment attachment) {
        attachments.put(attachment.getName(), attachment);
        return this;
    }

    @Override
    public void update() {
        executor().update(this);
    }

    @Override
    public void delete() {
        executor().delete(this);
    }

    @Override
    public Card fetch() {
        return executor().fetch(this);
    }

    @Override
    public Attachments attachments() {
        return new AttachmentsImpl(executor(), this);
    }

}
