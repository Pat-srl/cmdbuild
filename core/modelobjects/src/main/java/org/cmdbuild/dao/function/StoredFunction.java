package org.cmdbuild.dao.function;

import static com.google.common.base.Objects.equal;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.CMEntryTypeVisitor;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.entrytype.EntryTypeType;
import static org.cmdbuild.dao.entrytype.EntryTypeType.ET_FUNCTION;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface StoredFunction extends EntryType {

    List<Attribute> getInputParameters();

    List<Attribute> getOutputParameters();

    boolean returnsSet();

    Iterable<StoredFunctionCategory> getCategories();

    Map<String, Object> getMetadataExt();

    @Override
    FunctionMetadata getMetadata();

    default boolean isCached() {
        return getMetadata().isCached();
    }

    default boolean isScheduled() {
        return getMetadata().isScheduled();
    }

    @Override
    default void accept(CMEntryTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    default boolean hasHistory() {
        return false;
    }

    @Override
    default String getPrivilegeId() {
        return privilegeId(PS_STOREDFUNCTION, getId());
    }

    default Attribute getOutputParameter(String name) {
        checkNotBlank(name);
        return getOutputParameters().stream().filter((p) -> equal(p.getName(), name)).collect(onlyElement());
    }

    default Attribute getOnlyOutputParameter() {
        return getOnlyElement(getOutputParameters());
    }

    default boolean hasOnlyOneOutputParameter() {
        return getOutputParameters().size() == 1;
    }

    default List<String> getInputParameterNames() {
        return getInputParameters().stream().map(Attribute::getName).collect(toList());
    }

    @Override
    default EntryTypeType getEtType() {
        return ET_FUNCTION;
    }

    default Attribute getInputParameter(String name) {
        return getInputParameters().stream().filter(p -> equal(p.getName(), name)).collect(onlyElement("input parameter not found for name =< %s >", name));
    }

    default Set<String> getTags() {
        return getMetadata().getTags();
    }

    @Nullable
    default String getSourceClassName() {
        return getMetadata().getSource();
    }

    default boolean hasSourceClassName() {
        return isNotBlank(getSourceClassName());
    }

}
