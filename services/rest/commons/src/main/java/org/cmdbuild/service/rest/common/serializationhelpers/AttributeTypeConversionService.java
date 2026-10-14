/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers;

import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.DecimalAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.IpAddressAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.NullAttributeTypeVisitor;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.QueryParam;
import static java.lang.String.format;
import java.util.Collection;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.calendar.CalendarTriggerInfo;
import org.cmdbuild.classe.access.UserClassService;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_INCLUDE_INACTIVE_ELEMENTS;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.paged;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Attribute;
import static org.cmdbuild.dao.entrytype.AttributePermission.*;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.serializeAttributePermissionMode;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.serializePermissions;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryTypeType;
import org.cmdbuild.dao.entrytype.attributetype.*;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import static org.cmdbuild.dao.entrytype.attributetype.UnitOfMeasureLocationUtils.serializeUnitOfMeasureLocation;
import static org.cmdbuild.dao.utils.RelationDirectionUtils.serializeRelationDirection;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.ecql.EcqlBindingInfo;
import org.cmdbuild.ecql.utils.EcqlUtils;
import static org.cmdbuild.ecql.utils.EcqlUtils.*;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.*;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.unkey;
import static org.cmdbuild.view.ViewService.ATTR_DESCR_INHERITED_FROM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class AttributeTypeConversionService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final BiMap<AttributeTypeName, String> TYPE_NAME_MAP = ImmutableBiMap.copyOf(map(
            AttributeTypeName.BOOLEAN, TYPE_BOOLEAN,
            AttributeTypeName.CHAR, TYPE_CHAR,
            AttributeTypeName.DATE, TYPE_DATE,
            AttributeTypeName.TIMESTAMP, TYPE_DATE_TIME,
            AttributeTypeName.DECIMAL, TYPE_DECIMAL,
            AttributeTypeName.DOUBLE, TYPE_DOUBLE,
            AttributeTypeName.FLOAT, TYPE_FLOAT,
            AttributeTypeName.REGCLASS, TYPE_ENTRY_TYPE,
            AttributeTypeName.FOREIGNKEY, TYPE_FOREIGN_KEY,
            AttributeTypeName.INTEGER, TYPE_INTEGER,
            AttributeTypeName.INTERVAL, TYPE_INTERVAL,
            AttributeTypeName.LONG, TYPE_LONG,
            AttributeTypeName.LINK, TYPE_LINK,
            AttributeTypeName.FORMULA, TYPE_FORMULA,
            AttributeTypeName.FILE, TYPE_FILE,
            AttributeTypeName.LOOKUPARRAY, TYPE_LOOKUP_ARRAY,
            AttributeTypeName.INET, TYPE_IP_ADDRESS,
            AttributeTypeName.LOOKUP, TYPE_LOOKUP,
            AttributeTypeName.REFERENCE, TYPE_REFERENCE,
            AttributeTypeName.REFERENCEARRAY, TYPE_REFERENCE_ARRAY,
            AttributeTypeName.STRINGARRAY, TYPE_STRING_ARRAY,
            AttributeTypeName.BYTEARRAY, TYPE_BYTE_ARRAY, // (in postgres is a bytea, see SqlQueryUtils)
            AttributeTypeName.BYTEAARRAY, TYPE_BYTEA_ARRAY, // (in postgres is a _bytea, see SqlQueryUtils)
            AttributeTypeName.STRING, TYPE_STRING,
            AttributeTypeName.GEOMETRY, TYPE_GEOMETRY,
            AttributeTypeName.JSON, TYPE_JSON,
            AttributeTypeName.TEXT, TYPE_TEXT,
            AttributeTypeName.TIME, TYPE_TIME));

    private final DaoService dao;
    private final ObjectTranslationService translationService;
    private final UserClassService classService;
    private final UserDomainService userDomainService;
    private final CalendarWsSerializationHelper calendarHelper;
    private final LookupService lookupService;
    private final DmsService dmsService;

    public AttributeTypeConversionService(
            DaoService dao,
            ObjectTranslationService translationService,
            UserClassService classService,
            UserDomainService userDomainService,
            CalendarWsSerializationHelper calendarHelper,
            LookupService lookupService,
            DmsService dmsService) {
        this.dao = checkNotNull(dao);
        this.translationService = checkNotNull(translationService);
        this.classService = checkNotNull(classService);
        this.userDomainService = checkNotNull(userDomainService);
        this.calendarHelper = checkNotNull(calendarHelper);
        this.lookupService = checkNotNull(lookupService);
        this.dmsService = checkNotNull(dmsService);
    }

    public PagedElements serializeReportAttributeIterable(Iterable<Attribute> attributeIterable, String reportCode, Integer limit, Integer offset) {
        return paged(attributeIterable, (a) -> serializeAttributeType(a).with(
                "_description_translation", translationService.translateReportAttributeDescription(reportCode, a.getName(), a.getDescription())
        ), offset, limit);
    }

    public PagedElements serializeAndSort(List<Attribute> attributeList, Integer limit, @QueryParam(START) Integer offset, boolean showNoActive) {
        List<FluentMap<String, Object>> listFluentMap = attributeList.stream().sorted((a, b) -> Integer.compare(a.getIndex(), b.getIndex())).map(a -> serializeAttributeType(a, showNoActive)).collect(toList());
        return paged(listFluentMap, offset, limit);
    }

    public List<CmMapUtils.FluentMap<String, Object>> serialize(Collection<Attribute> attributesForView) {
        return attributesForView.stream().map(a -> serializeAttributeType(a).accept(m -> {
            if (a.getMetadata().hasValue(ATTR_DESCR_INHERITED_FROM)) {
                List<String> info = unkey(a.getMetadata().get(ATTR_DESCR_INHERITED_FROM));
                m.put("_description_translation", translationService.translateAttributeDescription(dao.getType(parseEnum(info.get(0), EntryTypeType.class), info.get(1)).getAttribute(info.get(2))));//TODO improve this
            }
        })).collect(toList());
    }

    public static String serializeAttributeType(AttributeTypeName name) {
        return checkNotNull(TYPE_NAME_MAP.get(name), "unsupported attr type = %s", name);
    }

    public FluentMap<String, Object> serializeAttributeType(Attribute attribute) {
        return serializeAttributeType(attribute, true);
    }

    public FluentMap<String, Object> serializeAttributeType(Attribute attribute, boolean showNoActive) {
        return new AttrSerializerHelper(attribute, showNoActive).serializeAttributeType();
    }

    public List serializeParametersList(List<Attribute> parameters) {
        return list(parameters)
                .mapWithIndex((index, attr) -> map(
                "_id", attr.getName(),
                "type", AttributeTypeConversionService.serializeAttributeType(attr.getType().getName()),
                "name", attr.getName(),
                "description", attr.getName(),
                "active", true,
                "index", index))
                .collect(toList());
    }

    public Object serializeParameter(Attribute param, String functionId) {
        return map(serializeAttributeType(param)).with("_description_translation", translationService.translateFunctionAttributeDescription(functionId, param.getName(), param.getName()));
    }

    public Object serializeResponse(Collection<Attribute> parameters, String funName, @Nullable Integer limit, @Nullable Integer offset) {
        return response(paged(parameters.stream().map(p -> serializeParameter(p, funName)).collect(toList()), offset, limit));
    }

    private class AttrSerializerHelper {

        private final Attribute attribute;
        private final boolean showNoActive;

        public AttrSerializerHelper(Attribute attribute, boolean showNoActive) {
            this.attribute = attribute;
            this.showNoActive = showNoActive;
        }

        public FluentMap<String, Object> serializeAttributeType() {
            return (FluentMap) map(
                    "_id", attribute.getName(),
                    "type", AttributeTypeConversionService.serializeAttributeType(attribute.getType().getName()),
                    "name", attribute.getName(),
                    "description", attribute.getDescription(),
                    "_description_translation", translationService.translateAttributeDescription(attribute),
                    "showInGrid", attribute.showInGrid(),
                    "showInReducedGrid", attribute.showInReducedGrid(),
                    "unique", attribute.isUnique(),
                    "mandatory", attribute.isMandatory(),
                    "inherited", attribute.isInherited(),
                    "active", attribute.isActive(),
                    "index", attribute.getIndex(),
                    "defaultValue", attribute.getDefaultValue(),
                    "group", attribute.getGroupNameOrNull(),
                    "_group_description", attribute.getGroupDescriptionOrNull(),
                    "_group_description_translation", attribute.hasGroup() ? (attribute.getOwner().isView() ? translationService.translateViewAttributeGroupDescription(attribute.getOwner().getName(), attribute.getGroupName(), attribute.getGroupDescriptionOrNull()) : translationService.translateAttributeGroupDescription(attribute.getOwner(), attribute.getGroup())) : null,
                    "mode", serializeAttributePermissionMode(attribute.getMode()),
                    "writable", attribute.hasUiPermission(AP_CREATE) || attribute.hasUiPermission(AP_UPDATE),
                    "immutable", attribute.hasUiPermission(AP_CREATE) && !attribute.hasUiPermission(AP_UPDATE),
                    "hidden", !attribute.hasUiReadPermission(),
                    "_can_read", attribute.hasServiceReadPermission(),
                    "_can_create", attribute.hasServicePermission(AP_CREATE),
                    "_can_update", attribute.hasServicePermission(AP_UPDATE),
                    "_can_modify", attribute.hasServicePermission(AP_MODIFY),
                    "metadata", attribute.getMetadata().getCustomMetadata(),
                    "help", attribute.getMetadata().getHelpMessage(),
                    "showIf", attribute.getMetadata().getShowIfExpr(),
                    "validationRules", attribute.getMetadata().getValidationRulesExpr(),
                    "autoValue", attribute.getMetadata().getAutoValueExpr(),
                    "alias", attribute.getMetadata().getUiAlias(),
                    "syncToDmsAttr", attribute.getMetadata().getSyncToDmsAttr(),
                    "helpAlwaysVisible", attribute.getMetadata().helpAlwaysVisible(),
                    "hideInFilter", attribute.isHiddenInFilter(),
                    "hideInGrid", attribute.isHiddenInGrid(),
                    "virtual", attribute.isVirtual(),
                    "sortingEnabled", attribute.isSortable())
                    .accept((m) -> {
                        if (attribute.hasServiceModifyPermission()) {
                            m.put("_permissions", serializePermissions(attribute));
                        }
                        if (attribute.getOwner().isDomain()) {
                            m.put("domainKey", attribute.isDomainKey());
                        }
                    })
                    .with(serializeAttributeSpecificValues(attribute));
        } // end serializeAttributeType method

        private FluentMap<String, Object> serializeAttributeSpecificValues(Attribute attribute) {
            FluentMap<String, Object> map = map();
            attribute.getType().accept(new NullAttributeTypeVisitor() {
                @Override
                public void visit(DecimalAttributeType attributeType) {
                    map.put("precision", attributeType.getPrecision(), "scale", attributeType.getScale());
                }

                @Override
                public void visit(ForeignKeyAttributeType attributeType) {
                    Classe classe = dao.getClasse(attributeType.getForeignKeyDestinationClassName());
                    map.put("targetClass", classe.getName(),
                            "targetType", getType(classe),
                            "cascadeAction", serializeEnum(attributeType.getForeignKeyCascadeAction()),
                            "filter", attribute.getFilter(),
                            "isMasterDetail", attribute.getMetadata().isMasterDetail(),
                            "masterDetailDescription", attribute.getMetadata().getMasterDetailDescription());
                    attachEcqlFilterStuffIfApplicable(map, attribute);
                }

                @Override
                public void visit(LookupAttributeType attributeType) {
                    map.put("lookupType", attributeType.getLookupTypeName(),
                            "filter", attribute.getFilter());
                    attachEcqlFilterStuffIfApplicable(map, attribute);
                }

                @Override
                public void visit(LookupArrayAttributeType attributeType) {
                    map.put("lookupType", attributeType.getLookupTypeName(),
                            "filter", attribute.getFilter());
                    attachEcqlFilterStuffIfApplicable(map, attribute);
                }

                @Override
                public void visit(ReferenceAttributeType attributeType) {
                    Domain domain = userDomainService.getDomain(attributeType.getDomainName());
                    Classe target = domain.getReferencedClass(attributeType);
                    map.put("domain", domain.getName(),
                            "direction", serializeRelationDirection(attributeType.getDirection()),
                            "targetClass", target.getName(),
                            "targetType", getType(target),
                            "filter", attribute.getFilter(),
                            "useDomainFilter", attribute.getMetadata().isUseDomainFilter());
                    attachEcqlFilterStuffIfApplicable(map, attribute, domain);
                }

                @Override
                public void visit(TextAttributeType attributeType) {
                    map.put("language", serializeEnum(attributeType.getLanguage()),
                            "editorType", serializeEnumUpper(attribute.getEditorType()));
                }

                @Override
                public void visit(IpAddressAttributeType attributeType) {
                    map.put("ipType", attributeType.getType().name().toLowerCase());
                }
            });
            switch (attribute.getType().getName()) {
                case REFERENCE, FOREIGNKEY, LOOKUP, LOOKUPARRAY ->
                    map.put("preselectIfUnique", attribute.getMetadata().preselectIfUnique());
                case TIME, TIMESTAMP ->
                    map.put("showSeconds", attribute.getMetadata().showSeconds(),
                            "formatPattern", attribute.getMetadata().getFormatPattern());
                case DATE ->
                    map.put("formatPattern", attribute.getMetadata().getFormatPattern());
                case DECIMAL, DOUBLE, FLOAT ->
                    map.put(
                            "visibleDecimals", attribute.getMetadata().getVisibleDecimals(),
                            "unitOfMeasure", attribute.getMetadata().getUnitOfMeasure(),
                            "unitOfMeasureLocation", serializeUnitOfMeasureLocation(attribute.getMetadata().getUnitOfMeasureLocation()),
                            "showSeparators", attribute.getMetadata().showSeparators(),
                            "showThousandsSeparator", attribute.getMetadata().showThousandsSeparator(),
                            "formatPattern", attribute.getMetadata().getFormatPattern());
                case INTEGER, LONG ->
                    map.put(
                            "unitOfMeasure", attribute.getMetadata().getUnitOfMeasure(),
                            "unitOfMeasureLocation", serializeUnitOfMeasureLocation(attribute.getMetadata().getUnitOfMeasureLocation()),
                            "showSeparators", attribute.getMetadata().showSeparators(),
                            "showThousandsSeparator", attribute.getMetadata().showThousandsSeparator(),
                            "formatPattern", attribute.getMetadata().getFormatPattern());
                case STRING ->
                    map.put(
                            "password", attribute.getMetadata().isPassword(),
                            "showPassword", serializeEnum(attribute.getMetadata().getShowPassword()));
                case LINK ->
                    map.put(
                            "showLabel", attribute.getMetadata().showLabel(),
                            "labelRequired", attribute.getMetadata().labelRequired());
                case FILE -> {
                    LookupValue lookupValue = lookupService.getLookupByTypeAndCodeOrDescriptionOrId(dmsService.getCategoryLookupType(attribute.getOwnerClass()).getName(),
                            attribute.getMetadata().getDmsCategory());
                    map.put(
                            "dmsCategory", attribute.getMetadata().getDmsCategory(),
                            "dmsModel", lookupValue.getDmsModelClass(),
                            "_dmsCategory_description", lookupValue.getDescription(),
                            "showPreview", attribute.getMetadata().showPreview());
                }
                case FORMULA ->
                    map.put(
                            "formulaType", serializeEnum(attribute.getMetadata().getFormulaType()),
                            "formulaCode", attribute.getMetadata().getFormulaCode());
            }
            switch (attribute.getType().getName()) {
                case STRING, STRINGARRAY, TEXT ->
                    map.put(
                            "textContentSecurity", serializeEnum(attribute.getMetadata().getTextContentSecurity()),
                            "_html", attribute.isHtmlSafe(),
                            "maxLength", firstNotNull(attribute.getMaxLength(), Integer.MAX_VALUE),//TODO check this
                            "multiline", attribute.isMultiline(),
                            "trimEnabled", attribute.isTrimEnabled(),
                            "anonymizable", attribute.isAnonymizable());
                case LOOKUP, LOOKUPARRAY ->
                    map.put("_html", true,
                            "showOnlyIconIfPresent", attribute.isShowOnlyIconIfPresent());
                default ->
                    map.put("_html", false);
            }
            switch (attribute.getType().getName()) {
                case STRING, TEXT, DECIMAL, DOUBLE, FLOAT, INTEGER, LONG ->
                    map.put(
                            "mobileEditor", serializeEnum(attribute.getMetadata().getMobileEditor()),
                            "mobileEditorRegex", attribute.getMetadata().getMobileEditorRegex()
                    );
            }
            switch (attribute.getType().getName()) {
                case TIMESTAMP, DATE -> {
                    if (attribute.getOwner().isRegularClass()) {//TODO improve this
                        List<CalendarTriggerInfo> calendarTriggers = classService.getExtendedClass(attribute.getOwnerClass(), CQ_INCLUDE_INACTIVE_ELEMENTS).getCalendarTriggersForAttr(attribute.getName());
                        map.put("calendarTriggers", calendarTriggers.stream().filter((t) -> showNoActive || t.isActive()).map(calendarHelper::serializeDetailedTrigger).collect(toList()));
                    }
                }
            }
            return map;
        } // end serializeAttributeSpecificValues method

    } // end AttrSerializerHelper class

    private void attachEcqlFilterStuffIfApplicable(final FluentMap<String, Object> map, Attribute attribute) {
        attachEcqlFilterStuffIfApplicable(map, attribute, null);
    }

    private void attachEcqlFilterStuffIfApplicable(final FluentMap<String, Object> map, Attribute attribute, Domain domain) {
        if (attribute.isOfType(REFERENCE) && attribute.getMetadata().isUseDomainFilter()) {
            checkArgument(domain != null, "domain not found for attribute %s", attribute.getName());
            String filterSide = domain.getFilterSide(attribute);
            String filter = domain.getFilterFromFilterSide(filterSide);
            if (isNotBlank(filter)) {
                EcqlBindingInfo ecqlBindingInfo = getEcqlBindingInfoForExpr(getEcqlExpression(domain.getMetadata(), filter));
                String ecqlId = buildDomainEcqlId(domain, filterSide);
                EcqlFilterSerializationHelper.addEcqlFilter(map, "ecqlFilter", ecqlId, ecqlBindingInfo);
            }
        } else if (attribute.hasFilter()) {
            logger.debug("attaching ecql filter stuff serialization for attribute < {} >", attribute.getName());
            EcqlBindingInfo ecqlBindingInfo = EcqlUtils.getEcqlBindingInfoForExpr(getEcqlExpressionFromAttributeFilter(attribute));

            String ecqlId = EcqlUtils.buildAttrEcqlId(attribute);

            checkNotNull(ecqlId, format("while handling a attribute filter, unhandled attribute type %s", attribute.getOwner().getEtType()));

            EcqlFilterSerializationHelper.addEcqlFilter(map, "ecqlFilter", ecqlId, ecqlBindingInfo);
        }
    }

    private static String getType(Classe classe) {
        return classe.isProcess() ? "process" : "class";
    }

}
