/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.common.serializationhelpers.card;

import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.AttributePermissions;
import org.cmdbuild.dao.entrytype.ClassPermission;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.cmdbuild.auth.user.UserRepository;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.LookupValue;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.*;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.TextAttributeType;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import org.cmdbuild.gis.CmGeometry;
import org.cmdbuild.gis.utils.GisUtils;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.PermissionsHandlerProxyImpl;
import org.cmdbuild.services.permissions.DummyPermissionsHandler;
import org.cmdbuild.services.permissions.PermissionsHandlerProxy;
import org.cmdbuild.services.serialization.CardAttributeSerializerAdapter;
import org.cmdbuild.services.serialization.attribute.file.CardAttributeFileFullWsSerializationHelper;
import org.cmdbuild.services.serialization.attribute.file.CardAttributeFileHelper;
import org.cmdbuild.services.serialization.attribute.file.CardAttributeFileSerializationData;
import org.cmdbuild.services.serialization.attribute.file.CardAttributeFileWsSerializationHelper;
import org.cmdbuild.services.serialization.widget.WidgetHelper;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.html.HtmlSanitizerUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.cmdbuild.utils.lang.CmNullableUtils;
import org.cmdbuild.widget.WidgetService;
import org.cmdbuild.widget.model.Widget;
import org.cmdbuild.widget.model.WidgetData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Lists.transform;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.dao.constants.SystemAttributes.*;
import static org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.emptyOptions;
import static org.cmdbuild.dao.entrytype.AttributePermission.AP_WRITE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import static org.cmdbuild.dao.entrytype.TextContentSecurity.TCS_HTML_SAFE;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.buildReferenceAttrName;
import static org.cmdbuild.dao.utils.RelationDirectionUtils.serializeRelationDirection;
import static org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeConverterUtilsv3.toClient;
import static org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3.ExtendedCardOptions.INCLUDE_MODEL;
import static org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3.ExtendedCardOptions.SKIP_SYSTEM_ATTRS;
import static org.cmdbuild.service.rest.common.utils.WsSerializationUtils.serializeGeometry;
import static org.cmdbuild.service.rest.common.utils.WsSerializationUtils.userDescription;
import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullNorBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.*;
import static org.cmdbuild.view.ViewService.JOIN_VIEW_ATTR_JOIN_ID;

@Component
public class CardWsSerializationHelperv3 {

    private final List<String> CARD_ATTR_EXT_META = ImmutableList.of("_changed", "_previous");
    private final Set<String> SPECIAL_ATTRS = ImmutableSet.of(ATTR_IDTENANT, ATTR_IDCLASS, ATTR_ID);//TODO improve this

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;
    private final ObjectTranslationService translationService;
    private final ClassSerializationHelper classSerializationHelper;
    private final AttributeTypeConversionService attributeTypeConversionService;
    private final WidgetService widgetService;
    private final UserRepository userRepository;
    private final CardAttributeFileHelper cardAttributeFile_helper;
    private final WidgetHelper widgets_helper;
    private final PermissionsHandlerProxy permissionsHandler;

    @Autowired
    public CardWsSerializationHelperv3(DaoService dao, ObjectTranslationService translationService, ClassSerializationHelper classSerializationHelper, AttributeTypeConversionService attributeTypeConversionService, WidgetService widgetService, UserRepository userRepository, CardAttributeFileHelper cardAttributeFile_helper, WidgetHelper widgets_helper) {
        this(dao, translationService, classSerializationHelper, attributeTypeConversionService, widgetService, userRepository, cardAttributeFile_helper, widgets_helper, new PermissionsHandlerProxyImpl()); // handle permissions
    }

