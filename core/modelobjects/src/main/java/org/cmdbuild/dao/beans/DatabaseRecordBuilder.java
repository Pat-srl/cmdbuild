package org.cmdbuild.dao.beans;

import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface DatabaseRecordBuilder<T extends DatabaseRecord, B extends Builder<T, B>> extends Builder<T, B> {

    B withAttributes(Map<String, Object> attributes);

    B withAttribute(String key, @Nullable Object value);

    default B withAttributes(Object... attributes) {
        return withAttributes(map(attributes));
    }
}
