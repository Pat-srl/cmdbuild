package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Objects.equal;
import com.google.common.collect.Iterables;
import java.util.List;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.entrytype.ClassMetadata.ClassSpeciality;
import static org.cmdbuild.dao.entrytype.ClassMultitenantMode.CMM_ALWAYS;
import static org.cmdbuild.dao.entrytype.ClassMultitenantMode.CMM_NEVER;
import static org.cmdbuild.dao.entrytype.ClassType.CT_SIMPLE;
import static org.cmdbuild.dao.entrytype.ClassType.CT_STANDARD;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_CLASS;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public interface Classe extends EntryType, ClassPermissions, ClasseInfo {

    @Override
    ClassMetadata getMetadata();

    List<String> getAncestors();

    default ClassType getClassType() {
        return getMetadata().getClassType();
    }

    default ClassSpeciality getClassSpeciality() {
        return getMetadata().getClassSpeciality();
    }

    default List<String> getAncestorsAndSelf() {
        return list(getAncestors()).with(getName());
    }

    default boolean isProcess() {
        return getMetadata().isProcess();
    }

    @Override
    default EntryTypeType getEtType() {
        return ET_CLASS;
    }

    default boolean hasParent() {
        return getParentOrNull() != null;
    }

    @Nullable
    default String getParentOrNull() {
        if (getAncestors().isEmpty()) {
            return null;
        } else {
            return Iterables.getLast(getAncestors());
        }
    }

    default String getParent() {
        return checkNotBlank(getParentOrNull(), "this class does not have a parent");
    }

    @Override
    default String getDescription() {
        return firstNotBlank(getMetadata().getDescription(), getName());
    }

    @Override
    default long getOid() {
        return getId();
    }

    default boolean isSuperclass() {
        return getMetadata().isSuperclass();
    }

    @Override
    default boolean hasHistory() {
        return getMetadata().holdsHistory();
    }

    default boolean isSimpleClass() {
        return equal(getClassType(), CT_SIMPLE);
    }

    default boolean isStandardClass() {
        return equal(getClassType(), CT_STANDARD);
    }

    default boolean isDefaultSpeciality() {
        return getMetadata().isDefaultSpeciality();
    }

    default boolean isDmsModel() {
        return getMetadata().isDmsModel();
    }

    default boolean isWfUserStoppable() {
        return getMetadata().isWfUserStoppable();
    }

    default ClassMultitenantMode getMultitenantMode() {
        return getMetadata().getMultitenantMode();
    }

    default boolean hasMultitenantEnabled() {
        return !equal(getMultitenantMode(), CMM_NEVER);
    }

    default boolean hasMultitenantModeAlways() {
        return equal(getMultitenantMode(), CMM_ALWAYS);
    }

    @Override
    default String getPrivilegeId() {
        return String.format("Class:%s", getName());
    }

    default boolean hasDmsCategory() {
        return !isBlank(getDmsCategoryOrNull());
    }

    default String getDmsCategory() {
        return checkNotBlank(getDmsCategoryOrNull());
    }

    @Nullable
    default String getDmsCategoryOrNull() {
        return getMetadata().getDmsCategoryOrNull();
    }

    default boolean isAncestorOf(Classe otherClass) {
        return otherClass.hasAncestor(this);
    }

    default boolean equalToOrAncestorOf(Classe otherClass) {
        return equal(this.getName(), otherClass.getName()) || isAncestorOf(otherClass);
    }

    default boolean equalToOrDescendantOf(String otherClassName) {
        return equal(this.getName(), otherClassName) || getAncestors().contains(otherClassName);
    }

    default boolean equalToOrDescendantOf(Classe otherClasse) {
        return equalToOrDescendantOf(otherClasse.getName());
    }

    default boolean hasReferenceForDomain(Domain domain) {
        return getAllAttributes().stream().filter(a -> a.isOfType(REFERENCE)).map(Attribute::getType).map(ReferenceAttributeType.class::cast).anyMatch(a -> equal(a.getDomainName(), domain.getName()));
    }

    default boolean hasReferenceForDomain(Domain domain, RelationDirection direction) {
        return getAllAttributes().stream().filter(a -> a.isOfType(REFERENCE)).map(Attribute::getType).map(ReferenceAttributeType.class::cast).anyMatch(a -> equal(a.getDomainName(), domain.getName()) && equal(a.getDirection(), direction));
    }

    default boolean hasAncestor(Classe ancestor) {
        return getAncestors().contains(ancestor.getName());
    }

    default boolean hasCommonSubclasses(Classe other) {
        return this.equalToOrAncestorOf(other) || other.equalToOrAncestorOf(this);
    }

    default boolean isLeafClass() {
        return !isSuperclass();
    }

}
