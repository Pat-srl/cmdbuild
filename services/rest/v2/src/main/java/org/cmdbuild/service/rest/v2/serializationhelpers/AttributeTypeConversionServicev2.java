/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v2.serializationhelpers;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import java.util.Map;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_CREATE;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_MODIFY;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_UPDATE;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.serializeAttributePermissionMode;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.serializePermissions;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.utils.EcqlUtils;
import static org.cmdbuild.ecql.utils.EcqlUtils.getEcqlExpressionFromAttributeFilter;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_BOOLEAN;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_BYTEA_ARRAY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_CHAR;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_DATE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_DATE_TIME;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_DECIMAL;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_DOUBLE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_ENTRY_TYPE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_FLOAT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_FOREIGN_KEY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_GEOMETRY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_INTEGER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_IP_ADDRESS;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_LONG;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_LOOKUP;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_REFERENCE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_STRING;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_STRING_ARRAY;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_TEXT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.TYPE_TIME;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnumUpper;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import org.springframework.stereotype.Component;

@Component
public class AttributeTypeConversionServicev2 {

    public final static BiMap<AttributeTypeName, String> TYPE_NAME_MAP = ImmutableBiMap.copyOf(map(
            AttributeTypeName.BOOLEAN, TYPE_BOOLEAN,
            AttributeTypeName.CHAR, TYPE_CHAR,
            AttributeTypeName.DATE, TYPE_DATE,
            AttributeTypeName.TIMESTAMP, TYPE_DATE_TIME,
            AttributeTypeName.DECIMAL, TYPE_DECIMAL,
            AttributeTypeName.FLOAT, TYPE_FLOAT,
            AttributeTypeName.DOUBLE, TYPE_DOUBLE,
            AttributeTypeName.REGCLASS, TYPE_ENTRY_TYPE,
            AttributeTypeName.FOREIGNKEY, TYPE_FOREIGN_KEY,
            AttributeTypeName.INTEGER, TYPE_INTEGER,
            AttributeTypeName.LONG, TYPE_LONG,
            AttributeTypeName.INET, TYPE_IP_ADDRESS,
            AttributeTypeName.LOOKUP, TYPE_LOOKUP,
            AttributeTypeName.REFERENCE, TYPE_REFERENCE,
            AttributeTypeName.STRINGARRAY, TYPE_STRING_ARRAY,
            AttributeTypeName.BYTEAARRAY, TYPE_BYTEA_ARRAY,
            AttributeTypeName.STRING, TYPE_STRING,
            AttributeTypeName.GEOMETRY, TYPE_GEOMETRY,
            AttributeTypeName.TEXT, TYPE_TEXT,
            AttributeTypeName.TIME, TYPE_TIME));

    private final DaoService dao;
    private final ObjectTranslationService translationService;

    public AttributeTypeConversionServicev2(DaoService dataView, ObjectTranslationService translationService) {
        this.dao = checkNotNull(dataView);
        this.translationService = checkNotNull(translationService);
    }

