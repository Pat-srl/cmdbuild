package org.cmdbuild.lookup;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.List;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.beans.CmdbFilterImpl;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

public interface LookupRepository {

    @Nullable
    LookupValue getByIdOrNull(long lookupId);

    List<LookupValue> getAll();

    List<LookupValue> getByType(String type, CmdbFilter filter);

    List<LookupType> getAllTypes();

    List<LookupValue> getAllByTypeClassAttr(String type, String forClass, String forAttr);

    @Nullable
    LookupValue getOneByTypeAndCodeOrNull(String type, String code);

    @Nullable
    LookupValue getOneByTypeAndDescriptionOrNull(String type, String description);

    LookupValue createOrUpdate(LookupValue lookup);

    @Nullable
    LookupType getTypeByNameOrNull(String lookupTypeName);

    LookupType createLookupType(LookupType lookupType);

    /**
     * Used by <code>model-diff-schema</code> to handle creation of
     * <code>protected</code> {@link LookupType}s.
     *
     * @param lookupType
     * @return
     */
    LookupType createLookupType_Protected(LookupType lookupType);

    void deleteLookupValue(long lookupValueId);

    void deleteLookupType(String lookupTypeId);

    LookupType getTypeById(long typeId);

    default LookupValue getById(long lookupId) {
        return checkNotNull(getByIdOrNull(lookupId), "lookup not found for id = %s", lookupId);
    }

    default LookupType getTypeByName(String lookupTypeName) {
        return checkNotNull(getTypeByNameOrNull(lookupTypeName), "lookup type not found for name =< %s >", lookupTypeName);
    }

    default Collection<LookupValue> getAllByType(long typeId) {
        return getAllByType(getTypeById(typeId));
    }

    default Collection<LookupValue> getAllByType(String type) {
        return getByType(type, CmdbFilterImpl.noopFilter());
    }

    default Collection<LookupValue> getAllByType(LookupType type) {
        return getAllByType(type.getName());
    }

    default LookupValue getOneByTypeAndCode(String type, String code) {
        return checkNotNull(getOneByTypeAndCodeOrNull(type, code), "lookup not found for type =< %s > and code =< %s >", type, code);
    }

    default LookupValue getOneByTypeAndCodeOrId(String type, Object codeOrId) {
        LookupValue value = getOneByTypeAndCodeOrNull(type, toStringNotBlank(codeOrId));
        if (value == null && NumberUtils.isCreatable(toStringNotBlank(codeOrId))) {
            value = getOneByTypeAndIdOrNull(type, toLong(codeOrId));
        }
        return checkNotNull(value, "lookup not found for type =< %s > and code or description or id =< %s >", type, codeOrId);
    }

    default LookupValue getOneByTypeAndCodeOrDescriptionOrId(String type, String codeOrDescriptionOrId) {
        LookupValue value = getOneByTypeAndCodeOrNull(type, codeOrDescriptionOrId);
        if (value == null) {
            value = getOneByTypeAndDescriptionOrNull(type, codeOrDescriptionOrId);
        }
        if (value == null && NumberUtils.isCreatable(codeOrDescriptionOrId)) {
            value = getOneByTypeAndIdOrNull(type, toLong(codeOrDescriptionOrId));
        }
        return checkNotNull(value, "lookup not found for type =< %s > and code or description or id =< %s >", type, codeOrDescriptionOrId);
    }

    default LookupValue getOneByTypeAndDescription(String type, String description) {
        return checkNotNull(getOneByTypeAndDescriptionOrNull(type, description), "lookup not found for type =< %s > and description =< %s >", type, description);
    }

    default LookupValue getOneByTypeAndId(String type, long lookupValueId) {
        return checkNotNull(getOneByTypeAndIdOrNull(type, lookupValueId), "lookup not found for type =< %s > and id = %s", type, lookupValueId);
    }

    @Nullable
    default LookupValue getOneByTypeAndIdOrNull(String type, long lookupValueId) {
        LookupValue lookup = getByIdOrNull(lookupValueId);
        if (lookup != null && equal(lookup.getType().getName(), type)) {
            return lookup;
        } else {
            return null;
        }
    }

    default boolean hasLookupWithTypeAndId(String type, long id) {
        return getOneByTypeAndIdOrNull(type, id) != null;
    }

    default boolean hasLookupWithTypeAndCode(String type, String code) {
        return getOneByTypeAndCodeOrNull(type, code) != null;
    }

    default boolean hasLookupWithTypeAndDescription(String type, String description) {
        return getOneByTypeAndDescriptionOrNull(type, description) != null;
    }

}
