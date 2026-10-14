/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.service.rest.common.serializationhelpers.WsLookupValue;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeIfHex;

/**
 *
 * @author schursin
 */
@Component
public class DmsCategoryValueWsCommand {

    protected final LookupService lookupService;
    protected final ObjectTranslationService objectTranslationService;
    private final LookupValueWsCommand lookupValueWsCommand;

    public DmsCategoryValueWsCommand(LookupService lookupService, ObjectTranslationService objectTranslationService, LookupValueWsCommand lookupValueWsCommand) {
        this.lookupService = checkNotNull(lookupService);
        this.objectTranslationService = checkNotNull(objectTranslationService);
        this.lookupValueWsCommand = checkNotNull(lookupValueWsCommand);
    }

    public LookupValue doRead(Long lookupValueId) {
        return lookupService.getLookup(lookupValueId);
    }

    public LookupValue doCreate(String lookupTypeId, WsLookupValue wsLookupValue) {
        checkIsDmsCategory(lookupTypeId);
        return lookupValueWsCommand.doCreate(lookupTypeId, wsLookupValue);
    }

    public LookupValue doUpdate(String lookupTypeId, Long lookupId, WsLookupValue wsLookupValue) {
        checkIsDmsCategory(lookupTypeId);
        return lookupValueWsCommand.doUpdate(lookupTypeId, lookupId, wsLookupValue);
    }

    public void doDelete(String lookupTypeId, Long lookupId) {
        checkIsDmsCategory(lookupTypeId);
        lookupService.deleteLookupValue(decodeIfHex(lookupTypeId), lookupId);
    }

    public PagedElements<LookupValue> doReorder(String lookupTypeId, List<Long> lookupValueIds) {
        return lookupValueWsCommand.doReorder(lookupTypeId, lookupValueIds);
    }

    void checkIsDmsCategory(String lookupTypeId) {//TODO duplicate code
        LookupType lookupType = lookupService.getLookupType(decodeIfHex(lookupTypeId));
        checkArgument(lookupType.isDmsCategorySpeciality(), "invalid lookup type =< %s > : not a dms category", lookupTypeId);
    }

}