    /**
     *
     * @param dao
     * @param translationService
     * @param classSerializationHelper
     * @param attributeTypeConversionService
     * @param widgetService
     * @param userRepository
     * @param cardAttributeFile_helper       handles fetching from DMS service and
     *                                       serialization into a map
     * @param widgets_helper                 handlers fetching from Widgets service and
     *                                       serialization into map
     * @param permissionsHandler             use {@link DummyPermissionsHandler} to skip all
     *                                       permissions handling
     */
    @VisibleForTesting
    protected CardWsSerializationHelperv3(DaoService dao, ObjectTranslationService translationService, ClassSerializationHelper classSerializationHelper, AttributeTypeConversionService attributeTypeConversionService, WidgetService widgetService, UserRepository userRepository, CardAttributeFileHelper cardAttributeFile_helper, WidgetHelper widgets_helper, PermissionsHandlerProxy permissionsHandler) {
        this.dao = checkNotNull(dao);
        this.translationService = checkNotNull(translationService);
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
        this.widgetService = checkNotNull(widgetService);
        this.userRepository = checkNotNull(userRepository);
        this.cardAttributeFile_helper = checkNotNull(cardAttributeFile_helper);
        this.widgets_helper = checkNotNull(widgets_helper);
        this.permissionsHandler = checkNotNull(permissionsHandler);
    }

    public enum ExtendedCardOptions {
        NONE, INCLUDE_MODEL, SKIP_SYSTEM_ATTRS
    }

    public FluentMap<String, Object> serializeBasicHistory(Card record) {
        return map(
                "_type", record.getType().getName(),
                "_id", record.getId(),
                "_endDate", toIsoDateTime(record.getEndDate()),
                "_beginDate", toIsoDateTime(record.getBeginDate()),
                "_user", record.getUser(),
                "__user_description", userDescription(userRepository, record.getUser()),
                "_status", record.getCardStatus().name());
    }

    public Consumer<FluentMap<String, Object>> serializeWidgets(Card card) {
        return m -> {
            List<WidgetData> classWidgets = widgetService.getAllWidgetsForClass(card.getType()).stream()
                    .filter(w -> permissionsHandler.cardWsSerializationHelperv3_isWidgetEnabled(card, w)).collect(toList());
            List<Widget> cardWidgets = widgetService.widgetDataToWidget(card.getClassName(), null, classWidgets, card.toMap());
            m.put("_widgets", cardWidgets.stream().map(e -> classSerializationHelper.serializeWidget(e, card.getTypeName())).collect(toList()));
        };
    }

    public FluentMap<String, Object> serializeMinimalRelation(CMRelation relation) {
        return map("_id", relation.getId(),
                "_type", relation.getType().getName(),
                "_user", relation.getUser(),
                "__user_description", userDescription(userRepository, relation.getUser()),
                "_beginDate", toIsoDateTime(relation.getBeginDate()),
                "_sourceType", relation.getSourceCard().getClassName(),
                "_sourceId", relation.getSourceId(),
                "_sourceDescription", relation.getSourceDescription(),//TODO translation
                "_sourceCode", relation.getSourceCode(),
                "_destinationType", relation.getTargetCard().getClassName(),
                "_destinationId", relation.getTargetId(),
                "_destinationCode", relation.getTargetCode(),
                "_destinationDescription", relation.getTargetDescription(),//TODO translation
                "_direction", serializeRelationDirection(relation.getDirection()),
                "_is_direct", relation.isDirect(),
                "_can_update", relation.getType().hasServicePermission(CP_UPDATE),
                "_can_delete", relation.getType().hasServicePermission(CP_DELETE)
        );
    }

    public CmMapUtils.FluentMap<String, Object> serializeDetailedRelation(CMRelation relation) {
        return serializeMinimalRelation(relation).accept((map) -> {
            addCardValuesAndDescriptionsAndExtras(relation, map::put);
        });
    }

    public FluentMap<String, Object> serializeCard(Card card, ExtendedCardOptions... extendedCardOptions) {
        return serializeCard(card, emptyOptions(), set(extendedCardOptions));
    }

    public FluentMap<String, Object> serializeCard(Card card, DaoQueryOptions queryOptions, ExtendedCardOptions... extendedCardOptions) {
        return serializeCard(card, queryOptions, set(extendedCardOptions));

    }

    public FluentMap<String, Object> serializeCard(Card card, Set<ExtendedCardOptions> extendedCardOptions) {
        return serializeCard(card, emptyOptions(), extendedCardOptions);

    }

