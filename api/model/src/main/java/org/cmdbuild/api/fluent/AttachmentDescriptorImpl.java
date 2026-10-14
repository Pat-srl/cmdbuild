package org.cmdbuild.api.fluent;

import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class AttachmentDescriptorImpl implements AttachmentDescriptor {

    private final String name;
    private final String description;
    private final String category;
    private final Map<String, Object> meta;

    public AttachmentDescriptorImpl(String name, String description, String category, Map<String, Object> meta) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.meta = map(meta).immutable();
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
    public Map<String, Object> getMeta() {
        return meta;
    }

}
