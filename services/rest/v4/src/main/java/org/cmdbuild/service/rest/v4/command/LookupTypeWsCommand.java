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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.lookup.LookupSpeciality.LS_DEFAULT;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;

/**
 * @author ldare
 */
@Component
public class LookupTypeWsCommand {

    private final LookupService lookupService;

    public LookupTypeWsCommand(LookupService lookupLogic) {
        this.lookupService = checkNotNull(lookupLogic);
    }

    public LookupType doRead(String lookupTypeId) {
        return lookupService.getLookupType(decodeIfHex(lookupTypeId));
    }

    public List<LookupType> doReadAll(String filter) {
        return lookupService.getAllTypes(filter).stream().filter(LookupType::isDefaultSpeciality).collect(toList());
    }

    public LookupType doCreateLookupType(WsLookupType wsLookupType) {
        return lookupService.createLookupType(wsLookupType.toLookupType(lookupService).withSpeciality(LS_DEFAULT).build());
    }

    public void doDeleteLookupType(String lookupTypeId) {
        lookupService.deleteLookupType(decodeIfHex(lookupTypeId));
    }
}
