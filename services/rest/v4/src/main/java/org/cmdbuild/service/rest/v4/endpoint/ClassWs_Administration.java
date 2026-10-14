/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.v4.command.ClassWsCommand;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_VIEW_AUTHORITY;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.*;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.filterAttributes;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author schursin
 */
@Path("administration/classes/")
@Tags({
        @Tag( name = "APIs to manage classes", description = "APIs to manage classes."),
        @Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class ClassWs_Administration {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UserClassService userClassService;
    private final ClassSerializationHelper classSerializationHelper;
    private final ClassWsCommand command;

    public ClassWs_Administration(UserClassService userClassService, ClassSerializationHelper classSerializationHelper, ClassWsCommand command) {
        this.userClassService = checkNotNull(userClassService);
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
        this.command = checkNotNull(command);

    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all classes",
            description = "Obtain a list of all classes for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Classe.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public Object readAll(
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response") Boolean detailed,
            @QueryParam("includeLookupValues") @Parameter(description = "If true include lookup values") @DefaultValue(FALSE) Boolean includeLookupValues,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample"))  Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0")  Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter to limit the attributes returned in the response") String filterStr
    ) {
        List<Classe> classeList = command.doReaAll(userClassService::getAllUserClasses);

        Set<ClassQueryFeatures> features = set(CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER);
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
            summary = "Get a class",
            description = "Obtain details of a specific class",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Classe.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public Object read(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query", schema = @Schema(type = "string"))  String classId,
            @QueryParam("includeLookupValues") @Parameter(description = "If true include lookup values") @DefaultValue(FALSE) Boolean includeLookupValues
    ) {
        ExtendedClass classe = command.doRead(classId, CQ_INCLUDE_INACTIVE_ELEMENTS, CQ_FOR_USER, includeLookupValues ? CQ_INCLUDE_LOOKUP_VALUES : null);
        return response(classSerializationHelper.buildFullDetailExtendedResponse(classe));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create a new class",
            description = "Create a new class",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Classe.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object create(
            @RequestBody(description = "Class data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ClassSerializationHelper.WsClassData.class))) ClassSerializationHelper.WsClassData data
    ) {
        logger.debug("create classe with data = {}", data);
        ExtendedClass classe = command.doCreate(data);
        return response(classSerializationHelper.buildFullDetailExtendedResponse(classe));
    }

    @PUT
    @Path("{" + CLASS_ID + "}/")
    @Operation(
            summary = "Update an existing class",
            description = "Update an existing class",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = Classe.class))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to update", schema = @Schema(type = "string"))  String classId,
            @RequestBody(description = "Class data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(implementation = ClassSerializationHelper.WsClassData.class))) ClassSerializationHelper.WsClassData data
    ) {
        logger.debug("update classe = {} with data = {}", classId, data);
        ExtendedClass classe = command.doUpdate(classId, data);
        return response(classSerializationHelper.buildFullDetailExtendedResponse(classe));
    }

    @DELETE
    @Path("{" + CLASS_ID + "}/")
    @Operation(
            summary = "Delete a class",
            description = "Delete a class",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to delete") String classId
    ) {
        command.doDelete(classId);
        return success();
    }
}
