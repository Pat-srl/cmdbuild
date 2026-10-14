/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.dashboard.DashboardData;
import org.cmdbuild.dashboard.DashboardService;
import org.cmdbuild.service.rest.common.serializationhelpers.DashboardSerializationHelper;
import org.cmdbuild.service.rest.v4.command.DashboardWsCommand;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 *
 * @author schursin
 */
@Path("dashboards/")
@Tag(name = "Dashboards")
@Produces(APPLICATION_JSON)
@Component
public class DashboardWs_Management {

    private final DashboardService dashboardService;
    private final DashboardSerializationHelper helper;
    private final DashboardWsCommand command;

    public DashboardWs_Management(ObjectTranslationService objectTranslationService, DashboardService dashboardService, DashboardWsCommand command) {
        this.dashboardService = checkNotNull(dashboardService);
        this.helper = new DashboardSerializationHelper(objectTranslationService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all dashboards for the current user",
            description = "Obtain a list of all dashboards for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of dashboards for the current user"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    public Object getAll(
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "If true includes full details in the response") Boolean detailed,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")  Integer offset
    ) {
        List<DashboardData> listDashboardData = command.doGetAll(dashboardService::getActiveForCurrentUser);
        return response(helper.applySerializationToListDashboardData(listDashboardData, detailed, limit, offset));
    }

    @GET
    @Path("{id}/")
    @Operation(
            summary = "Get a specific dashboard by ID or code",
            description = "Get a specific dashboard by ID or code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of dashboard data"),
                    @ApiResponse(responseCode = "404", description = "The dashboard was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement(name = "basicAuth", scopes = {}), @SecurityRequirement(name = "bearerAuth", scopes = {}) }
    )
    public Object readOne(
            @PathParam(ID) @Parameter(name = ID, description = "Id or code of the dashboard", schema = @Schema(type = "string")) String idOrCode
    ) {
        DashboardData dashboardData = command.doReadOne(idOrCode, dashboardService::getForUserByIdOrCode);
        return response(helper.serializeDetailedDashboardData(dashboardData));
    }
}
