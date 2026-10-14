/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v2.serializationhelpers;

import com.google.common.base.Function;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Lists.transform;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.LookupValue;
import static org.cmdbuild.dao.utils.RelationDirectionUtils.serializeRelationDirection;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeMetadata;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.ShowPassword.SP_ALWAYS;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.ShowPassword.SP_NEVER;
import static org.cmdbuild.dao.entrytype.AttributeMetadata.ShowPassword.SP_ONWRITEACCESS;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_DELETE;
import static org.cmdbuild.dao.entrytype.ClassPermission.CP_UPDATE;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import static org.cmdbuild.service.rest.v2.serializationhelpers.CardWsSerializationHelperV2.ExtendedCardOptions.INCLUDE_MODEL;
import static org.cmdbuild.service.rest.v2.utils.WsAttributeConverterUtilsv2.toClient;
import org.cmdbuild.translation.ObjectTranslationService;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.date.CmDateUtils.systemZoneId;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

@Component
public class CardWsSerializationHelperV2 {

    private final List<String> CARD_ATTR_EXT_META = ImmutableList.of("_changed", "_previous");

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ObjectTranslationService translationService;
    private final ClassSerializationHelperv2 classSerializationHelper;
    private final AttributeTypeConversionServicev2 attributeTypeConversionService;

    public CardWsSerializationHelperV2(ObjectTranslationService translationService, ClassSerializationHelperv2 classSerializationHelper, AttributeTypeConversionServicev2 attributeTypeConversionService) {
        this.translationService = checkNotNull(translationService);
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
    }

    public enum ExtendedCardOptions {
        NONE, INCLUDE_MODEL
    }

    public CmMapUtils.FluentMap<String, Object> serializeMinimalRelation(CMRelation relation) {
        return map("_id", relation.getId(),
                "_type", relation.getType().getName(),
                "_user", relation.getUser(),
                "_beginDate", formatDate(relation.getBeginDate()),
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
        return serializeCard(card, set(extendedCardOptions));
    }

    public FluentMap<String, Object> serializeCard(Card card, Set<ExtendedCardOptions> extendedCardOptions) {
        try {
            return (FluentMap) map("_id", card.getId(),
                    "_type", card.getType().getName(),
                    "_user", card.getUser(),
                    "_beginDate", formatDate(card.getBeginDate())
            ).accept((m) -> {
                if (card.getType().hasMultitenantEnabled()) {
                    m.put("_tenant", card.getTenantId());
                }
                addCardValuesAndDescriptionsAndExtras(card, m::put);
                if (extendedCardOptions.contains(INCLUDE_MODEL)) {
                    m.put("_model", classSerializationHelper.buildBasicResponse(card.getType())
                            .with("attributes", list(transform(card.getType().getServiceAttributes(), attributeTypeConversionService::serializeAttributeType))));
                }
            });
        } catch (Exception ex) {
            throw runtime(ex, "error serializing card = %s", card);
        }
    }

    public void addCardValuesAndDescriptionsAndExtras(DatabaseRecord card, BiConsumer<String, Object> adder) {
        addCardValuesAndDescriptionsAndExtras(card.getType().getServiceAttributes(), card::get, adder);
    }

    public void addCardValuesAndDescriptionsAndExtras(Collection<Attribute> attributes, Function<String, Object> getter, BiConsumer<String, Object> adder) {
        attributes.stream().filter(Attribute::hasServiceReadPermission).forEach((a) -> {
            addCardValuesAndDescriptionsAndExtras(a.getName(), a.getType(), a.getMetadata(), getter, adder);
        });
    }

    public FluentMap<Object, Object> serializeAttributeValue(EntryType classe, String attributeName, Map<String, Object> data) {
        return map().accept(m -> {
            Attribute attribute = classe.getAttribute(attributeName);
            addCardValuesAndDescriptionsAndExtras(attribute.getName(), attribute.getType(), attribute.getMetadata(), data::get, m::put);
        });
    }

    public Object formatDate(Object value) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        ZonedDateTime date = CmDateUtils.toDateTime(value);
        return date != null ? date.withZoneSameInstant(systemZoneId()).format(formatter) : null;
    }

    public void addCardValuesAndDescriptionsAndExtras(String name, CardAttributeType type, @Nullable AttributeMetadata attributeMetadata, Function<String, Object> getter, BiConsumer<String, Object> adder) {
        Object rawValue = "<undefined>";
        try {
            rawValue = getter.apply(name);
            Object value = toClient(type, rawValue);
            if (attributeMetadata != null && attributeMetadata.isPassword()) {
                adder.accept(format("_%s_has_value", name), isNotBlank(toStringOrNull(value)));
                switch (attributeMetadata.getShowPassword()) {
                    case SP_ALWAYS, SP_NEVER, SP_ONWRITEACCESS -> {
                        return; //TODO excluded, implement them on rest v2?
                    }
                    default ->
                        throw new UnsupportedOperationException();
                }
            }
            adder.accept(name, value);
            switch (type.getName()) {
                case REFERENCE, FOREIGNKEY, LOOKUP -> {
                    if (rawValue instanceof IdAndDescription idAndDescription) {
                        adder.accept(format("_%s_code", name), idAndDescription.getCode());
                        adder.accept(format("_%s_description", name), idAndDescription.getDescription());
                    }
                    if (rawValue instanceof LookupValue lookupValue) {
                        LookupValue lookup = lookupValue;
                        adder.accept(format("_%s_description_translation", name), translationService.translateLookupDescriptionSafe(lookup.getLookupType(), lookup.getCode(), lookup.getDescription()));
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
            throw runtime(ex, "error processing attr = %s of type = %s with value = %s", name, type, rawValue);
        }
    }

}
