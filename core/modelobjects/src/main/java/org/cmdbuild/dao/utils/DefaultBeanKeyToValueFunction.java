package org.cmdbuild.dao.utils;

import java.util.function.BiFunction;
import org.cmdbuild.utils.object.CmBeanUtils;

public enum DefaultBeanKeyToValueFunction implements BiFunction<String, Object, Object> {
    INSTANCE;

    @Override
    public Object apply(String key, Object object) {
        return CmBeanUtils.getBeanPropertyValue(object, key);
    }

}
