/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.object;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.MoreCollectors.onlyElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class CmBeanUtils {

    @Nullable
    public static Object getBeanPropertyValue(Object bean, String key) {
        checkNotNull(bean);
        checkNotBlank(key);
        try {
            if (bean instanceof Map map) {
                return map.get(key);
            } else {
                Method method = list(bean.getClass().getMethods()).stream().filter((m) -> m.getParameterCount() == 0).filter((m) -> m.getName().toLowerCase().matches("^(get|is)" + Pattern.quote(key.toLowerCase()))).collect(onlyElement());
                return method.invoke(bean);
            }
        } catch (Exception ex) {
            throw runtime(ex, "error extracting bean value for key =< %s > from bean = %s", key, bean);
        }
    }

    @Nullable
    public static <T> T getBeanPropertyValue(Object bean, String key, Class<T> classe) {
        return convert(getBeanPropertyValue(bean, key), classe);
    }

    @Nullable
    public static Collection<String> getBeanProperties(Object bean) {
        if (bean instanceof Map map) {
            return map.keySet();
        } else {
            return list(bean.getClass().getMethods()).filter((m) -> m.getParameterCount() == 0).map(m -> {
                Matcher matcher = Pattern.compile("^(get|is)(.+)").matcher(m.getName());
                return matcher.matches() ? matcher.group(1) : null;
            }).filter(StringUtils::isNotBlank).collect(toImmutableSet());
        }
    }

}
