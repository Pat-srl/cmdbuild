package org.cmdbuild.service.rest.v3.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("functions/")
@Tag( name = "Functions", description = "Functions")
@Produces(APPLICATION_JSON)
public class FunctionsWs {

    private final org.cmdbuild.service.rest.v4.endpoint.FunctionsWs functionsWs;

    public FunctionsWs(org.cmdbuild.service.rest.v4.endpoint.FunctionsWs functionsWs) {
        this.functionsWs = checkNotNull(functionsWs);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get the list of stored functions",
            description = "Retrieve the list of stored functions defined in the CMDBuild system. Supports filtering, pagination, and detailed views.",
            parameters = {
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Include or not full details in the response")
            },
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of function data")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed
    ) {
        return functionsWs.readAll(limit, offset, filterStr, detailed);
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/")
    @Operation(
            summary = "Get stored function details",
            description = "Retrieve detailed information about a specific stored function by its ID or name.",
            parameters = {
                    @Parameter(name = FUNCTION_ID, in = ParameterIn.PATH, description = "ID or name of the function to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of function details"),

            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(FUNCTION_ID) String functionId
    ) {
        return functionsWs.read(functionId);
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/parameters/")
    @Operation(
            summary = "Get stored function input parameters",
            description = "Retrieve the input parameters of a specific stored function by its ID or name.",
            parameters = {
                    @Parameter(name = FUNCTION_ID, in = ParameterIn.PATH, description = "ID or name of the function to retrieve", required = true),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")
            },
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of function input parameters")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readInputParameters(
            @PathParam(FUNCTION_ID) String functionId,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset
    ) {
        return functionsWs.readInputParameters(functionId, limit, offset);
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/attributes/")
    @Operation(
            summary = "Get stored function output parameters",
            description = "Retrieve the output parameters of a specific stored function by its ID or name.",
            parameters = {
                    @Parameter(name = FUNCTION_ID, in = ParameterIn.PATH, description = "ID or name of the function to retrieve", required = true),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")
            },
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of function output parameters")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOutputParameters(
            @PathParam(FUNCTION_ID) String functionId,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset
    ) {
        return functionsWs.readOutputParameters(functionId, limit, offset);
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/outputs/")
    @Operation(
            summary = "Call stored function and get output parameters",
            description = "Invoke a specific stored function by its ID or name and retrieve the output parameters based on provided input parameters.",
            parameters = {
                    @Parameter(name = FUNCTION_ID, in = ParameterIn.PATH, description = "ID or name of the function to retrieve", required = true),
                    @Parameter(name = PARAMETERS, in = ParameterIn.QUERY, description = "Input parameters for the function", schema = @Schema(type = "string", format = "json")),
                    @Parameter(name = "model", in = ParameterIn.QUERY, description = "Model to use for the function call")
            },
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of function output parameters")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object call(
            @PathParam(FUNCTION_ID) String functionId,
            @QueryParam(PARAMETERS) String inputs,
            @QueryParam("model") String model
    ) {
        return functionsWs.call(functionId, inputs, model);
    }

    @POST
    @Path("{" + FUNCTION_ID + "}/outputs/")
    @Operation(
            summary = "Call stored function via POST and get output parameters",
            description = "Invoke a specific stored function by its ID or name using a POST request and retrieve the output parameters based on provided input parameters.",
            parameters = {
                    @Parameter(name = FUNCTION_ID, in = ParameterIn.PATH, description = "ID or name of the function to retrieve", required = true),
                    @Parameter(name = "model", in = ParameterIn.QUERY, description = "Model to use for the function call"),
                    @Parameter(name = PARAMETERS, in = ParameterIn.QUERY, description = "Input parameters for the function", schema = @Schema(type = "string", format = "json"))
            },
            responses = {@ApiResponse(responseCode = "200", description = "Successful retrieval of function output parameters")},
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object callAsPost(
            @PathParam(FUNCTION_ID) String functionId,
            @QueryParam("model") String model,
            @Multipart(value = PARAMETERS, required = false) @Nullable String parameters) {
        return functionsWs.callAsPost(functionId, model, parameters);
    }
}
