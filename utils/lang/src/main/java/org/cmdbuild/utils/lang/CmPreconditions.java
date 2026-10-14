/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import jakarta.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.defaultString;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.commons.lang3.math.NumberUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.duplicates;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class CmPreconditions {

    public static <T> void checkNoDuplicates(Iterable<T> iterable) {
        Set<T> duplicates = duplicates(iterable);
        checkArgument(duplicates.isEmpty(), "found duplicate elements =< %s >", duplicates);
    }

    public static <T> void checkArgument(boolean expression) {
        if (!expression) {
            throw illegalArgument("check argument");
        }
    }

    public static <T> void checkArgument(boolean expression, @Nullable String message, @Nullable Object... params) {
        if (!expression) {
            throw illegalArgument(message, params);
        }
    }

    public static <T> T checkNotNull(@Nullable T arg) {
        return Objects.requireNonNull(arg);
    }

    public static <T> T checkNotNull(@Nullable T arg, @Nullable String message) {
        return Objects.requireNonNull(arg, message);
    }

    public static <T> T checkNotNull(@Nullable T arg, @Nullable String message, @Nullable Object... params) {
        return Objects.requireNonNull(arg, String.format(message, params));
    }

    public static String checkNotBlank(@Nullable String arg) {
        checkNotNull(trimToNull(arg));
        return arg;
    }

    public static String checkNotNumber(@Nullable String arg) {
        checkArgument(!NumberUtils.isCreatable(defaultString(arg)), "arg should not be a number =< %s >", arg);
        return arg;
    }

    public static Object checkNotBlank(@Nullable Object arg) {
        checkNotNull(blankToNull(arg));
        return arg;
    }

    public static Object checkNotBlank(@Nullable Object arg, @Nullable String message) {
        checkNotNull(blankToNull(arg), message);
        return arg;
    }

    public static Object checkNotBlank(@Nullable Object arg, @Nullable String message, @Nullable Object... params) {
        checkNotNull(blankToNull(arg), message, params);
        return arg;
    }

    public static byte[] checkNotBlank(@Nullable byte[] arg) {
        checkNotNull(arg);
        checkArgument(arg.length > 0, "byte array is empty");
        return arg;
    }

    public static byte[] checkNotBlank(@Nullable byte[] arg, @Nullable String message) {
        checkNotNull(arg);
        checkArgument(arg.length > 0, message);
        return arg;
    }

    public static <T extends Number> T checkNotNullAndGtZero(@Nullable T n) {
        checkArgument(isNotNullAndGtZero(n));
        return n;
    }

    public static <T extends Number> T checkNotNullAndGtZero(@Nullable T n, String message, Object... params) {
        checkArgument(isNotNullAndGtZero(n), message, params);
        return n;
    }

    public static <T extends Iterable> T checkNotEmpty(@Nullable T arg) {
        checkNotNull(arg);
        checkArgument(!isEmpty(arg));
        return arg;
    }

    public static <T extends Map> T checkNotEmpty(@Nullable T arg) {
        checkNotNull(arg);
        checkArgument(!isEmpty(arg.values()));
        return arg;
    }

    public static <T extends Iterable> T checkNotEmpty(@Nullable T arg, @Nullable String message) {
        checkNotNull(arg, message);
        checkArgument(!isEmpty(arg), message);
        return arg;
    }

    public static <T extends Iterable> T checkNotEmpty(@Nullable T arg, @Nullable String message, @Nullable Object... params) {
        checkNotNull(arg, message, params);
        checkArgument(!isEmpty(arg), message, params);
        return arg;
    }

    public static String checkNotBlank(@Nullable String arg, @Nullable String message) {
        checkNotNull(trimToNull(arg), message);
        return arg;
    }

    public static String checkNotBlank(@Nullable String arg, @Nullable String message, @Nullable Object... params) {
        checkNotNull(trimToNull(arg), message, params);
        return arg;
    }

    public static <T, C extends Iterable<T>> C checkNoneBlank(@Nullable C c, @Nullable String message, @Nullable Object... params) {
        checkNotNull(c, message, params);
        stream(c).forEach(x -> checkNotBlank(toStringOrNull(x), message, params));
        return c;
    }

    public static <T, C extends Iterable<T>> C checkNoneBlank(@Nullable C c) {
        checkNotNull(c);
        stream(c).forEach(x -> checkNotBlank(toStringOrNull(x)));
        return c;
    }

    public static String trimAndCheckNotBlank(@Nullable String arg) {
        return checkNotNull(trimToNull(arg));
    }

    public static String trimAndCheckNotBlank(@Nullable String arg, @Nullable String message) {
        return checkNotNull(trimToNull(arg), message);
    }

    public static String trimAndCheckNotBlank(@Nullable String arg, @Nullable String message, @Nullable Object... params) {
        return checkNotNull(trimToNull(arg), message, params);
    }

    public static String firstNotBlank(@Nullable String one, String two) {
        return checkNotBlank(firstNotBlankOrNull(one, two));
    }

    public static String firstNotBlank(@Nullable String one, String... others) {
        return checkNotBlank(list(one).with(checkNotNull(others)).stream().filter(StringUtils::isNotBlank).findFirst().orElse(null));
    }

    public static Object firstNotBlank(@Nullable Object one, Object two) {
        return checkNotBlank(firstNotBlankOrNull(one, two));
    }

    public static <T> T firstNotBlank(Supplier<T>... suppliers) {
        for (Supplier<T> supplier : suppliers) {
            T t = supplier.get();
            if (isNotBlank(t)) {
                return t;
            }
        }
        throw new NullPointerException();
    }

    @Nullable
    public static String firstNotBlankOrNull(@Nullable String one, @Nullable String two) {
        if (isBlank(one)) {
            return blankToNull(two);
        } else {
            return one;
        }
    }

    @Nullable
    public static Object firstNotBlankOrNull(@Nullable Object one, @Nullable Object two) {
        if (isNullOrBlank(one)) {
            return blankToNull(two);
        } else {
            return one;
        }
    }

    @Nullable
    public static String firstNotBlankOrNull(String... many) {
        return list(many).stream().filter(StringUtils::isNotBlank).findFirst().orElse(null);
    }

    @Nullable
    public static String firstNotBlankOrEmpty(@Nullable String one, String two) {
        if (isBlank(one)) {
            return defaultString(two);
        } else {
            return one;
        }
    }

    @Nullable
    public static String blankToNull(@Nullable String arg) {
        return isBlank(arg) ? null : arg;
    }

    @Nullable
    public static Object blankToNull(@Nullable Object arg) {
        return CmNullableUtils.isNullOrBlank(arg) ? null : arg;
    }

    /**
     *
     * @param obj object
     * @param function function where apply object
     * @return function applied if obj is not null, otherwise return null
     */
    @Nullable
    public static <V, T> T applyOrNull(V obj, Function<? super V, ? extends T> function) {
        if (obj == null) {
            return null;
        }
        return function.apply(obj);
    }

    /**
     *
     * @param obj object
     * @param function function where apply object
     * @param defaultValue default value
     * @return function applied if obj is not null, otherwise return default
     * value
     */
    @Nullable
    public static <V, T> T applyOrDefault(V obj, Function<? super V, ? extends T> function, T defaultValue) {
        if (obj == null) {
            return defaultValue;
        }
        return function.apply(obj);
    }
}
