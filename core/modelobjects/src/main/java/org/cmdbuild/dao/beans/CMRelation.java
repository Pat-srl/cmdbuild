package org.cmdbuild.dao.beans;

import static com.google.common.base.Objects.equal;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.common.beans.IdAndDescription;
import org.cmdbuild.common.beans.IdAndDescriptionImpl;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.beans.RelationImpl;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ1;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDOBJ2;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.ReverseDomain;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;

public interface CMRelation extends DatabaseRecord {

    static String ATTR_DESCRIPTION1 = "_description1", ATTR_DESCRIPTION2 = "_description2";
    static String ATTR_CODE1 = "_code1", ATTR_CODE2 = "_code2", ATTR_CANREAD1 = "_canread1", ATTR_CANREAD2 = "_canread2";

    @Override
    Domain getType();

    Long getSourceId();

    Long getTargetId();

    CardIdAndClassName getSourceCard();

    CardIdAndClassName getTargetCard();

    RelationDirection getDirection();

    default CMRelation getRelationDirect() {
        return getRelationWithDirection(RD_DIRECT);
    }

    default CMRelation getRelationWithDirection(RelationDirection direction) {
        return equal(direction, getDirection()) ? this : RelationImpl.inverseOf(this);
    }

    CMRelation getRelationWithSource(long cardId);

    Card.CardStatus getStatus();

    default String getSourceClassName() {
        return getSourceCard().getClassName();
    }

    default String getTargetClassName() {
        return getTargetCard().getClassName();
    }

    default boolean isDirect() {
        return equal(getDirection(), RD_DIRECT);
    }

    @Nullable
    default String getSourceDescription() {
        return getString(ATTR_DESCRIPTION1);
    }

    @Nullable
    default String getTargetDescription() {
        return getString(ATTR_DESCRIPTION2);
    }

    @Nullable
    default String getSourceCode() {
        return getString(ATTR_CODE1);
    }

    @Nullable
    default String getTargetCode() {
        return getString(ATTR_CODE2);
    }

    default boolean canReadSource() {
        return get(ATTR_CANREAD1, Boolean.class, false);
    }

    default boolean canReadTarget() {
        return get(ATTR_CANREAD2, Boolean.class, false);
    }

    default Domain getDomainWithThisRelationDirection() {
        return switch (getDirection()) {
            case RD_DIRECT ->
                getType();
            case RD_INVERSE ->
                ReverseDomain.of(getType());
            default ->
                throw unsupported("unsupported direction = %s", getDirection());
        };
    }

    default IdAndDescription getIdObjAttrValueAsFkAttrValue(String name) {
        return switch (name) {
            case ATTR_IDOBJ1 ->
                new IdAndDescriptionImpl(getSourceClassName(), getSourceId(), getSourceDescription(), getSourceCode());
            case ATTR_IDOBJ2 ->
                new IdAndDescriptionImpl(getTargetClassName(), getTargetId(), getTargetDescription(), getTargetCode());
            default ->
                throw new IllegalArgumentException("invalid attr name = " + name);
        };
    }

    default Card.CardStatus getCardStatus() {//TODO improve this //TODO implement everywhere, remove default
        if (getEndDate() == null) {
            return Card.CardStatus.A;
        } else {
            return Card.CardStatus.U;
        }
    }

    default boolean hasStatus(Card.CardStatus status) {
        return equal(status, getCardStatus());
    }

    default boolean hasStatusActive() {
        return hasStatus(Card.CardStatus.A);
    }

}
