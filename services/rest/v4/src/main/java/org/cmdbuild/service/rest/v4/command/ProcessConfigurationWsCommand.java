/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.collect.ImmutableMap;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.*;

/**
 * @author ldare
 */
@Component
public class ProcessConfigurationWsCommand {

    private final LookupService lookupService;

    public static final String OPEN = "open";
    public static final String SUSPENDED = "suspended";
    public static final String COMPLETED = "completed";
    public static final String ABORTED = "closed";

    public static final Map<String, String> PROCESS_STATUS_CODE_MAP = ImmutableMap.of(
            STATE_OPEN_RUNNING, OPEN,
            STATE_OPEN_NOT_RUNNING_SUSPENDED, SUSPENDED,
            STATE_CLOSED_COMPLETED, COMPLETED,
            STATE_CLOSED_ABORTED, ABORTED
    );

    public ProcessConfigurationWsCommand(LookupService lookupService) {
        this.lookupService = checkNotNull(lookupService);
    }

    public CmCollectionUtils.FluentList<LookupValue> doReadStatuses() {
        return list(lookupService.getAllLookup(FLOW_STATUS_LOOKUP)).filter(LookupValue::isActive);
    }
}
