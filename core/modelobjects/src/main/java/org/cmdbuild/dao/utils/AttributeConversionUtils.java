/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import com.google.common.primitives.Ints;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.apache.commons.lang3.math.NumberUtils.isCreatable;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.dao.DaoException;
import org.cmdbuild.dao.beans.LookupValueImpl;
import org.cmdbuild.dao.beans.LookupValueImpl.LookupValueBuilder;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.utils.crypto.Cm3EasyCryptoUtils;
import static org.cmdbuild.utils.date.CmDateUtils.toDate;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import static org.cmdbuild.utils.date.CmDateUtils.toInterval;
import static org.cmdbuild.utils.date.CmDateUtils.toTime;
import static org.cmdbuild.utils.html.HtmlSanitizerUtils.sanitizeHtmlForLink;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.stream;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBigDecimalOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toDoubleOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toFloatOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;
import org.cmdbuild.workflow.type.LookupType;
import org.cmdbuild.workflow.type.ReferenceType;
import static org.cmdbuild.workflow.type.utils.WorkflowTypeUtils.emptyToNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttributeConversionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static <T> T rawToSystem(Attribute attribute, @Nullable Object value) {
        value = rawToSystem(attribute.getType(), value);
        if (attribute.getMetadata().encryptOnDb()) {
            value = Cm3EasyCryptoUtils.decryptValue(toStringOrNull(value));
            value = rawToSystem(attribute.getType(), value);
        }
        return (T) value;
    }

    public static <T> T rawToSystem(CardAttributeType<T> attributeType, @Nullable Object value) {
        try {
            return doRawToSystem(attributeType, value);
        } catch (Exception ex) {
            throw runtime(ex, "error converting raw value =< %s > ( %s ) for attribute type = %s", abbreviate(value), getClassOfNullable(value).getSimpleName(), attributeType);
        }
    }

    private static <T> T doRawToSystem(CardAttributeType<T> attributeType, @Nullable Object value) {
        if (value == null) {
            return null;
        } else {
            return switch (attributeType.getName()) {
                case BOOLEAN ->
                    (T) toBooleanOrNull(value);
                case CHAR ->
                    (T) convertTextValue(value, 1);
                case DATE ->
                    (T) toDate(value);
                case TIME ->
                    (T) toTime(value);
                case TIMESTAMP ->
                    (T) toDateTime(value);
                case INTERVAL ->
                    (T) toInterval(value);
                case DECIMAL ->
                    (T) toBigDecimalOrNull(value);
                case DOUBLE ->
                    (T) toDoubleOrNull(value);
                case FLOAT ->
                    (T) toFloatOrNull(value);
                case REGCLASS ->
                    (T) (value instanceof EntryType ? ((EntryType) value).getName() : unescapeRegclassName(toStringOrNull(value)));
                case REFERENCE, FOREIGNKEY ->
                    (T) convertReference(value);
                case FILE ->
                    (T) convertReference(value);
                case REFERENCEARRAY ->
                    (T) convertReferenceArray(value);
                case INTEGER ->
                    (T) toIntegerOrNull(value);
                case LONG ->
                    (T) toLongOrNull(value);
                case LOOKUP ->
                    (T) convertLookupValue(((LookupAttributeType) attributeType).getLookupTypeName(), value);
                case LOOKUPARRAY ->
                    (T) convertLookupArrayValue(((LookupAttributeType) attributeType).getLookupTypeName(), value);
                case STRINGARRAY ->
                    (T) convert(value, String[].class);
                case BYTEAARRAY ->
                    (T) convert(value, byte[][].class);
                case STRING ->
                    (T) convertTextValue(value, ((StringAttributeType) attributeType).getLength());
//                    return (T) convertTextValue(value, null);//TODO max val from metadata??
                case LINK ->
                    (T) validateLinkAttrValue(convertTextValue(value, null));
                case GEOMETRY ->
                    (T) convertTextValue(value, null);//TODO validate geometry syntax (?)
                case TEXT, JSON ->
                    (T) convertTextValue(value, null);
                case BYTEARRAY ->
                    (T) convertByteArrayValue(value);
                case INET ->
                    (T) parseIpAddrAttributeValue((IpAddressAttributeType) attributeType, value);
                default ->
                    (T) value;
            };
        }
    }

    @Nullable
    private static LookupValue convertLookupValue(String lookupTypeName, @Nullable Object value) {
        checkNotBlank(lookupTypeName);
        if (value == null || value instanceof LookupValue) {
            return (LookupValue) value;
        } else if (value instanceof IdAndDescription idAndDescription) {
            return new LookupValueImpl(idAndDescription.getId(), idAndDescription.getDescription(), lookupTypeName);
        } else if (value instanceof LookupType lookupType) {
            return new LookupValueImpl(lookupType.getId(), lookupType.getDescription(), lookupTypeName);
        } else if (value instanceof Number) {
            if (toLong(value) == 0) {
                return null;
            } else {
                return new LookupValueImpl(toLong(value), StringUtils.EMPTY, lookupTypeName);
            }
        } else if (value instanceof String) {
            String str = String.class.cast(value);
            if (isBlank(str)) {
                return null;
            } else {
                LookupValueBuilder builder = LookupValueImpl.builder().withDescription("").withLookupType(lookupTypeName);
                if (str.startsWith(lookupTypeName + ".")) {//TODO improve this, add type validation
                    str = str.substring(lookupTypeName.length() + 1);
                    builder.withCode(str);
                } else if (isCreatable(str)) { // is a number
                    builder.withId(toLong(str));
                } else {
                    builder.withCode(str);
                }
                return builder.build();
            }
        } else {
            throw new IllegalArgumentException(format("invalid value = <%s> (%s) for lookup attr type", abbreviate(value), getClassOfNullable(value).getName()));
        }
    }

    /**
     * Prepare LOOKUPARRAY value data for processing (when handling in a email
     * template) that will be done in
     * {@link EmailTemplateProcessorServiceImpl#handleLookupArrayAttrValues()}
     *
     * @param lookupTypeName
     * @param value
     * @return
     */
    @Nullable
    private static List<LookupValue> convertLookupArrayValue(String lookupTypeName, @Nullable Object value) {
        LOGGER.debug("retrieving lookup array value ({}): {}", value.getClass().getSimpleName(), value);

        checkNotBlank(lookupTypeName);
        // A List containing something
        // Handles:
        // - Long[] -- when doing a select after creating a Card;
        // - java.sql.Array -- see #7734;
        // - ArrayList of Long;
        // - ArrayList of Integer -- see AttributeRestIT.;
        // - CSV String -- see #7646;
        List<?> parsedValues = CmConvertUtils.convert(value, List.class);
        if (isNull(parsedValues) || parsedValues.isEmpty()) {
            return emptyList();
        }

        // workaround for type erasure in List with generics
        if (parsedValues.get(0) instanceof LookupValue) {
            return (List<LookupValue>) parsedValues;
        } else {
            List<Long> ids = fetchLookupIds(lookupTypeName, parsedValues);
            return fetchLookupFromId(lookupTypeName, ids);
        }
    }

    /**
     *
     * @param lookupTypeName
     * @param value
     * @return
     * @throws IllegalArgumentException if component lookup id not found in one
     * of supported data type.
     */
    private static List<Long> fetchLookupIds(String lookupTypeName, List<?> values) {
        List<Long> result = list();
        values.forEach(value -> {
            try {
                Long id = CmConvertUtils.convert(value, Long.class);
                result.add(id);
            } catch (IllegalArgumentException excIll) {
                throw new IllegalArgumentException(format("invalid value = <%s> (%s) for lookupArray attr %s type", abbreviate(value), getClassOfNullable(value).getName(), lookupTypeName));
            }
        });

        return result;
    }

    private static List<LookupValue> fetchLookupFromId(String lookupTypeName, List<Long> ids) {
        return ids.stream().map(v -> lookupValueFromId(lookupTypeName, v)).collect(toList());
    }

    @Nullable
    private static LookupValue lookupValueFromId(String lookupTypeName, Long idValue) {
        final long id = toLong(idValue);
        if (toLong(id) == 0) {
            return null;
        }

        return new LookupValueImpl(id, StringUtils.EMPTY, lookupTypeName);
    }

    public static String LINK_MATCHING_REGEX = "^[<]a\\s+href=\"([^\"\\s]+)\"(\\starget=\"_blank\"\\srel=\"noopener\\snoreferrer\")*>([^<]*)</a>$";

    @Nullable
    public static String validateLinkAttrValue(@Nullable String link) {
        checkArgument(isBlank(link) || (link.matches(LINK_MATCHING_REGEX)), "invalid value for link attr =< %s >", link);
        return sanitizeHtmlForLink(link);
    }

    @Nullable
    private static IdAndDescription convertReference(@Nullable Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof IdAndDescription) {
            return IdAndDescription.class.cast(value);
        } else if (value instanceof ReferenceType referenceType) {
            ReferenceType reference = emptyToNull(referenceType);
            return reference == null ? null : new IdAndDescriptionImpl(reference.getId(), reference.getDescription());
        } else if (value instanceof Number number) {
            if (isNotNullAndGtZero(number)) {
                return new IdAndDescriptionImpl(toLong(value), StringUtils.EMPTY);
            } else {
                return null;
            }
        } else if (value instanceof String) {
            return isCreatable(value.toString()) ? convertReference(toLongOrNull(value)) : null; //revert this, workaround waiting merge request !334
        } else if (value instanceof Map map) {
            Long id = toLongOrNull(map.get("_id"));
//            String description = toStringOrNull(map.get("_description"));
//            String code = toStringOrNull(map.get("_code"));
//            return isNotNullAndGtZero(id) ? new IdAndDescriptionImpl(sqlTableToClassNameOrNull(toStringOrNull(map.get("_type"))), id, toStringOrNull(map.get("_description")), toStringOrNull(map.get("_code"))) : null;
            return isNotNullAndGtZero(id) ? new IdAndDescriptionImpl(toStringOrNull(map.get("_type")), id, toStringOrNull(map.get("_description")), toStringOrNull(map.get("_code"))) : null;
        } else {
            throw new IllegalArgumentException(format("invalid value = '%s' for reference attr type", value));
        }
    }

    public static boolean isLookupArrayIdsAsString(String text, String separator) {
        boolean hasNotNumberElement = false;
        for (String s : Splitter.on(separator).split(text)) {
            if (Ints.tryParse(s) == null) {
                hasNotNumberElement = true;
            }
        }
        return !hasNotNumberElement;
    }

    @Nullable
    private static IdAndDescription[] convertReferenceArray(@Nullable Object value) {
        if (value == null) {
            return null;
        } else {
            if (value.getClass().isArray()) {
                value = convert(value, List.class);
            }
            checkArgument(value instanceof Iterable, "expect some sort of iterable, found %s (%s)", value, getClassOfNullable(value));
            return ((List<IdAndDescription>) stream((Iterable) value).map(AttributeConversionUtils::convertReference).collect(toList())).toArray(IdAndDescription[]::new);
        }
    }

    @Nullable
    private static String convertTextValue(@Nullable Object value, @Nullable Integer maxLen) {
        if (value == null) {
            return null;
        }
        String stringValue = value.toString();
        if (isBlank(stringValue)) {
            return null;
        }
        if (maxLen != null) {
            checkArgument(stringValue.length() <= maxLen, "string '%s' exceed maximum attr len of %s", abbreviate(stringValue), maxLen);
        }

        return stringValue;
    }

    @Nullable
    private static byte[] convertByteArrayValue(@Nullable Object value) {
        if (value == null) {
            return null;
        }
        checkArgument(value instanceof byte[], "expected argument of type byte[], found instead %s (%s)", value.getClass(), value);
        return (byte[]) value;
    }

    private static final String IPV4SEG = "(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])";
    private static final String IPV4ADDR = "(" + IPV4SEG + "\\.){3,3}" + IPV4SEG;
    private static final String IPV6SEG = "[0-9a-fA-F]{1,4}";
    private static final String IPV6ADDR = "(" //
            + "(" + IPV6SEG + ":){7,7}" + IPV6SEG + "|" //
            + "(" + IPV6SEG + ":){1,7}:|" //
            + "(" + IPV6SEG + ":){1,6}:" + IPV6SEG + "|" //
            + "(" + IPV6SEG + ":){1,5}(:" + IPV6SEG + "){1,2}|" //
            + "(" + IPV6SEG + ":){1,4}(:" + IPV6SEG + "){1,3}|" //
            + "(" + IPV6SEG + ":){1,3}(:" + IPV6SEG + "){1,4}|" //
            + "(" + IPV6SEG + ":){1,2}(:" + IPV6SEG + "){1,5}|" //
            + IPV6SEG + ":((:" + IPV6SEG + "){1,6})|" //
            + ":((:" + IPV6SEG + "){1,7}|:)|" //
            + "fe80:(:" + IPV6SEG + "){0,4}%[0-9a-zA-Z]{1,}|" //
            + "::(ffff(:0{1,4}){0,1}:){0,1}" + IPV4ADDR + "|" //
            + "(" + IPV6SEG + ":){1,4}:" + IPV4ADDR //
            + ")";
    private static final String CLASS_SEPARATOR_REGEX = "/";
    private static final String IPV4_CLASS_REGEX = "(3[0-2]|[1-2][0-9]|[8-9])";
    private static final String IPV4_PATTERN = "^" + IPV4ADDR + "(" + CLASS_SEPARATOR_REGEX + IPV4_CLASS_REGEX + ")*" + "$";
    private static final String IPV6_CLASS_REGEX = "([0-9]|[1-9][0-9]|1[0-1][0-9]|12[0-8])";
    private static final String IPV6_PATTERN = "^" + IPV6ADDR + "(" + CLASS_SEPARATOR_REGEX + IPV6_CLASS_REGEX + ")*" + "$";

    @Nullable
    public static String parseIpAddrAttributeValue(IpAddressAttributeType attributeType, @Nullable Object value) {
        String stringValue = trimToNull(toStringOrNull(value));
        if (isBlank(stringValue)) {
            return null;
        } else {
            switch (attributeType.getType()) {
                case IPV4 ->
                    checkArgument(stringValue.matches(IPV4_PATTERN), "invalid ipv4 value");
                case IPV6 ->
                    checkArgument(stringValue.matches(IPV6_PATTERN), "invalid ipv6 value");
                case ANY ->
                    checkArgument(stringValue.matches(IPV4_PATTERN) || stringValue.matches(IPV6_PATTERN), "invalid ipv4/ipv6 value");
                default ->
                    throw new DaoException("invalid ip type = %s", attributeType.getType());
            }
            return stringValue;
        }
    }

    @Nullable
    private static String unescapeRegclassName(@Nullable String name) {
        if (isBlank(name)) {
            return null;
        } else {
            return name.replaceAll("^\"|\"$", "");
        }
    }

}
