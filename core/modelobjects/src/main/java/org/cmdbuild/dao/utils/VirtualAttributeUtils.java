/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Predicates.isNull;
import static java.lang.String.format;
import java.util.Map;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import static org.cmdbuild.dao.entrytype.EntryTypeMetadata.VIRTUAL_ATTRIBUTES_PREFIX;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ByteArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ByteaArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.CharAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DateTimeAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DoubleAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FileAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FloatAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.FormulaAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.GeometryAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntegerAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IntervalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.JsonAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LinkAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LongAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.RegclassAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TimeAttributeType;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class VirtualAttributeUtils {

    public static final String VIRTUAL_ATTRIBUTE_TYPE_ATTR = "type";

    public static Map<String, String> createVirtualAttribute(Map<String, String> meta, AttributeWithoutOwner attribute) {
        checkArgument(attribute.isVirtual());
        return map(meta).with(map(VIRTUAL_ATTRIBUTE_TYPE_ATTR, serializeEnum(attribute.getType().getName())).with(attribute.getMetadata().getAll())
                .withoutValues(isNull())
                .mapKeys(k -> format("%s___%s___%s", VIRTUAL_ATTRIBUTES_PREFIX, attribute.getName(), k)));
    }

    public static Map<String, String> deleteVirtualAttribute(Map<String, String> meta, AttributeWithoutOwner attribute) {
        checkArgument(attribute.isVirtual());
        return map(meta).withoutKeys(k -> k.startsWith(format("%s___%s___", VIRTUAL_ATTRIBUTES_PREFIX, attribute.getName())));
    }

    public static Map<String, String> updateVirtualAttribute(Map<String, String> meta, AttributeWithoutOwner attribute) {
        checkArgument(attribute.isVirtual());
        return createVirtualAttribute(deleteVirtualAttribute(meta, attribute), attribute);
    }

    public static boolean isVirtual(CardAttributeType type) {
        return isVirtual(type.getName());
    }

    public static boolean isVirtual(AttributeTypeName type) {
        return switch (type) {
            case FORMULA ->
                true;
            default ->
                false;
        };
    }

    public static CardAttributeType attributeTypeNameToAttributeType(AttributeTypeName typeName) {
        return switch (typeName) {
//                case DECIMAL:
//                    return new DecimalAttributeType(precision, scale);
//                case FOREIGNKEY:
//                    return new ForeignKeyAttributeType(targetClass);
//                case INET:
//                    return new IpAddressAttributeType(parseEnumOrDefault(ipType, IpType.IPV4));
//                case LOOKUP:
//                    return new LookupAttributeType(lookupType);
//                case LOOKUPARRAY:
//                    return new LookupArrayAttributeType(lookupType);
//                case REFERENCE:
//                    return new ReferenceAttributeType(domain, direction);
//                case STRING:
//                    return new StringAttributeType(maxLength);
            case BOOLEAN ->
                new BooleanAttributeType();
            case BYTEARRAY ->
                new ByteArrayAttributeType();
            case CHAR ->
                new CharAttributeType();
            case DATE ->
                new DateAttributeType();
            case DOUBLE ->
                new DoubleAttributeType();
            case FLOAT ->
                FloatAttributeType.INSTANCE;
            case FORMULA ->
                FormulaAttributeType.INSTANCE;
            case FILE ->
                FileAttributeType.INSTANCE;
            case LINK ->
                LinkAttributeType.INSTANCE;
            case REGCLASS ->
                RegclassAttributeType.INSTANCE;
            case INTEGER ->
                new IntegerAttributeType();
            case LONG ->
                LongAttributeType.INSTANCE;
            case GEOMETRY ->
                GeometryAttributeType.INSTANCE;
            case JSON ->
                JsonAttributeType.INSTANCE;
            case STRINGARRAY ->
                new StringArrayAttributeType();
            case BYTEAARRAY ->
                new ByteaArrayAttributeType();
            case TEXT ->
                new TextAttributeType();
            case TIME ->
                new TimeAttributeType();
            case TIMESTAMP ->
                new DateTimeAttributeType();
            case INTERVAL ->
                IntervalAttributeType.INSTANCE;
            default ->
                throw unsupported("unable to convert attribute type =< %s >", typeName);
        };
    }
}
