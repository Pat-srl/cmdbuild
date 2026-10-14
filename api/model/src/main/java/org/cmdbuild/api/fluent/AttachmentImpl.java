package org.cmdbuild.api.fluent;

import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class AttachmentImpl implements Attachment {

    private final String url;
    private final String name;
    private final String category;
    private final String description;
    private final Object data;
    private final Map<String, Object> meta;

    private AttachmentImpl(String name, String description, String category, String url, Object data, Map<String, Object> meta) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.url = url;
        this.data = data;
        this.meta = map(meta).immutable();
    }

    public static Attachment fromUrl(String name, String description, String category, String url, Map<String, Object> meta) {
        return new AttachmentImpl(name, description, category, url, null, meta);
    }

    public static Attachment fromData(String name, String description, String category, Object data, Map<String, Object> meta) {
        return new AttachmentImpl(name, description, category, null, data, meta);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    @Nullable
    public String getUrl() {
        return url;
    }

    @Override
    @Nullable
    public Object getDocument() {
        return data;
    }

    @Override
    public Map<String, Object> getMeta() {
        return meta;
    }

}