    public FluentMap<String, Object> serializeCard(Card card, DaoQueryOptions queryOptions, Set<ExtendedCardOptions> extendedCardOptions) {
        try {
            return mapOf(String.class, Object.class).accept((m) -> {
                if (!extendedCardOptions.contains(SKIP_SYSTEM_ATTRS)) {
                    m.put("_id", card.getId(),
                            "_type", card.getType().getName(),
                            //                            "_type_description", card.getType().getDescription(),
                            //                            "_type_description_translation", translationService.translateClassDescription(card.getType()),
                            "_user", card.getUser(),
                            "__user_description", userDescription(userRepository, card.getUser()),
                            "_beginDate", toIsoDateTime(card.getBeginDate()));
                    if (card.getType().hasMultitenantEnabled()) {
                        m.put("_tenant", card.getTenantId());
                    }
                }
            }).filterKeys(inCaseFilterOnSelectedAttributes(card, queryOptions)).accept((m) -> {
                addCardValuesAndDescriptionsAndExtras(card, a -> !queryOptions.hasAttrs() || queryOptions.getAttrs().contains(a.getName()), m::put);
                if (extendedCardOptions.contains(INCLUDE_MODEL)) {
                    m.put("_model", classSerializationHelper.buildBasicResponse(card.getType())
                            .with("attributes", list(transform(card.getType().getServiceAttributes(), attributeTypeConversionService::serializeAttributeType))));
                }
            });
        } catch (Exception ex) {
            throw runtime(ex, "error serializing card = %s", card);
        }
    }

    public FluentMap<String, Object> serializeCardWithValue(Card card) {
        return serializeCard(card).accept(m -> {
            if (card.hasValue(JOIN_VIEW_ATTR_JOIN_ID)) {
                m.put("_id", card.get(JOIN_VIEW_ATTR_JOIN_ID));
            }
        });
    }

    /**
     * (In case) filters only selected card attributes.
     *
     * @param card
     * @param queryOptions (in case) contains a subset of card attributes to
     *                     filter on.
     * @return
     */
    protected static com.google.common.base.Predicate<String> inCaseFilterOnSelectedAttributes(Card card, DaoQueryOptions queryOptions) {
        return k -> !queryOptions.hasAttrs() || queryOptions.getAttrs().contains(card.getType().getAliasToAttributeMap().getOrDefault(k, k));
    }

    public void addCardValuesAndDescriptionsAndExtras(DatabaseRecord card, BiConsumer<String, Object> adder) {
        addCardValuesAndDescriptionsAndExtras(card, Predicates.alwaysTrue(), adder);
    }

    public void addCardValuesAndDescriptionsAndExtras(DatabaseRecord card, Predicate<Attribute> attrFilter, BiConsumer<String, Object> adder) {
        addCardValuesAndDescriptionsAndExtras(card.getType().getServiceAttributes().stream().filter(attrFilter).collect(toImmutableList()), card::get, adder);
    }

    public void addCardValuesAndDescriptionsAndExtras(Collection<Attribute> attributes, Function<String, Object> getter, BiConsumer<String, Object> adder) {
        attributes.stream()
                .filter(Attribute::isActive)
                .filter(Attribute::hasServiceReadPermission)
                .filter(a -> !SPECIAL_ATTRS.contains(a.getName()))//TODO improve this
                .forEach((a) -> {
                    addCardValuesAndDescriptionsAndExtras(a, getter, adder);
                });
    }

    public FluentMap<String, Object> serializeAttributeValue(EntryType classe, String attributeName, Object value) {
        return serializeAttributeValue(classe, attributeName, map(attributeName, value));
    }

    public FluentMap<String, Object> serializeAttributeValue(EntryType classe, String attributeName, Map<String, Object> data) {
        return serializeAttributeValue(classe.getAttribute(attributeName), data);
    }

    public FluentMap<String, Object> serializeAttributeValue(Attribute attribute, Map<String, Object> data) {
        return mapOf(String.class, Object.class).accept(m -> {
            addCardValuesAndDescriptionsAndExtras(attribute, data::get, m::put);
        });
    }

    public void addCardValuesAndDescriptionsAndExtras(Attribute attribute, Function<String, Object> getter, BiConsumer<String, Object> adder) {
        addCardValuesAndDescriptionsAndExtras(attribute.getName(), attribute.getType(), attribute.getMetadata(), attribute, getter, adder, attribute.getOwner());
    }

