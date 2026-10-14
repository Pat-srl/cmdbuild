package org.cmdbuild.api.fluent;

import static java.util.Collections.emptyMap;
import java.util.Map;

public interface AttachmentDescriptor {

    String getName();

    String getDescription();

    String getCategory();

    default Map<String, Object> getMeta() {
        return emptyMap();
    }
}
