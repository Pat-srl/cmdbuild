package org.cmdbuild.lookup;

import static com.google.common.base.Objects.equal;
import com.google.common.base.Optional;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.collect.Ordering;
import jakarta.annotation.Nullable;
import jakarta.inject.Provider;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.apache.commons.lang3.math.NumberUtils;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.data.filter.CmdbFilter;
import static org.cmdbuild.data.filter.beans.CmdbFilterImpl.noopFilter;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNullOrLtEqZero;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.alwaysTrue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("lookupService")
public class LookupServiceImpl implements LookupService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LookupRepository repository;
    private final Provider<TranslationService> translationService;//TODO improve this, remove dependency loop

    public LookupServiceImpl(LookupRepository repository, Provider<TranslationService> translationService) {
        this.repository = checkNotNull(repository);
        this.translationService = checkNotNull(translationService);
    }

    @Override
    public List<LookupType> getAllTypes(@Nullable String filter) {
        logger.trace("getting all lookup types");
        Collection<LookupType> allTypes = repository.getAllTypes();
        List<LookupType> list = allTypes.stream()
                .filter(isBlank(filter) ? alwaysTrue() : (l) -> matchFilter(filter, l.getName()))
                .sorted(Ordering.natural().onResultOf(LookupType::getName))
                //                .skip(firstNonNull(offset, 0))
                //                .limit(firstNonNull(limit, Integer.MAX_VALUE))
                .collect(toList());
        return list;
    }

    @Override
    public PagedElements<LookupValue> getAllLookup(String type, @Nullable Integer offset, @Nullable Integer limit, CmdbFilter filter) {
        return getAllLookup(type, offset, limit, filter, false);
    }

    @Override
    public PagedElements<LookupValue> getDistinctActiveLookup(String type, Integer offset, Integer limit, String forClass, String forAttr) {
        logger.debug("retrieving persisted lookup {}", type);

        Collection<LookupValue> lookups = repository.getAllByTypeClassAttr(type, forClass, forAttr).stream().filter(LookupValue::isActive).collect(toList());
        int count = lookups.size();

        logger.debug("retrieved persisted lookup {} [{}]: {}", type, count, lookups);

        lookups = lookups.stream()
                .sorted(Ordering.natural().onResultOf(LookupValue::getIndex))
                .skip(firstNonNull(offset, 0))
                .limit(firstNonNull(limit, Integer.MAX_VALUE))
                .collect(toList());
        return new PagedElements<>(lookups, count);
    }

    @Override
    public PagedElements<LookupValue> getActiveLookup(String type, Integer offset, Integer limit, CmdbFilter filter) {
        return getAllLookup(type, offset, limit, filter, true);
    }

    @Override
    @Nullable
    public LookupValue getLookupByTypeAndCodeOrNull(String type, String code) {
        return repository.getOneByTypeAndCodeOrNull(type, code);
    }

    @Override
    public Iterable<LookupValue> getAllLookupOfParent(LookupType type) {
        logger.debug("getting all lookups for the parent of type '{}'", type);
        if (type.hasParent()) {
            return repository.getAllByType(type.getParentNotNull());
        } else {
            return emptyList();
        }
    }

    @Override
    public LookupValue getLookup(Long id) {
        return repository.getById(id);
    }

    @Override
    public LookupValue getLookupOrNull(Long id) {
        return repository.getByIdOrNull(id);
    }

    @Override
    public LookupType getLookupType(String lookupTypeId) {
        return repository.getTypeByName(lookupTypeId);
    }

    @Override
    public LookupType getLookupType(long lookupTypeId) {
        return repository.getTypeById(lookupTypeId);
    }

    @Override
    public LookupType getLookupTypeCreateIfMissing(String type) {
        return Optional.fromNullable(repository.getTypeByNameOrNull(type)).or(() -> createLookupType(type));
    }

    @Override
    public LookupValue createOrUpdateLookup(LookupValue lookup) {
        logger.info("creating or updating lookup '{}'", lookup);
        checkNotNull(lookup);
        if (isNullOrLtEqZero(lookup.getIndex())) {
            int nextIndex = repository.getAllByType(lookup.getType()).stream().map(LookupValue::getIndex).reduce(Integer::max).orElse(-1) + 1;
            lookup = LookupValueImpl.copyOf(lookup).withType(getLookupType(lookup.getTypeName())).withIndex(nextIndex).build();
        } else {
            lookup = LookupValueImpl.copyOf(lookup).build();
        }
        return repository.createOrUpdate(lookup);
    }

    @Override
    public List<LookupValue> getAllTranslatedLookup(String type) {
        return getAllLookup(type, null, null, noopFilter()).elements().stream().map(l -> LookupValueImpl.copyOf(l).withDescription(translationService.get().translateLookupDescription(type, l.getCode(), l.getDescription())).build()).collect(toList());
    }

    @Override
    public LookupValue getTranslatedLookupOrNull(Long id) {
        return applyOrNull(getLookupOrNull(id), l -> LookupValueImpl.copyOf(l).withDescription(translationService.get().translateLookupDescription(l.getLookupType(), l.getCode(), l.getDescription())).build());
    }

    @Override
    public LookupValue getTranslatedLookupOrNull(String type, String translation) {
        return getAllTranslatedLookup(type).stream().filter(l -> equal(l.getDescription(), translation)).collect(toOptional()).orElse(null);
    }

    @Override
    public LookupValue getActiveTranslatedLookupOrNull(String type, String translation) {
        return getAllTranslatedLookup(type).stream().filter(LookupValue::isActive).filter(l -> equal(l.getDescription(), translation)).collect(toOptional()).orElse(null);
    }

    @Override
    @Nullable
    public LookupValue getLookupByTypeAndCodeOrDescriptionOrIdOrNull(String type, String value) {
        checkNotBlank(type);
        checkNotBlank(value);
        LookupValue lookup = repository.getOneByTypeAndCodeOrNull(type, value);
        if (lookup == null) {
            lookup = repository.getOneByTypeAndDescriptionOrNull(type, value);
        }
        if (lookup == null && NumberUtils.isCreatable(value)) {
            lookup = repository.getOneByTypeAndId(type, toLong(value));
        }
        if (lookup == null) {
            lookup = getAllTranslatedLookup(type).stream().filter(l -> equal(l.getDescription(), value)).collect(toOptional()).orElse(null);
        }
        return lookup;
    }

    @Override
    public LookupValue createOrUpdateLookup(LookupType lookupType, String code) {
        return createOrUpdateLookup(LookupValueImpl.builder().withType(lookupType).withCode(code).build());
    }

    @Override
    public LookupType createLookupType(LookupType lookupType) {
        return repository.createLookupType(lookupType);
    }

    /**
     * Used by <code>model-diff-schema</code> to handle creation of
     * <code>protected</code> {@link LookupType}s.
     *
     * @param lookupType
     * @return
     */
    @Override
    public LookupType createLookupType_Protected(LookupType lookupType) {
        return repository.createLookupType_Protected(lookupType);
    }    
    
    @Override
    public void deleteLookupValue(String lookupTypeId, long lookupValueId) {
        repository.deleteLookupValue(lookupValueId);
    }

    @Override
    public void deleteLookupType(String lookupTypeId) {
        repository.deleteLookupType(lookupTypeId);
    }

    @Override
    public LookupType createLookupType(String lookupType) {
        return createLookupType(LookupTypeImpl.builder().withName(lookupType).build());
    }

    @Override
    public LookupValue createLookupValue(String type, String code) {
        return createLookup(LookupValueImpl.build(type, code));
    }

    @Override
    public LookupValue createLookupValue(String type, String code, String description) {
        return createLookup(LookupValueImpl.builder().withTypeName(type).withCode(code).withDescription(description).build());
    }

    private PagedElements<LookupValue> getAllLookup(String type, @Nullable Integer offset, @Nullable Integer limit, CmdbFilter filter, boolean activeOnly) {
        Collection<LookupValue> lookups = repository.getByType(type, filter);
        if (activeOnly) {
            lookups = lookups.stream().filter(LookupValue::isActive).collect(toList());
        }
        int count = lookups.size();
        lookups = lookups.stream()
                .sorted(Ordering.natural().onResultOf(LookupValue::getIndex))
                .skip(firstNonNull(offset, 0))
                .limit(firstNonNull(limit, Integer.MAX_VALUE))
                .collect(toList());
        return new PagedElements<>(lookups, count);
    }

    private static boolean matchFilter(String filter, String name) {
        checkNotBlank(filter);
        checkNotBlank(name);
        return name.toLowerCase().contains(filter.trim().toLowerCase());
    }
}
