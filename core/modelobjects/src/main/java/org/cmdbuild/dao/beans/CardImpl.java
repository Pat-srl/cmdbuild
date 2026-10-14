/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import com.google.common.base.MoreObjects;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DatabaseRecord;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CURRENTID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ENDDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDTENANT;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public class CardImpl implements Card {

    private final Long tenantId, id, currentId;
    private final Classe type;
    private final String code, description, user;
    private final ZonedDateTime beginDate, endDate;
    private final CardStatus status;//TODO get status
    private final Map<String, Object> attributes;

    private CardImpl(CardImplBuilder builder) {
        this.type = checkNotNull(builder.type, "card type cannot be null");
        this.attributes = map(builder.rawAttributes).mapKeys(type.getAliasToAttributeMap()::get).mapValues((key, value) -> {
            Attribute attribute = type.getAttributeOrNull(key);
            if (attribute != null) {
                value = fetchAttributeValue(value, attribute);
            }
            return value;
        }).immutable();
        this.id = attributes.get(ATTR_ID) instanceof BigInteger ? null : convert(attributes.get(ATTR_ID), Long.class); //TODO check this
        this.currentId = firstNotNullOrNull(convert(attributes.get(ATTR_CURRENTID), Long.class), id);
        this.tenantId = convert(attributes.get(ATTR_IDTENANT), Long.class);
        code = nullToEmpty(convert(attributes.get(ATTR_CODE), String.class));
        description = nullToEmpty(convert(attributes.get(ATTR_DESCRIPTION), String.class));
        user = nullToEmpty(convert(attributes.get(ATTR_USER), String.class));
        beginDate = convert(attributes.get(ATTR_BEGINDATE), ZonedDateTime.class);
        endDate = convert(attributes.get(ATTR_ENDDATE), ZonedDateTime.class);
        status = switch (type.getClassType()) {
            case CT_STANDARD ->
                MoreObjects.firstNonNull(convert(attributes.get(ATTR_STATUS), CardStatus.class), endDate == null ? CardStatus.A : CardStatus.U);//N ?
            case CT_SIMPLE ->
                CardStatus.A;
        };

    }

    private Object fetchAttributeValue(Object value, Attribute attribute) throws RuntimeException {
        // Workaround for attribute of type FILE used in synthesized Card when emailing a report in import/export
        if (value != null && isSynthesized() && attribute.isOfType(AttributeTypeName.FILE)) {
            // Expected a documentId
            if (value instanceof String) {
                return toStringOrNull(value);
            } else if (value instanceof Long) {
                // cardId (as long) of document passed
                return value;
            } else if (value instanceof IdAndDescription valueIdDescr) {
                // cardId (as IdAndDescription) of document passed
                return valueIdDescr.getId();
            } else {
                throw runtime(new IllegalArgumentException("in synthesized card, a FILE attribute value is not a documentId or cardId as expected"), "error converting value for class = %s attribute = %s from value =< %s > ( %s )", type.getName(), attribute.getName(), toStringOrNull(value), getClassOfNullable(value).getName());
            }
        }

        try {
            value = rawToSystem(attribute, value);
        } catch (Exception ex) {
            throw runtime(ex, "error converting value for class = %s attribute = %s from value =< %s > ( %s )", type.getName(), attribute.getName(), toStringOrNull(value), getClassOfNullable(value).getName());
        }
        return value;
    }

    @Override
    public Classe getType() {
        return type;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Long getCurrentId() {
        return checkNotNull(currentId, "no id for this card (new card)");
    }

    @Override
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    public boolean hasId() {
        return id != null;
    }

    @Override
    @Nullable
    public Long getTenantId() {
        return tenantId;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public ZonedDateTime getBeginDate() {
        return beginDate;
    }

    @Override
    @Nullable
    public ZonedDateTime getEndDate() {
        return endDate;
    }

    @Override
    public Iterable<Map.Entry<String, Object>> getRawValues() {
        return attributes.entrySet();
    }

    @Override
    public Map<String, Object> getAllValuesAsMap() {
        return attributes;
    }

    @Override
    public Object get(String key) {
        return attributes.get(key);
    }

    @Override
    public Iterable<Map.Entry<String, Object>> getAttributeValues() {
        return attributes.entrySet().stream().filter(e -> type.hasAttribute(e.getKey()) && (!type.getAttribute(e.getKey()).hasNotServiceListPermission() || e.getKey().equals(ATTR_IDTENANT) && type.hasMultitenantEnabled())).collect(toList());
    }

    @Override
    public CardStatus getCardStatus() {
        return status;
    }

    public static CardImplBuilder builder() {
        return new CardImplBuilder();
    }

    public static CardImpl buildCard(Classe type, Map<String, Object> attributes) {
        return builder().withType(type).withAttributes(attributes).build();
    }

    public static CardImpl buildCard(Classe type, Object... attributes) {
        return buildCard(type, map(attributes));
    }

    public static CardImplBuilder copyOf(DatabaseRecord card) {
        return builder()
                .withType(card.getType().asClasse())
                .withAttributes(card.getAllValuesAsMap());
    }

    @Override
    public String toString() {
        return "CardImpl{" + "id=" + id + (isBlank(getCode()) ? "" : format(", code=%s", getCode())) + ", type=" + type.getName() + '}';
    }

    private boolean isSynthesized() {
        return id == null;
    }

    public static class CardImplBuilder implements DatabaseRecordBuilder<CardImpl, CardImplBuilder> {

        private Classe type;
        private final Map<String, Object> rawAttributes = map();

        public CardImplBuilder withType(Classe type) {
            this.type = type;
            return this;
        }

        public CardImplBuilder withId(Long id) {
            return this.withAttribute(ATTR_ID, id);
        }

        @Override
        public CardImplBuilder withAttributes(Map<String, Object> attributes) {
            this.rawAttributes.putAll(attributes);
            return this;
        }

        @Override
        public CardImplBuilder withAttribute(String key, @Nullable Object value) {
            this.rawAttributes.put(key, value);
            return this;
        }

        @Override
        public CardImpl build() {
            return new CardImpl(this);
        }

    }

}
