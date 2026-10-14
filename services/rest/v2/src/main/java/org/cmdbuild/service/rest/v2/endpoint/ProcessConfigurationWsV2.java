package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.lookup.LookupService;
import org.cmdbuild.lookup.LookupValue;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.FLOW_STATUS_LOOKUP;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.STATE_CLOSED_ABORTED;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.STATE_CLOSED_COMPLETED;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.STATE_OPEN_NOT_RUNNING_SUSPENDED;
import static org.cmdbuild.workflow.utils.FlowStatusUtils.STATE_OPEN_RUNNING;

@Path("configuration/processes/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ProcessConfigurationWsV2 {

    private final LookupService service;
    private final TranslationService translationService;

    public ProcessConfigurationWsV2(LookupService service, TranslationService translationService) {
        this.service = checkNotNull(service);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path("statuses/")
    public Object readStatuses() {
        return response(list(service.getAllLookup(FLOW_STATUS_LOOKUP)).filter(LookupValue::isActive).map(ps -> map(
                "_id", ps.getId(),
                "value", PROCESS_STATUS_CODE_MAP.get(ps.getCode()),
                "description", translationService.translateLookupDescription(FLOW_STATUS_LOOKUP, ps.getCode(), ps.getDescription())
        )));
    }

    private static final String OPEN = "open";
    private static final String SUSPENDED = "suspended";
    private static final String COMPLETED = "completed";
    private static final String ABORTED = "closed";

    private static final Map<String, String> PROCESS_STATUS_CODE_MAP = ImmutableMap.of(
            STATE_OPEN_RUNNING, OPEN,
            STATE_OPEN_NOT_RUNNING_SUSPENDED, SUSPENDED,
            STATE_CLOSED_COMPLETED, COMPLETED,
            STATE_CLOSED_ABORTED, ABORTED
    );
}
