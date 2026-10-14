/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.Maps.transformEntries;
import java.time.ZonedDateTime;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.beans.CardIdAndClassName;
import static org.cmdbuild.dao.beans.CMRelation.ATTR_DESCRIPTION2;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import static org.cmdbuild.dao.utils.RelationUtils.rotateRelationWithSource;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CURRENTID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ENDDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_STATUS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.dao.utils.AttributeConversionUtils.rawToSystem;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;

public class RelationImpl implements CMRelation {

    private final Long id, currentId;
    private final Domain type;
    private final String user;
    private final RelationDirection direction;
    private final ZonedDateTime beginDate, endDate;
    private final Card.CardStatus status;//TODO get status
    private final CardIdAndClassName sourceCard, targetCard;
    private final Map<String, Object> attributes;

    private RelationImpl(RelationImplBuilder builder, Map<String, Object> attributes) {
        this.type = checkNotNull(builder.type);
        this.attributes = map(checkNotNull(attributes)).immutable();
        if (builder.id != null) {
            this.id = checkNotNull(builder.id);
            this.currentId = checkNotNull(builder.currentId);
        } else {
            this.id = null;
            this.currentId = null;
        }
        this.beginDate = builder.beginDate;
        this.endDate = builder.endDate;
        this.status = checkNotNull(builder.status);
        this.direction = checkNotNull(builder.direction);
        this.user = nullToEmpty(builder.user);

        this.sourceCard = checkNotNull(builder.sourceCard);
        this.targetCard = checkNotNull(builder.targetCard);

        if (sourceCard instanceof Card) {//TODO improve this
            checkArgument(type.getThisDomainWithDirection(direction).isDomainForSourceClasse(((Card) sourceCard).getType()), "invalid source card = %s for domain = %s", sourceCard, type);
        }
        if (targetCard instanceof Card) {//TODO improve this
            checkArgument(type.getThisDomainWithDirection(direction).isDomainForTargetClasse(((Card) targetCard).getType()), "invalid target card = %s for domain = %s", targetCard, type);
        }
    }

    @Override
    @Nullable
    public Long getId() {
        return id;
    }

    @Nullable
    @Override
    public Long getCurrentId() {
        return currentId;
    }

