package org.cmdbuild.easytemplate;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import java.lang.invoke.MethodHandles;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EasytemplateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Function<String, Object> emptyStringOnNull(Function<String, Object> delegate) {
        return Functions.compose((input) -> defaultIfNull(input, ""), delegate);
    }

    public static Function<String, Object> nullOnError(Function<String, Object> delegate) {
        return (in) -> {
            try {
                return delegate.apply(in);
            } catch (final Throwable e) {
                LOGGER.warn("evaluation error, returning 'null' value", e);
                return null;
            }
        };
    }
}
