/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.endpoint.ClassOrProcessDomainsWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.ClassOrProcessDomainsWs_Management;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("{type:classes|processes}/{"+ CLASS_ID + "}/domains")
@Tag(name = "Domains", description = "Operations related to domains of a specific class or process")
@Produces(APPLICATION_JSON)
public class ClassOrProcessDomainsWs {

    private final ClassOrProcessDomainsWs_Administration classOrProcessDomainsWs_adm;
    private final ClassOrProcessDomainsWs_Management classOrProcessDomainsWs_mng;

    public ClassOrProcessDomainsWs(ClassOrProcessDomainsWs_Administration classOrProcessDomainsWs_adm, ClassOrProcessDomainsWs_Management classOrProcessDomainsWs_mng) {
        this.classOrProcessDomainsWs_adm = checkNotNull(classOrProcessDomainsWs_adm);
        this.classOrProcessDomainsWs_mng = checkNotNull(classOrProcessDomainsWs_mng);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get domains for a class or process",
            description = "Obtain the list of domains associated to a specific class or process. Optionally, include full details of each domain (i.e., including the list of valid values for each domain). The actual details included in the response depend on the view mode specified in the request header: if the view mode is admin, the response includes all details of each domain; otherwise, only basic details are included (i.e., the list of valid values is not included, and only the count of valid values is provided).",
            parameters = {
                    @Parameter( name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"}) ),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode of the request", schema = @Schema(allowableValues = {"admin", "management"})),
                    @Parameter( name = "classId", in = ParameterIn.PATH, description = "Identifier of the class or process" ),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Include full details of each domain")
            },
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful operation"),
                    @ApiResponse( responseCode = "400", description = "Bad request"),
                    @ApiResponse( responseCode = "404", description = "Class or process not found"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getDomains(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean includeFullDetails
    ) {
        if (isAdminViewMode(viewMode)) {
            return classOrProcessDomainsWs_adm.getDomains(classId, includeFullDetails);
        }
        return classOrProcessDomainsWs_mng.getDomains(classId, includeFullDetails);
    }

}
