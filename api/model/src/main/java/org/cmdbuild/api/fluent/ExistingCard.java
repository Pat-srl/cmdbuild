package org.cmdbuild.api.fluent;

import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;

public interface ExistingCard<T extends ExistingCard> extends Card {

    T with(String name, Object value);

    T withAttribute(String name, Object value);

    @Deprecated
    ExistingCard limitAttributes(String... names);

    @Deprecated
    Set<String> getRequestedAttributes();

    Collection<Attachment> getAttachments();

    T with(Attachment attachment);

    void update();

    void delete();

    Card fetch();

    Attachments attachments();

    default T withDescription(String value) {
        return this.withAttribute(ATTR_DESCRIPTION, value);
    }

    default T withCode(String value) {
        return this.withAttribute(ATTR_CODE, value);
    }

    default NewAttachment newAttachment() {
        return attachments().newAttachment();
    }

    default NewAttachment newAttachment(String name, String description, String category) {
        return newAttachment().withName(name).withDescription(description).withCategory(category);
    }

    default NewAttachment newAttachment(String name, String description, String category, Object document) {
        return newAttachment().withName(name).withDescription(description).withCategory(category).withDocument(document);
    }

    default NewAttachment newAttachment(String name, String description, String category, Object document, Object... keyValues) {
        return newAttachment().withName(name).withDescription(description).withCategory(category).withDocument(document).withMeta(keyValues);
    }

    default T withAttachment(String url, String fileName, String documentCategory, String description) {
        return with(AttachmentImpl.fromUrl(fileName, description, documentCategory, url, emptyMap()));
    }

    default T withAttachment(String url, String fileName, String documentCategory, String description, Map<String, Object> meta) {
        return with(AttachmentImpl.fromUrl(fileName, description, documentCategory, url, meta));
    }
}