    @Override
    public Domain getType() {
        return type;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public ZonedDateTime getBeginDate() {
        return checkNotNull(beginDate, "no begin date for this relation (new relation)");
    }

    @Override
    @Nullable
    public ZonedDateTime getEndDate() {
        return endDate == null ? null : endDate;
    }

    @Override
    public Card.CardStatus getStatus() {
        return status;
    }

    @Override
    public RelationDirection getDirection() {
        return direction;
    }

    @Override
    public CardIdAndClassName getSourceCard() {
        return sourceCard;
    }

    @Override
    public CardIdAndClassName getTargetCard() {
        return targetCard;
    }

    @Override
    public Long getSourceId() {
        return getSourceCard().getId();
    }

    @Override
    public Long getTargetId() {
        return getTargetCard().getId();
    }

    @Override
    public Iterable<Map.Entry<String, Object>> getRawValues() {
        return attributes.entrySet();
    }

    @Override
    public Object get(String key) {
        return attributes.get(key);
    }

    @Override
    public Iterable<Map.Entry<String, Object>> getAttributeValues() {
        return attributes.entrySet();//TODO
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "RelationImpl{" + "id=" + id + ", type=" + type + ", sourceCard=" + sourceCard + ", targetCard=" + targetCard + '}';
    }

    @Override
    public CMRelation getRelationWithSource(long cardId) {
        return rotateRelationWithSource(this, cardId);
    }

    public static RelationImplBuilder builder() {
        return new RelationImplBuilder();
    }

    public static CMRelation build(Domain domain, CardIdAndClassName source, CardIdAndClassName target) {
        return builder().withType(domain).withSourceCard(source).withTargetCard(target).build();
    }

    public static RelationImplBuilder copyOf(CMRelation relation) {
        return builder()
                .withAttributes(relation.getAllValuesAsMap())
                .withBeginDate(((RelationImpl) relation).beginDate)//TODO
                .withCurrentId(relation.getCurrentId())
                .withDirection(relation.getDirection())
                .withEndDate(((RelationImpl) relation).endDate)//TODO
                .withId(relation.getId())
                .withSourceCard(relation.getSourceCard())
                .withSourceDescription(relation.getSourceDescription())
                .withStatus(((RelationImpl) relation).status)//TODO
                .withTargetCard(relation.getTargetCard())
                .withTargetDescription(relation.getTargetDescription())
                .withType(relation.getType())
                .withUser(relation.getUser());
    }

    public static CMRelation inverseOf(CMRelation relation) {
        return copyOf(relation).withDirection(relation.isDirect() ? RD_INVERSE : RD_DIRECT).withSourceCard(relation.getTargetCard()).withTargetCard(relation.getSourceCard()).build();
    }

    public static class RelationImplBuilder implements DatabaseRecordBuilder<RelationImpl, RelationImplBuilder> {

        private Long id;
        private Long currentId;
        private Domain type;
        private String user;
        private ZonedDateTime beginDate;
        private ZonedDateTime endDate;
        private Card.CardStatus status;
        private RelationDirection direction = RelationDirection.RD_DIRECT;
        private CardIdAndClassName sourceCard;
        private CardIdAndClassName targetCard;
        private final Map<String, Object> rawAttributes = map();

        public RelationImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public RelationImplBuilder withCurrentId(Long currentId) {
            this.currentId = currentId;
            return this;
        }

        public RelationImplBuilder withType(Domain type) {
            this.type = type;
            return this;
        }

        public RelationImplBuilder withDirection(RelationDirection direction) {
            this.direction = direction;
            return this;
        }

        public RelationImplBuilder withUser(String user) {
            this.user = user;
            return this;
        }

        public RelationImplBuilder withBeginDate(ZonedDateTime beginDate) {
            this.beginDate = beginDate;
            return this;
        }

        public RelationImplBuilder withEndDate(ZonedDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public RelationImplBuilder withStatus(Card.CardStatus status) {
            this.status = status;
            return this;
        }

        public RelationImplBuilder withSourceCard(CardIdAndClassName sourceCard) {
            this.sourceCard = sourceCard;
            return this;
        }

        public RelationImplBuilder withTargetCard(CardIdAndClassName targetCard) {
            this.targetCard = targetCard;
            return this;
        }

        public RelationImplBuilder withSourceDescription(String desc) {
            return this.withAttribute(ATTR_DESCRIPTION1, desc);
        }

        public RelationImplBuilder withTargetDescription(String desc) {
            return this.withAttribute(ATTR_DESCRIPTION2, desc);
        }

        public RelationImplBuilder withSourceCode(String desc) {
            return this.withAttribute(ATTR_CODE1, desc);
        }

        public RelationImplBuilder withTargetCode(String desc) {
            return this.withAttribute(ATTR_CODE2, desc);
        }

        @Override
        public RelationImplBuilder withAttributes(Map<String, Object> attributes) {
            this.rawAttributes.clear();
            this.rawAttributes.putAll(attributes);
            return this;
        }

        public RelationImplBuilder addAttributes(Map<String, Object> attributes) {
            this.rawAttributes.putAll(attributes);
            return this;
        }

        @Override
        public RelationImplBuilder withAttribute(String key, @Nullable Object value) {
            this.rawAttributes.put(key, value);
            return this;
        }

        @Override
        public RelationImpl build() {
            checkNotNull(type, "relation domain cannot be null");

            Map<String, Object> attributes = map(transformEntries(rawAttributes, (String key, Object value) -> {
                Attribute attribute = type.getAttributeOrNull(key);
                if (attribute != null) {
                    value = rawToSystem(attribute, value);
                }
                return value;
            }));

            this.id = firstNotNullOrNull(id, convert(rawAttributes.get(ATTR_ID), Long.class));
            this.currentId = firstNotNullOrNull(convert(rawAttributes.get(ATTR_CURRENTID), Long.class), id);
            user = firstNotNullOrNull(user, convert(rawAttributes.get(ATTR_USER), String.class));
            beginDate = firstNotNullOrNull(beginDate, convert(rawAttributes.get(ATTR_BEGINDATE), ZonedDateTime.class));
            endDate = firstNotNullOrNull(endDate, convert(rawAttributes.get(ATTR_ENDDATE), ZonedDateTime.class));
            status = firstNotNullOrNull(status, convert(rawAttributes.get(ATTR_STATUS), Card.CardStatus.class));
            if (status == null) {
                status = endDate == null ? Card.CardStatus.A : Card.CardStatus.U;//N ?
            }

            if (sourceCard == null) {
                sourceCard = card(convert(rawAttributes.get(ATTR_IDCLASS1), String.class), convert(rawAttributes.get(ATTR_IDOBJ1), Long.class));
            }
            if (targetCard == null) {
                targetCard = card(convert(rawAttributes.get(ATTR_IDCLASS2), String.class), convert(rawAttributes.get(ATTR_IDOBJ2), Long.class));
            }

            return new RelationImpl(this, attributes);
        }

    }
}
