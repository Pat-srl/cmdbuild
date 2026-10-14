/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.lookup.LookupValueImpl;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupValue;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.MoreCollectors.onlyElement;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

/**
 *
 * @author schursin
 */
@Component
public class LookupValueWsCommand {

    private final LookupService lookupService;

    public LookupValueWsCommand(LookupService lookupService) {
        this.lookupService = checkNotNull(lookupService);
    }

    public LookupValue doRead(Long lookupValueId) {
        return lookupService.getLookup(lookupValueId);
    }

    public PagedElements<LookupValue> doReadAll(String lookupTypeId, Integer limit, Integer offset, String filterStr, String forClass, String forAttr, Boolean isAdmin) {
        if (isNotBlank(forClass) && isNotBlank(forAttr)) {
            return lookupService.getDistinctActiveLookup(decodeIfHex(lookupTypeId), offset, limit, forClass, forAttr);
        }

        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        if (isAdmin) {
            return lookupService.getAllLookup(decodeIfHex(lookupTypeId), offset, limit, filter);
        } else {
            return lookupService.getActiveLookup(decodeIfHex(lookupTypeId), offset, limit, filter);
        }
    }

    public LookupValue doCreate(String lookupTypeId, WsLookupValue wsLookupValue) {
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        return lookupService.createOrUpdateLookup(wsLookupValue.buildLookup().withType(lookupType).build());
    }

    public LookupValue doUpdate(String lookupTypeId, Long lookupId, WsLookupValue wsLookupValue) {
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        return lookupService.createOrUpdateLookup(wsLookupValue.buildLookup().withType(lookupType).withId(checkNotNull(lookupId)).build());
    }

    public void doDelete(String lookupTypeId, Long lookupId) {
        lookupService.deleteLookupValue(decodeIfHex(lookupTypeId), lookupId);
    }

    public PagedElements<LookupValue> doReorder(String lookupTypeId, List<Long> lookupValueIds) {
        lookupTypeId = decodeIfHex(lookupTypeId);
        checkNotNull(lookupValueIds);
        checkArgument(set(lookupValueIds).size() == lookupValueIds.size());
        checkArgument(lookupValueIds.stream().allMatch(notNull()));

        List<LookupValue> listLookupValue = list(lookupService.getAllLookup(lookupTypeId));

        List<LookupValue> listLookupsToSave = list();

        for (int i = 0; i < lookupValueIds.size(); i++) {
            Long lookupId = lookupValueIds.get(i);
            LookupValue lookup = listLookupValue.stream().filter((l) -> equal(l.getId(), lookupId)).collect(onlyElement());
            int newIndex = i + 1;
            if (newIndex != lookup.getIndex()) {
                listLookupsToSave.add(LookupValueImpl.copyOf(lookup).withIndex(newIndex).build());
            }
        }

        listLookupsToSave.forEach(lookupService::createOrUpdateLookup);
        return doReadAll(lookupTypeId, null, null, null, null, null, true);
    }
}
