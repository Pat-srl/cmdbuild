package org.cmdbuild.dao.beans;

import org.cmdbuild.common.beans.CardIdAndClassName;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import org.cmdbuild.common.beans.IdAndDescription;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_NOTES;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;

public interface Card extends DatabaseRecord, CardIdAndClassName, IdAndDescription {

    @Override
    Classe getType();

    @Override
    String getCode();

    @Override
    String getDescription();

    @Override
    default String getTypeName() {
        return getType().getName();
    }

    @Override
    default boolean hasId() {
        return true;//TODO implement everywhere, remove default
    }

    @Nullable
    default Long getIdOrNull() {
        return hasId() ? getId() : null;
    }

    @Override
    default String getClassName() {
        return getType().getName();
    }

    default CardStatus getCardStatus() {//TODO improve this //TODO implement everywhere, remove default
        return hasEndDate() ? CardStatus.U : CardStatus.A;
    }

    default boolean hasStatus(CardStatus status) {
        return equal(status, getCardStatus());
    }

    default boolean hasStatusActive() {
        return hasStatus(CardStatus.A);
    }

    default boolean isProcess() {
        return getType().isProcess();
    }

    @Nullable
    default String getNotes() {
        return getString(ATTR_NOTES);
    }

    @Nullable
    default CMRelation getRelation(String attr, Domain domain) { //TODO test this (?)
        Attribute attribute = getType().getAttribute(attr);
        checkArgument(attribute.isOfType(AttributeTypeName.REFERENCE), "invalid reference attr = %s", attribute);
        checkArgument(equal(attribute.getType().as(ReferenceAttributeType.class).getDomainName(), domain.getName()), "invalid reference attr = %s for domain = %s", attribute, domain);
        RelationDirection direction = attribute.getType().as(ReferenceAttributeType.class).getDirection();
        Long targetId = get(attr, Long.class);
        if (isNullOrLtEqZero(targetId)) {
            return null;
        } else {
            Classe target = domain.getThisDomainWithDirection(direction).getTargetClass();
            CardIdAndClassName other = card(target.getName(), targetId);
            return RelationImpl.builder()
                    .withType(domain)
                    .withDirection(direction)
                    .withSourceCard(this).withTargetCard(other)
                    .accept(b -> {
                        domain.getActiveServiceAttributes().forEach(a -> {
                            Object value = get(format("_%s_attr_%s", attribute.getName(), a.getName()));//TODO improve this
                            b.withAttribute(a.getName(), value);
                        });
                    }).build();
        }
    }

    enum CardStatus {
        A, N, U, D
    }

}