    public void addCardValuesAndDescriptionsAndExtras(String name, CardAttributeType type, Function<String, Object> getter, BiConsumer<String, Object> adder) {
        addCardValuesAndDescriptionsAndExtras(name, type, null, null, getter, adder, null);
    }

    private void addCardValuesAndDescriptionsAndExtras(String name, CardAttributeType<?> type, @Nullable AttributeMetadata attributeMetadata, @Nullable AttributePermissions attributePermission, Function<String, Object> getter, BiConsumer<String, Object> adder, EntryType attributeClass) {
        Object rawValue = "<undefined>";
        try {
            rawValue = getter.apply(name);
            Object value = toClient(type, rawValue);
            if (attributeMetadata != null && attributeMetadata.isPassword()) {
                adder.accept(format("_%s_has_value", name), isNotBlank(toStringOrNull(value)));
                switch (attributeMetadata.getShowPassword()) {
                    case SP_ALWAYS -> {
                        break; //nothing to do
                    }
                    case SP_NEVER -> {
                        return; //skip password attrs
                    }
                    case SP_ONWRITEACCESS -> {
                        if (attributePermission != null && attributeClass != null && attributePermission.hasUiPermission(AP_WRITE)) {
                            //nothing to do
                            if (attributeClass.isDomain() && ((Domain) attributeClass).hasUiPermission(ClassPermission.CP_WRITE)) {
                                break;
                            } else if (attributeClass.isClasse() && ((Classe) attributeClass).hasUiPermission(ClassPermission.CP_WRITE)) {
                                break;
                            }
                            return;
                        } else {
                            return; //skip password attrs
                        }
                    }
                    default -> throw new UnsupportedOperationException();
                }
            }
            if (attributeMetadata != null && attributeMetadata.hasTextContentSecurity(TCS_HTML_SAFE)) {
                value = HtmlSanitizerUtils.sanitizeHtml(toStringOrNull(value));
            }
            adder.accept(name, value);
            switch (type.getName()) {
                case REFERENCE, FOREIGNKEY, LOOKUP -> {
                    if (type.isOfType(REFERENCE)) {
                        Domain domain = dao.getDomain(type.as(ReferenceAttributeType.class).getDomainName()).getThisDomainWithDirection(type.as(ReferenceAttributeType.class).getDirection());
                        domain.getActiveServiceAttributes().stream().filter(Attribute::showInGrid).forEach(a -> {
                            String key = buildReferenceAttrName(name, a.getName());
                            addCardValuesAndDescriptionsAndExtras(key, a.getType(), a.getMetadata(), a, getter, adder, null);
                        });
                    }
                    if (rawValue instanceof IdAndDescription idAndDescription) {
                        adder.accept(format("_%s_code", name), idAndDescription.getCode());
                        adder.accept(format("_%s_description", name), idAndDescription.getDescription());
                    }
                    if (rawValue instanceof LookupValue lookupValue) {
                        LookupValue lookup = lookupValue;
                        if (lookup.hasCode()) {
                            adder.accept(format("_%s_description_translation", name), translationService.translateLookupDescriptionSafe(lookup.getLookupType(), lookup.getCode(), lookup.getDescription()));
                        }
                    }
                }
                case FILE -> {
                    if (CmNullableUtils.isNotBlank(value)) {
                        long cardDocId = toLong(value);

                        // Fetch
                        CardAttributeFileSerializationData serializationData = cardAttributeFile_helper.fetchDocument(name, attributeClass.getName(), cardDocId);
                        cardAttributeFile_helper.fetchCategory(serializationData);

                        // Serialize
                        Map<String, Object> serialization = cardAttributeFile_helper.serialize(serializationData, new CardAttributeFileWsSerializationHelper(new ThisAttributeSerializer()));
                        serialization.forEach(adder);
                    }
                }
                case LOOKUPARRAY -> {
                    if (rawValue instanceof Iterable iterable) {
                        adder.accept(format("_%s_details", name), list(iterable).map(v -> map().accept(m -> {
                            if (v instanceof IdAndDescription idAndDescription) {
                                m.put("_id", idAndDescription.getId(),
                                        "code", idAndDescription.getCode(),
                                        "description", idAndDescription.getDescription());
                            }
                            if (v instanceof LookupValue && ((LookupValue) v).hasCode()) {
                                m.put("_description_translation", translationService.translateLookupDescriptionSafe(((LookupValue) v).getLookupType(), ((LookupValue) v).getCode(), ((LookupValue) v).getDescription()));
                            }
                        })));
                    }
                }
                case TEXT -> {
                    switch (((TextAttributeType) type).getLanguage()) {
                        case TAL_HTML -> adder.accept(format("_%s_html", name), toStringOrEmpty(value));
                        case TAL_MARKDOWN ->
                                adder.accept(format("_%s_html", name), markdownToHtml(toStringOrEmpty(value)));
                        //TODO handle others
                    }
                }
                case GEOMETRY -> {
                    if (isNotNullNorBlank(value)) {
                        CmGeometry geometry = GisUtils.parseGeometry(toStringNotBlank(value));
                        adder.accept(format("_%s_%s", name, serializeEnum(geometry.getType())), serializeGeometry(geometry));
                    }
                }
            }
            CARD_ATTR_EXT_META.stream().map((e) -> format("_%s%s", name, e)).forEach((n) -> {
                Object v = getter.apply(n);
                if (v != null) {
                    adder.accept(n, v);
                }
            });
        } catch (Exception ex) {
            logger.debug("error processing attr = {} of type = {} - {}", name, type, ex);
            throw runtime(ex, "error processing attr = %s of type = %s with value = %s", name, type, rawValue);
        }
    }

