/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.lang;

import jakarta.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import static java.lang.String.format;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import static java.util.stream.Collectors.joining;
import org.apache.commons.lang3.Strings;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.unkey;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class CmExceptionUtils {

    private final static String CM_ERROR_PREFIX = "CM_ERROR_PREFIX_FOR_TRANSLATION";
    private final static String CM_ERROR_SEPARATOR = "CM_ERROR_LIST_FOR_TRANSLATION";

    /**
     * return a slf4j marker to mark log messages (usually warning messages)
     * that should be propagated to user
     *
     * @return
     */
    public static Marker marker() {
        return MarkerFactory.getMarker("NOTIFY");
    }

    public static Object lazyString(Supplier supplier) {
        CmPreconditions.checkNotNull(supplier);
        return new Object() {
            @Override
            public String toString() {
                return toStringOrNull(supplier.get());
            }
        };
    }

    public static RuntimeException cause(Throwable ex) {
        return extractCause(ex);
    }

    public static RuntimeException extractCause(Throwable ex) {
        if (ex.getCause() != null) {
            return toRuntimeException(ex.getCause());
        } else {
            return toRuntimeException(ex);
        }
    }

    public static RuntimeException runtime(Throwable ex) {
        return toRuntimeException(ex);
    }

    public static RuntimeException runtime(Throwable ex, String message, Object... args) {
        return new RuntimeException(format(message, args), ex);
    }

    public static RuntimeException runtime(String message, Object... args) {
        return new RuntimeException(format(message, args));
    }

    public static IllegalArgumentException illegalArgument(Throwable ex) {
        return new IllegalArgumentException(ex);
    }

    public static IllegalArgumentException illegalArgument(Throwable ex, String message, Object... args) {
        return new IllegalArgumentException(format(message, args), ex);
    }

    public static IllegalArgumentException illegalArgument(String message, Object... args) {
        return new IllegalArgumentException(format(message, args));
    }

    public static UnsupportedOperationException unsupported(String message) {
        return new UnsupportedOperationException(message);
    }

    public static UnsupportedOperationException unsupported(String message, Object... args) {
        return new UnsupportedOperationException(format(message, args));
    }

    public static Exception inner(Exception ex) {
        if ((ex instanceof ExecutionException || ex instanceof InvocationTargetException) && (ex.getCause() != null) && (ex.getCause() instanceof Exception)) {
            ex = (Exception) ex.getCause();
        }
        return ex;
    }

    public static RuntimeException toRuntimeException(Throwable ex) {
        if (ex instanceof RuntimeException re) {
            return re;
        } else {
            return new RuntimeException(ex);
        }
    }

    public static String exceptionToMessage(Throwable ex) {
        List<String> messages = list();
        while (ex != null) {
            messages.add(ex.toString());
            ex = ex.getCause();
        }
        return messages.stream().collect(joining(", caused by: "));
    }

    public static String printStackTrace(Throwable ex) {
        StringWriter writer = new StringWriter();
        try (PrintWriter printWriter = new PrintWriter(writer)) {
            ex.printStackTrace(printWriter);
        }
        return writer.toString();
    }

    public static String printStackTrace(Thread thread) {
        Throwable throwable = new Throwable();
        throwable.setStackTrace(thread.getStackTrace());
        return printStackTrace(throwable).replaceFirst("^.*?[\n\r]+\\s+", Matcher.quoteReplacement("thread \"%s\" is %s ".formatted(thread.getName(), thread.getState())));
    }

    @Nullable
    public static <T extends Throwable> T extractExceptionOrNull(Throwable ex, Class<T> type) {
        if (type.isInstance(ex)) {
            return type.cast(ex);
        } else {
            if (ex.getCause() == null) {
                return null;
            } else {
                return extractExceptionOrNull(ex.getCause(), type);
            }
        }
    }

    public static boolean isParamError(String key) {
        return key.startsWith(CM_ERROR_PREFIX);
    }

    public static String parseError(String error, String... parts) {
        return format("%s%s%s%s", CM_ERROR_PREFIX, error, CM_ERROR_SEPARATOR, key(parts));
    }

    public static String parseErrorCode(String code, String error, String... parts) {
        return format("CMO %s: %s%s%s%s", code, CM_ERROR_PREFIX, error, CM_ERROR_SEPARATOR, key(parts));
    }

    public static Pair<String, List<String>> serializeError(String key) {
        String[] split = Strings.CS.removeStart(key, CM_ERROR_PREFIX).split(CM_ERROR_SEPARATOR);
        if (split.length == 1) {
            return Pair.of(split[0], list());
        }
        CmPreconditions.checkArgument(split.length == 2, "too many arguments");
        return Pair.of(split[0], unkey(split[1]));
    }
}
