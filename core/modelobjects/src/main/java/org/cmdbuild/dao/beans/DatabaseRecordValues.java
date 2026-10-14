package org.cmdbuild.dao.beans;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import org.cmdbuild.common.beans.IdAndDescription;
import static org.cmdbuild.dao.constants.SystemAttributes.SYSTEM_ATTRIBUTES_NEVER_INSERTED;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTimeUtc;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.primitiveEquals;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBigDecimal;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface DatabaseRecordValues {

    @Nullable
    Object get(String key);

    Iterable<Map.Entry<String, Object>> getAttributeValues();

    @Nullable
    default <T> T get(String key, Class<? extends T> requiredType) {
        return convert(get(key), requiredType);
    }

    default <T> T getNotNull(String key, Class<? extends T> requiredType) {
        return checkNotNull(get(key, requiredType), "value is null or missing for key =< %s > within record = %s", key, this);
    }

    default <T> T get(String key, Class<? extends T> requiredType, T defaultValue) {
        return defaultIfNull(get(key, requiredType), defaultValue);
    }

    @Nullable
    default String getString(String key) {
        return get(key, String.class);
    }

    @Nullable
    default Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    @Nullable
    default Long getLong(String key) {
        return get(key, Long.class);
    }

    @Nullable
    default boolean getBoolean(String key) {
        return get(key, Boolean.class);
    }

    @Nullable
    default String getDescriptionOf(String key) {
        return Optional.ofNullable(get(key, IdAndDescription.class)).map(IdAndDescription::getDescription).orElse(null);
    }

    @Nullable
    default String getCodeOf(String key) {
        return Optional.ofNullable(get(key, IdAndDescription.class)).map(IdAndDescription::getCode).orElse(null);
    }

    default Map<String, Object> toMap() {
        return stream(getAttributeValues()).collect(CmMapUtils.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    default boolean allValuesEqualTo(DatabaseRecordValues other) {
        return getAttrsChangedFrom(other).isEmpty();
    }

    default Set<String> getAttrsChangedFrom(DatabaseRecordValues other) {
        return getAttrsChangedFrom(other.toMap());
    }

    default Set<String> getAttrsChangedFrom(Map<String, Object> other) {
        Map<String, Object> thisValues = map(toMap()).withoutKeys(SYSTEM_ATTRIBUTES_NEVER_INSERTED::contains), otherValues = map(other).filterKeys(thisValues::containsKey);
        return set(thisValues.keySet()).with(otherValues.keySet()).without(k -> {
            Object one = thisValues.get(k), two = otherValues.get(k);
            return isAttrEqual(one, two);
        });
    }

    default boolean isAttributeChanged(String key, DatabaseRecordValues other) {
        return !isAttributeEqual(key, other);
    }

    default boolean isAttributeEqual(String key, DatabaseRecordValues other) {
        Object one = map(toMap()).get(key), otherValues = map(other.toMap()).get(key);
        return isAttrEqual(one, otherValues);
    }

    static boolean isAttrEqual(Object one, Object two) {
        if (equal(one, two)) {
            return true;
        } else if (one == null || two == null) {
            return false;
        } else if (one instanceof IdAndDescription && two instanceof IdAndDescription) {
            return equal(((IdAndDescription) one).getId(), ((IdAndDescription) two).getId());
        } else if (one instanceof ZonedDateTime && two instanceof ZonedDateTime) {
            return equal(toIsoDateTimeUtc(one), toIsoDateTimeUtc(two));
        } else if (one instanceof Number && two instanceof Number) {
            return toBigDecimal(one).compareTo(toBigDecimal(two)) == 0;
        } else if (primitiveEquals(one, two)) {
            return true;
        } else {
            return false;
        }
    }

    static DatabaseRecordValues fromMap(Map map) {
        return new DatabaseRecordValues() {
            @Override
            public Object get(String key) {
                return map.get(key);
            }

            @Override
            public Iterable<Map.Entry<String, Object>> getAttributeValues() {
                return map.values();
            }
        };
    }
}