    /**
     * Serializes:
     * <ul>
     * <li>document, with <code>_author_description</code>;
     * <li>(possibly present) related card (id) in <code>card</code> and related
     * attributes;
     * <li>(if active) category, with further details on permissions and
     * translations.
     * </ul>
     *
     * @param classId
     * @param input
     * @return
     */
    public Map<String, Object> serializeAttachment(String classId, DocumentInfoAndDetail input) {
        // Fetch
        CardAttributeFileSerializationData serializationData = cardAttributeFile_helper.initSerializationData(classId, input);
        cardAttributeFile_helper.fetchCategory(serializationData);

        // Serialize
        Map<String, Object> serialization = cardAttributeFile_helper.serialize(serializationData,
                        new CardAttributeFileWsSerializationHelper(new ThisAttributeSerializer())) // No Widgets serialization
                .withoutKeys("isDmsServiceOk");
        return serialization;
    }

    /**
     * Serialization of {@link CardAttributeFileWsSerializationHelper} plus:
     * <ul>
     * <li>document (hash) <code>_&lt;attributeName&gt;__id</code>;
     * <li>related card (id) in <code>_&lt;attributeName&gt;__card</code>;
     * <li>(possibly present) widgets.
     * </ul>
     *
     * @param classId
     * @param input
     * @param includeWidgets
     * @return
     */
    public Map<String, Object> serializeAttachment_FullDetail(String classId, DocumentInfoAndDetail input, boolean includeWidgets) {
        // Fetch
        CardAttributeFileSerializationData serializationData = cardAttributeFile_helper.initSerializationData(classId, input);
        cardAttributeFile_helper.fetchCategory(serializationData);
        if (includeWidgets) {
            cardAttributeFile_helper.fetchWidgets(serializationData, widgets_helper);
        }

        // Serialize
        Map<String, Object> serialization = cardAttributeFile_helper.serialize(serializationData,
                        new CardAttributeFileFullWsSerializationHelper(new ThisAttributeSerializer())) // With full details and (possibly present) Widgets serialization
                .withoutKeys("isDmsServiceOk");
        return serialization;
    }

    private static String markdownToHtml(@Nullable String value) {
        if (isBlank(value)) {
            return value;
        } else {
            MutableDataSet options = new MutableDataSet();
            com.vladsch.flexmark.util.ast.Document document = Parser.builder(options).build().parse(value);
            return HtmlRenderer.builder(options).build().render(document);
        }
    }

    /**
     * Handler for recursion on this class, when used in Card attribute
     * serialization
     */
    class ThisAttributeSerializer implements CardAttributeSerializerAdapter {

        @Override
        public FluentMap<String, Object> serialize(Attribute attribute, Map<String, Object> cardData) {
            return CardWsSerializationHelperv3.this.serializeAttributeValue(attribute, cardData);
        }
    }

} // end CardWsSerializationHelperv3 class
