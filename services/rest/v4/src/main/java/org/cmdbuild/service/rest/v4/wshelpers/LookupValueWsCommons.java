package org.cmdbuild.service.rest.v4.wshelpers;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.lookup.LookupValueImpl;
import org.cmdbuild.service.rest.common.serializationhelpers.LookupSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupValue;
import org.cmdbuild.translation.ObjectTranslationService;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public abstract class LookupValueWsCommons {

    protected final LookupService lookupService;
    protected final ObjectTranslationService translationService;
    protected final LookupSerializationHelper serializationHelper;

    protected LookupValueWsCommons(LookupService lookupLogic, ObjectTranslationService translationService,
                                   LookupSerializationHelper serializationHelper) {
        this.lookupService = checkNotNull(lookupLogic);
        this.translationService = checkNotNull(translationService);
        this.serializationHelper = checkNotNull(serializationHelper);
    }

    public Object doRead(String lookupTypeId, Long lookupValueId) {
        LookupValue lookup = lookupService.getLookup(lookupValueId);
        return response(serializeLookupValue(lookup));
    }

    public Object doReadAll(String lookupTypeId, Integer limit, Integer offset, String filterStr, String viewMode) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        PagedElements<LookupValue> lookups = isAdminViewMode(viewMode) ? lookupService.getAllLookup(decodeIfHex(lookupTypeId), offset, limit, filter) : lookupService.getActiveLookup(decodeIfHex(lookupTypeId), offset, limit, filter);
        return response(lookups.stream().map(this::serializeLookupValue).collect(toList()), lookups.totalSize());
    }

    public Object doReadDistinct(String lookupTypeId, Integer limit, Integer offset, String viewMode, String forClass, String forAttr) {
        PagedElements<LookupValue> lookups = lookupService.getDistinctActiveLookup(decodeIfHex(lookupTypeId), offset, limit, forClass, forAttr);
        return response(lookups.stream().map(this::serializeLookupValue).collect(toList()), lookups.totalSize());
    }

    public Object doCreate(String lookupTypeId, WsLookupValue wsLookupValue) {
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        LookupValue lookup = lookupService.createOrUpdateLookup(wsLookupValue.buildLookup().withType(lookupType).build());
        return response(serializeLookupValue(lookup));
    }

    public Object doUpdate(String lookupTypeId, Long lookupId, WsLookupValue wsLookupValue) {
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        LookupValue lookup = lookupService.createOrUpdateLookup(wsLookupValue.buildLookup().withType(lookupType).withId(checkNotNull(lookupId)).build());
        return response(serializeLookupValue(lookup));
    }

    public Object doDelete(String lookupTypeId, Long lookupId) {
        lookupService.deleteLookupValue(decodeIfHex(lookupTypeId), lookupId);
        return success();
    }

    public Object doReorder(String lookupTypeId, List<Long> lookupValueIds, String viewMode) {
        lookupTypeId = decodeIfHex(lookupTypeId);
        checkNotNull(lookupValueIds);
        checkArgument(set(lookupValueIds).size() == lookupValueIds.size());
        checkArgument(lookupValueIds.stream().allMatch(notNull()));

        List<LookupValue> lookups = list(lookupService.getAllLookup(lookupTypeId));

        List<LookupValue> lookupsToSave = list();

        for (int i = 0; i < lookupValueIds.size(); i++) {
            Long lookupId = lookupValueIds.get(i);
            LookupValue lookup = lookups.stream().filter((l) -> equal(l.getId(), lookupId)).collect(onlyElement());
            int newIndex = i + 1;
            if (newIndex != lookup.getIndex()) {
                lookupsToSave.add(LookupValueImpl.copyOf(lookup).withIndex(newIndex).build());
            }
        }

        lookupsToSave.forEach(lookupService::createOrUpdateLookup);

        return doReadAll(lookupTypeId, null, null, null, viewMode);
    }

    public Map<String, Object> serializeLookupValue(LookupValue lookupValue) {
        return serializationHelper.serializeLookupValue(lookupValue);
    }
}
