package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.media.Content;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.StoredFunctionSerializer;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.FunctionsWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.serializationhelpers.StoredFunctionSerializer.toDetailedResponse;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("functions/")
@Tag( name = "Functions", description = "Functions")
@Produces(APPLICATION_JSON)
@Component
public class FunctionsWs {

    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3;
    private final AttributeTypeConversionService attributeTypeConversionService;
    private final FunctionsWsCommand command;

    public FunctionsWs(CardWsSerializationHelperv3 cardWsSerializationHelperv3, AttributeTypeConversionService attributeTypeConversionService, FunctionsWsCommand command) {
        this.cardWsSerializationHelperv3 = checkNotNull(cardWsSerializationHelperv3);
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get the list of stored functions",
            description = "Retrieve the list of stored functions defined in the CMDBuild system. Supports filtering, pagination, and detailed views.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of function data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")  Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter functions by attribute values") String filterStr,
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response") Boolean detailed
    ) {
        PagedElements<StoredFunction> paged = command.doReadAll(limit, offset, filterStr);

        return response(paged.stream().map(m -> detailed ? StoredFunctionSerializer.toDetailedResponse(m, attributeTypeConversionService) : StoredFunctionSerializer.toResponse(m)).collect(toList()), paged.totalSize());
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/")
    @Operation(
            summary = "Get stored function details",
            description = "Retrieve detailed information about a specific stored function by its ID or name.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of function details"),
                    @ApiResponse(responseCode = "404", description = "Function not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(FUNCTION_ID) @Parameter(description = "ID or name of the function", required = true) String functionId
    ) {
        StoredFunction function = command.doRead(functionId);
        return response(toDetailedResponse(function, attributeTypeConversionService));
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/parameters/")
    @Operation(
            summary = "Get stored function input parameters",
            description = "Retrieve the input parameters of a specific stored function by its ID or name.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of function input parameters"),
                    @ApiResponse(responseCode = "404", description = "Function not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readInputParameters(
            @PathParam(FUNCTION_ID) @Parameter(description = "ID or name of the function", required = true) String functionId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset
    ) {
        StoredFunction function = command.doRead(functionId);
        return attributeTypeConversionService.serializeResponse(function.getInputParameters(), function.getName(), limit, offset);
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/attributes/")
    @Operation(
            summary = "Get stored function output parameters",
            description = "Retrieve the output parameters of a specific stored function by its ID or name.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of function output parameters"),
                    @ApiResponse(responseCode = "404", description = "Function not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readOutputParameters(
            @PathParam(FUNCTION_ID) @Parameter(description = "ID or name of the function", required = true) String functionId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset
    ) {
        StoredFunction function = command.doRead(functionId);
        return attributeTypeConversionService.serializeResponse(function.getOutputParameters(), function.getName(), limit, offset);
    }

    @GET
    @Path("{" + FUNCTION_ID + "}/outputs/")
    @Operation(
            summary = "Call stored function and get output parameters",
            description = "Invoke a specific stored function by its ID or name and retrieve the output parameters based on provided input parameters.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of function output parameters"),
                    @ApiResponse(responseCode = "404", description = "Function not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object call(
            @PathParam(FUNCTION_ID) @Parameter(description = "ID or name of the function", required = true) String functionId,
            @QueryParam(PARAMETERS) @Parameter(description = "JSON string containing input parameters for the function") String inputs,
            @QueryParam("model") @Parameter(description = "JSON string containing output parameters model") String model
    ) {
        StoredFunction function = command.doRead(functionId);
        List<Object> inputParams = command.doGetInputParams(inputs, function);
        List<Attribute> outputParams = command.doGetOutputParams(model, function);
        List<ResultRow> results = command.doGetResults(function, inputParams, outputParams);

        List rows = results.stream().map((r) -> r.asMap())
                .map((source) -> map().accept((map) -> outputParams.forEach((p) -> cardWsSerializationHelperv3.addCardValuesAndDescriptionsAndExtras(p.getName(), p.getType(), source::get, map::put)))).collect(toList());

        return response(rows);
    }

    @POST
    @Path("{" + FUNCTION_ID + "}/outputs/")
    @Operation(
            summary = "Call stored function via POST and get output parameters",
            description = "Invoke a specific stored function by its ID or name using a POST request and retrieve the output parameters based on provided input parameters.",
            requestBody = @RequestBody(description = "Multipart body: the optional 'parameters' part holds the input parameters of the function as a JSON string", required = false, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = PARAMETERS, schema = @Schema(type = "string"))
            })),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of function output parameters"),
                    @ApiResponse(responseCode = "404", description = "Function not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object callAsPost(
            @PathParam(FUNCTION_ID) @Parameter(description = "ID or name of the function", required = true) String functionId,
            @QueryParam("model") @Parameter(description = "JSON string containing output parameters model") String model,
            @Multipart(value = PARAMETERS, required = false) @Nullable String parameters) {
        StoredFunction function = command.doRead(functionId);
        List<Object> inputParams = command.doGetInputParams(parameters, function);
        List<Attribute> outputParams = command.doGetOutputParams(model, function);
        List<ResultRow> results = command.doGetResults(function, inputParams, outputParams);

        List rows = results.stream().map((r) -> r.asMap())
                .map((source) -> map().accept((map) -> outputParams.forEach((p) -> cardWsSerializationHelperv3.addCardValuesAndDescriptionsAndExtras(p.getName(), p.getType(), source::get, map::put)))).collect(toList());

        return response(rows);
    }
}
