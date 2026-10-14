/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupType;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.lookup.LookupSpeciality.LS_DMSCATEGORY;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;

/**
 * @author ldare
 */
@Component
public class DmsCategoryWsCommand {

    private final LookupService lookupService;

    public DmsCategoryWsCommand(LookupService lookupService) {
        this.lookupService = checkNotNull(lookupService);
    }

    public LookupType doRead(String lookupTypeId) {
        return lookupService.getLookupType(decodeIfHex(lookupTypeId));
    }

    public List<LookupType> doReadAll(String filter) {
        return lookupService.getAllTypes(filter).stream().filter(LookupType::isDmsCategorySpeciality).collect(toList());
    }

    public LookupType doCreateLookupType(WsLookupType wsLookupType) {
        return lookupService.createLookupType(wsLookupType.toLookupType(lookupService).withSpeciality(LS_DMSCATEGORY).build());
    }

    public void doDeleteLookupType(String lookupTypeId) {
        checkIsDmsCategory(lookupTypeId);
        lookupService.deleteLookupType(decodeIfHex(lookupTypeId));
    }

    private void checkIsDmsCategory(String lookupTypeId) {
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        checkArgument(lookupType.isDmsCategorySpeciality(), "invalid lookup type =< %s > : not a dms category", lookupTypeId);
    }
}
