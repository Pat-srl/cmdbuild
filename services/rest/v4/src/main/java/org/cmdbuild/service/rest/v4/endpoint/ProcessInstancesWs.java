package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.ProcessWsSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ProcessInstancesWsCommand;
import org.cmdbuild.service.rest.v4.model.WsFlowData;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.cmdbuild.workflow.FlowAdvanceResponse;
import org.cmdbuild.workflow.WorkflowService;
import org.cmdbuild.workflow.model.Flow;
import org.cmdbuild.workflow.model.Process;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl.emptyOptions;
import static org.cmdbuild.email.Email.EMAIL_ATTR_CARD;
import static org.cmdbuild.email.Email.EMAIL_CLASS_NAME;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.PROCESS_INSTANCE_ID;
import static org.cmdbuild.service.rest.v4.utils.PositionOfUtils.handlePositionOfAndGetMeta;

@Path("processes/{" + PROCESS_ID + "}/instances/")
@Tag(name = "Process instances", description = "Operations related to process instances")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ProcessInstancesWs {

    private final DaoService daoService;
    private final WorkflowService workflowService;
    private final ProcessWsSerializationHelper processWsSerializationHelper;
    private final DmsService dmsService;
    private final ProcessInstancesWsCommand command;

    public ProcessInstancesWs(
            WorkflowService workflowService,
            ProcessWsSerializationHelper processWsSerializationHelper,
            DaoService daoService,
            DmsService dmsService,
            ProcessInstancesWsCommand command) {
        this.workflowService = checkNotNull(workflowService);
        this.processWsSerializationHelper = checkNotNull(processWsSerializationHelper);
        this.daoService = checkNotNull(daoService);
        this.dmsService = checkNotNull(dmsService);
        this.command = checkNotNull(command);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new process instance",
            description = "Create a new process instance for a specific process",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of process instance data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "409", description = "Conflict"),
                    @ApiResponse(responseCode = "422", description = "Unprocessable entity"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object create(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @RequestBody(description = "Process instance data", required = true) WsFlowData processInstance
    ) {
        FlowAdvanceResponse flowAdvanceResponse = command.doCreate(processId, processInstance);
        return response(processWsSerializationHelper.serializeFlowWithStatusIdAndTaskList(flowAdvanceResponse));
    }

    @PUT
    @Path("{" + PROCESS_INSTANCE_ID + "}")
    @Operation(
            summary = "Update a process instance",
            description = "Update a process instance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful update of process instance data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "422", description = "Unprocessable entity"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String planClassId,
            @PathParam(PROCESS_INSTANCE_ID) @Parameter(description = "Process instance id", required = true) Long flowCardId,
            @RequestBody(description = "Process instance data", required = true) WsFlowData processInstance
    ) {
        FlowAdvanceResponse response = command.doUpdate(planClassId, flowCardId, processInstance);
        return response(processWsSerializationHelper.serializeFlowWithStatusIdAndTaskList(response));
    }

    @GET
    @Path("{" + PROCESS_INSTANCE_ID + "}")
    @Operation(
            summary = "Get a process instance",
            description = "Get a process instance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process instance data"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String planClassId,
            @PathParam(PROCESS_INSTANCE_ID) @Parameter(description = "Process instance id", required = true)  Long flowCardId,
            @QueryParam("includeModel") @Parameter(description = "Include model", required = false) @DefaultValue(FALSE) Boolean includeModel,
            @QueryParam("include_tasklist") @Parameter(description = "Include tasklist", required = false) @DefaultValue(FALSE) Boolean includeTasklist,
            @QueryParam("includeStats") @Parameter(description = "Include stats", required = false) @DefaultValue(FALSE) Boolean includeStats
    ) {
        Flow card = command.doRead(planClassId, flowCardId);

        CmMapUtils.FluentMap<String, Object> map = processWsSerializationHelper.serializeFlow(card, includeTasklist, true, includeModel, emptyOptions());
        if (includeStats) {
            Integer attachmentCount = null;
            if (dmsService.isEnabled()) {
                attachmentCount = dmsService.getCardAttachmentCountSafe(card);
            }
            map.put("_attachment_count", attachmentCount,
                    "_email_count", daoService.selectCount().from(EMAIL_CLASS_NAME).where(EMAIL_ATTR_CARD, EQ, flowCardId).getCount());
        }
        return response(map);
    }

    @GET
    @Path("{" + PROCESS_INSTANCE_ID + "}/graph/")
    @Operation(
            summary = "Get a graph image for a process instance",
            description = "Get a graph image for a process instance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process instance graph image"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler plotGraph(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @PathParam(PROCESS_INSTANCE_ID) @Parameter(description = "Process instance id", required = true) Long cardId,
            @QueryParam("simplified") @Parameter(description = "Simplified graph", required = false) @DefaultValue(FALSE) Boolean simplified
    ) {
        return command.doPlotGraph(processId, cardId, simplified);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get process instances",
            description = "Get process instances",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of process instances data"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readMany(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam("include_tasklist") @Parameter(description = "Include tasklist", required = false) @DefaultValue(FALSE) Boolean includeTasklist
    ) {
        Process found = workflowService.getProcess(processId);
        DaoQueryOptions queryOptions = wsQueryOptions.getQuery().mapAttrNames(found.getAliasToAttributeMap());
        PagedElements<Flow> elements = command.doReadMany(found.getName(), queryOptions);

        return response(elements.stream().map(f -> processWsSerializationHelper.serializeFlow(f, includeTasklist, false, false, queryOptions)).collect(toList()), elements.totalSize(), handlePositionOfAndGetMeta(wsQueryOptions.getQuery(), elements));
    }

    @DELETE
    @Path("{" + PROCESS_INSTANCE_ID + "}") //TODO add permission control; use 'user can stop' wf option'
    @Operation(
            summary = "Abort a process instance",
            description = "Abort a process instance",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Successful deletion of process instance"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public void delete(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @PathParam(PROCESS_INSTANCE_ID) @Parameter(description = "Process instance id", required = true) Long instanceId
    ) {
        command.doDelete(processId, instanceId);
    }

    @POST
    @Path("{" + PROCESS_INSTANCE_ID + "}/suspend")
    @Operation(
            summary = "Suspend a process instance",
            description = "Suspend a process instance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful suspension of process instance"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public void suspend(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @PathParam(PROCESS_INSTANCE_ID) @Parameter(description = "Process instance id", required = true) Long instanceId
    ) {
        command.doSuspend(processId, instanceId);
    }

    @POST
    @Path("{" + PROCESS_INSTANCE_ID + "}/resume")
    @Operation(
            summary = "Resume a process instance",
            description = "Resume a process instance",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful resumption of process instance"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public void resume(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @PathParam(PROCESS_INSTANCE_ID) @Parameter(description = "Process instance id", required = true) Long instanceId
    ) {
        command.doResume(processId, instanceId);
    }

    @DELETE
    @Path("")
    @Operation(
            summary = "Abort multiple process instances",
            description = "Abort multiple process instances",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of multiple process instances"),
                    @ApiResponse(responseCode = "404", description = "Not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object deleteMany(
            @PathParam(PROCESS_ID) @Parameter(description = "Process id", required = true) String processId,
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        command.doDeleteMany(processId, wsQueryOptions);
        return success();
    }
}