    public Object serializeAttributeType(Attribute attribute) {
        return map(
                "_id", attribute.getName(),
                "type", serializeAttributeType(attribute.getType().getName()),
                "name", attribute.getName(),
                "description", attribute.getDescription(),
                "_description_translation", translationService.translateAttributeDescription(attribute),
                "showInGrid", attribute.showInGrid(),
                "unique", attribute.isUnique(),
                "mandatory", attribute.isMandatory(),
                "inherited", attribute.isInherited(),
                "active", attribute.isActive(),
                "index", attribute.getIndex(),
                "defaultValue", attribute.getDefaultValue(),
                "group", attribute.getGroupNameOrNull(),
                "_group_description", attribute.getGroupDescriptionOrNull(),
                //				"_group_description_translation", attribute.hasGroup() ? translationService.translateAttributeGroupDescription(attribute.getGroup()) : null, TODO
                "mode", serializeAttributePermissionMode(attribute.getMode()),
                "writable", attribute.hasUiPermission(AP_CREATE) || attribute.hasUiPermission(AP_UPDATE),
                "immutable", attribute.hasUiPermission(AP_CREATE) && !attribute.hasUiPermission(AP_UPDATE),
                "hidden", !attribute.hasUiReadPermission(),
                "_can_read", attribute.hasServiceReadPermission(),
                "_can_create", attribute.hasServicePermission(AP_CREATE),
                "_can_update", attribute.hasServicePermission(AP_UPDATE),
                "_can_modify", attribute.hasServicePermission(AP_MODIFY),
                "metadata", attribute.getMetadata().getCustomMetadata())
                .accept((a) -> {
                    if (attribute.hasServiceModifyPermission()) {
                        a.put("_permissions", serializePermissions(attribute));
                    }
                })
                .with(serializeAttributeSpecificValues(attribute));
    }

    public static String serializeAttributeType(AttributeTypeName name) {
        return checkNotNull(TYPE_NAME_MAP.get(name), "unsupported attr type = %s", name);
    }

    private Map<String, Object> serializeAttributeSpecificValues(Attribute attribute) {
        FluentMap<String, Object> map = map();
        attribute.getType().accept(new NullAttributeTypeVisitor() {
            @Override
            public void visit(DecimalAttributeType attributeType) {
                map.put("precision", attributeType.getPrecision(), "scale", attributeType.getScale());
            }

            @Override
            public void visit(ForeignKeyAttributeType attributeType) {
                Classe classe = dao.getClasse(attributeType.getForeignKeyDestinationClassName());
                map.put("targetClass", classe.getName(), "targetType", getType(classe));
            }

            @Override
            public void visit(LookupAttributeType attributeType) {
                map.put("lookupType", attributeType.getLookupTypeName(),
                        "filter", attribute.getFilter());
                attachEcqlFilterStuffIfApplicable();
            }

            @Override
            public void visit(ReferenceAttributeType attributeType) {
                Domain domain = dao.getDomain(attributeType.getDomainName());
                Classe target = domain.getReferencedClass(attributeType);
                map.put("domain", domain.getName(),
                        "targetClass", target.getName(),
                        "targetType", getType(target),
                        "filter", attribute.getFilter());
                attachEcqlFilterStuffIfApplicable();

//						.withFilter(newAttributeFilter() //
//								.withText(attribute.getFilter()) //
//								.withParams(toMap(metadataStoreFactory.storeForAttribute(attribute).readAll())) // TODO filter params from metadata (????)
            }

            @Override
            public void visit(StringAttributeType attributeType) {
                map.put("maxLength", attributeType.getLength());
            }

            @Override
            public void visit(TextAttributeType attributeType) {
                map.put("editorType", serializeEnumUpper(attribute.getEditorType()));//wtf? editor type should be an attr of TextAttributeType, not of Attribute
            }

            @Override
            public void visit(IpAddressAttributeType attributeType) {
                map.put("ipType", attributeType.getType().name().toLowerCase());
            }

            private void attachEcqlFilterStuffIfApplicable() {
                if (attribute.hasFilter()) {
                    EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(getEcqlExpressionFromAttributeFilter(attribute));
                    String ecqlId;
                    if (isNullOrLtEqZero(attribute.getOwner().getId())) {
                        ecqlId = EcqlUtils.buildEmbeddedEcqlId(attribute.getFilter());
                    } else {
                        ecqlId = EcqlUtils.buildAttrEcqlId(attribute);
                    }
                    map.put("ecqlFilter", map(
                            "id", ecqlId,
                            "bindings", map("server", ecqlBindingInfo.getServerBindings(), "client", ecqlBindingInfo.getClientBindings())
                    ));
                }
            }

        });
        return map;
    }

    private String getType(Classe classe) {
        return classe.isProcess() ? "process" : "class";
    }

}
