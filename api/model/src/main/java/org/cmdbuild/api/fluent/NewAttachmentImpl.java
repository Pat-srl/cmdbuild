/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.fluent;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;
import java.util.Map;
import static org.cmdbuild.utils.io.CmIoUtils.isUrl;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class NewAttachmentImpl implements NewAttachment {

    private final FluentApiExecutor executor;
    private final CardDescriptor card;

    private final Map<String, Object> meta = map();
    private String name, description, category;
    private Object document;

    public NewAttachmentImpl(FluentApiExecutor executor, CardDescriptor card) {
        this.executor = checkNotNull(executor);
        this.card = checkNotNull(card);
    }

    @Override
    public NewAttachment withName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public NewAttachment withDescription(String description) {
        this.description = description;
        return this;
    }

    @Override
    public NewAttachment withCategory(String category) {
        this.category = category;
        return this;
    }

    @Override
    public NewAttachment withDocument(Object document) {
        this.document = document;
        return this;
    }

    @Override
    public NewAttachment withMeta(Map<String, Object> meta) {
        if (meta != null) {
            this.meta.putAll(meta);
        }
        return this;
    }

    @Override
    public void upload() {
        executor.upload(card, singletonList(document instanceof String && isUrl((String) document) ? AttachmentImpl.fromUrl(name, description, category, (String) document, meta) : AttachmentImpl.fromData(name, description, category, document, meta)));
    }

}
