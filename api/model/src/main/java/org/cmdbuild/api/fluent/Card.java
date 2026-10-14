package org.cmdbuild.api.fluent;

import java.util.Map;
import java.util.Set;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;

public interface Card extends CardDescriptor {

    boolean hasAttribute(String name);

    Set<String> getAttributeNames();

    Map<String, Object> getAttributes();

    Object get(String name);

    void set(String name, Object value);

    default boolean has(String name) {
        return hasAttribute(name);
    }

    default <T> T get(String name, Class<T> clazz) {
        return convert(get(name), clazz);
    }

    default String getCode() {
        return get(ATTR_CODE, String.class);
    }

    default String getDescription() {
        return get(ATTR_DESCRIPTION, String.class);
    }

    default void setCode(String value) {
        set(ATTR_CODE, value);
    }

    default void setDescription(String value) {
        set(ATTR_DESCRIPTION, value);
    }

}
