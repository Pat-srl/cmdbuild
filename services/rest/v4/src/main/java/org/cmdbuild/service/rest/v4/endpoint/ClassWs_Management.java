/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ClassWsCommand;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.*;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.filterAttributes;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("classes/")
@Tag(name = "Classes management")
@Produces(APPLICATION_JSON)
@Component
public class ClassWs_Management {

    private final UserClassService userClassService;
    private final ClassSerializationHelper classSerializationHelper;
    private final ClassWsCommand command;

    public ClassWs_Management(UserClassService userClassService, ClassSerializationHelper classSerializationHelper, ClassWsCommand command) {
        this.userClassService = checkNotNull(userClassService);
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all the classes for the current user",
            description = "Obtain a list of all classes for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of classes"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {}) }
    )
    public Object readAll(
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response", schema = @Schema(type = "boolean", defaultValue = FALSE)) Boolean detailed,
            @QueryParam("includeLookupValues") @Parameter(description = "If true include lookup values", schema = @Schema(type = "boolean", defaultValue = FALSE)) @DefaultValue(FALSE) Boolean includeLookupValues,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")  Integer offset,
            @QueryParam(FILTER) @Parameter(description = "How to filter results", schema = @Schema(ref = "DefaultClassFilterExample")) String filterStr
    ) {
        List<Classe> classeList = command.doReaAll(userClassService::getActiveUserClasses);

        Set<UserClassService.ClassQueryFeatures> features = set(CQ_FOR_USER, CQ_FILTER_DEVICE);
        if (includeLookupValues) {
            features.add(CQ_INCLUDE_LOOKUP_VALUES);
        }
        List<FluentMap<String, Object>> classSerializations = classSerializationHelper.serialize(classeList, features, detailed);
        classSerializations = filterAttributes(classSerializations, filterStr);
        return response(paged(classSerializations, offset, limit));
    }

    @GET
    @Path("{" + CLASS_ID + "}/")
    @Operation(
            summary = "Get a specific class",
            description = "Obtain a specific class",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of class"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query")  String classId,
            @QueryParam("includeLookupValues") @Parameter(description = "If true include lookup values", schema = @Schema(type = "boolean", defaultValue = FALSE)) @DefaultValue(FALSE) Boolean includeLookupValues
    ) {
        ExtendedClass classe = command.doRead(classId, CQ_FOR_USER, CQ_FILTER_DEVICE, includeLookupValues ? CQ_INCLUDE_LOOKUP_VALUES : null);
        return response(classSerializationHelper.buildFullDetailExtendedResponse(classe));
    }
}
