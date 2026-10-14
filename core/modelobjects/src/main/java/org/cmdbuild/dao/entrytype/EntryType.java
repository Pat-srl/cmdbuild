package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Functions.compose;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Ordering;
import jakarta.annotation.Nullable;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_CLASS;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_DOMAIN;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_FUNCTION;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_OTHER;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.SorterElementDirection;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.data.filter.beans.SorterElementImpl;
import static org.cmdbuild.report.ReportConst.DUMMY_REPORT_PARAM_OWNER_CODE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.view.ViewConst.CLASS_IS_DUMMY_FOR_VIEW;

public interface EntryType extends PrivilegeSubjectWithInfo {

    void accept(CMEntryTypeVisitor visitor);

    boolean hasHistory();

    EntryTypeMetadata getMetadata();

    Map<String, Attribute> getAllAttributesAsMap();

    default Map<String, AttributeGroupData> getAttributeGroupsAsMap() {
        return emptyMap();
    }

    default List<AttributeGroupData> getAttributeGroups() {
        return list(getAttributeGroupsAsMap().values()).sorted(a -> (Integer) a.getIndex());
    }

    default boolean isActive() {
        return getMetadata().isActive();
    }

    @Override
    default String getDescription() {
        return getMetadata().getDescription();
    }

    default List<Attribute> getCoreAttributes() {
        return getAllAttributes().stream().filter(Attribute::hasCoreListPermission).collect(toList());
    }

    default List<Attribute> getServiceAttributes() {
        return getAllAttributes().stream().filter(Attribute::hasServiceListPermission).collect(toList());
    }

    default Collection<Attribute> getAllAttributes() {
        return getAllAttributesAsMap().values();
    }

    default Collection<Attribute> getActiveServiceAttributes() {
        return getServiceAttributes().stream().filter(Attribute::isActive).collect(toList());
    }

    default Collection<Attribute> getActiveUiAttributes() {
        return getAllAttributes().stream().filter(Attribute::hasUiReadPermission).filter(Attribute::isActive).collect(toList());
    }

    @Nullable
    default AttributeGroupData getAttributeGroupOrNull(String name) {
        return getAttributeGroupsAsMap().get(checkNotBlank(name));
    }

    default AttributeGroupData getAttributeGroup(String name) {
        return checkNotNull(getAttributeGroupOrNull(name), "attribute group not found for name =< %s >", name);
    }

    default boolean hasAttributeGroup(String name) {
        return getAttributeGroupOrNull(name) != null;
    }

    @Nullable
    default Attribute getAttributeOrNull(String name) {
        return getAllAttributesAsMap().get(checkNotBlank(name));
    }

    default Attribute getAttribute(String name) {
        return checkNotNull(getAttributeOrNull(name), "attribute not found for key = %s within classe = %s", name, this);
    }

    default boolean hasAttribute(String key) {
        return getAttributeOrNull(key) != null;
    }

    default boolean hasAttributeActive(String key) {
        return hasAttribute(key) && getAttribute(key).isActive();
    }

    default EntryTypeType getEtType() {
        return ET_OTHER;
    }

    default List<Attribute> getAttributesForDefaultOrder() {
        return getCoreAttributes().stream().filter((a) -> a.getClassOrder() != 0).sorted(Ordering.natural().onResultOf(compose(Math::abs, Attribute::getClassOrder))).collect(toList());
    }

    default CmdbSorter getDefaultOrder() {
        return new CmdbSorterImpl(getAttributesForDefaultOrder().stream().map(a -> new SorterElementImpl(a.getName(), a.getClassOrder() > 0 ? SorterElementDirection.ASC : SorterElementDirection.DESC)).collect(toList()));
    }

    default boolean isClasse() {
        return equal(getEtType(), ET_CLASS);
    }

    default boolean isDomain() {
        return equal(getEtType(), ET_DOMAIN);
    }

    default boolean isFunction() {
        return equal(getEtType(), ET_FUNCTION);
    }

    default boolean isReport() {
        return isClasse() && equal(getName(), DUMMY_REPORT_PARAM_OWNER_CODE);
    }

    default boolean isView() {
        return isClasse() && isNotBlank(getMetadata().getAll().get(CLASS_IS_DUMMY_FOR_VIEW));
    }

    default boolean isRegularClass() {
        return isClasse() && !isReport() && !isView();
    }

    default Domain asDomain() {
        return Domain.class.cast(this);
    }

    default Classe asClasse() {
        return Classe.class.cast(this);
    }

    default Map<String, String> getAttributeToAliasMap() {
        return getAllAttributes().stream().filter(a -> a.getMetadata().hasUiAlias()).collect(toMap(Attribute::getName, a -> a.getMetadata().getUiAlias()));
    }

    default Map<String, String> getAliasToAttributeMap() {
        return ImmutableBiMap.copyOf(getAttributeToAliasMap()).inverse();
    }

}
