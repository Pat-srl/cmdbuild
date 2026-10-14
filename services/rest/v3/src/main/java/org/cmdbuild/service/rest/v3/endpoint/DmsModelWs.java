package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper.WsClassData;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.service.rest.v4.endpoint.DmsModelWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.DmsModelWs_Management;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_DMS_MODIFY_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;


@Path("dms/models/")
@Produces(APPLICATION_JSON)
@Tag(name = "DMS Models", description = "Operations related to DMS models")
public class DmsModelWs {

    private final DmsModelWs_Administration dmsModelWs_adm;
    private final DmsModelWs_Management dmsModelWs_mng;

    public DmsModelWs(DmsModelWs_Administration dmsModelWs_adm, DmsModelWs_Management dmsModelWs_mng) {
        this.dmsModelWs_adm = checkNotNull(dmsModelWs_adm);
        this.dmsModelWs_mng = checkNotNull(dmsModelWs_mng);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all DMS models",
            description = "Get all DMS models. If the user has admin view permissions, all DMS models will be returned. " +
                    "Otherwise, only DMS models for which the user has management permissions will be returned. The " +
                    "view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or " +
                    "'management'. Additionally, a filter can be applied to the query by using the '" + FILTER + "' " +
                    "query parameter. The filter should be a string in the format 'property operator value', where " +
                    "'property' is the name of a property of the DMS model, 'operator' is one of 'eq' (equals), 'ne' " +
                    "(not equals), 'gt' (greater than), 'lt' (less than), 'ge' (greater than or equal to), 'le' (less " +
                    "than or equal to), and 'value' is the value to compare the property to. For example, 'name eq MyModel' " +
                    "would filter the results to only include DMS models with the name 'MyModel'",
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = DETAILED, in = ParameterIn.QUERY, description = "Whether to include detailed information about DMS models, such as the attributes contained in the model"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = FILTER, in = ParameterIn.QUERY, description = "Filter to apply to the query, in the format 'property operator value', where 'property' is the name of a property of the DMS model, 'operator' is one of 'eq' (equals), 'ne' (not equals), 'gt' (greater than), 'lt' (less than), 'ge' (greater than or equal to), 'le' (less than or equal to), and 'value' is the value to compare the property to")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view DMS models"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @QueryParam(DETAILED) @DefaultValue(FALSE) Boolean detailed,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset,
            @QueryParam(FILTER) String filterStr
    ) {
        if (isAdminViewMode(viewMode)) {
            return dmsModelWs_adm.readAll(detailed, limit, offset, filterStr);
        }
        return dmsModelWs_mng.readAll(detailed, limit, offset, filterStr);
    }

    @GET
    @Path("{"+ CLASS_ID + "}/")
    @Operation(
            summary = "Get a DMS model by class ID",
            description = "Get a DMS model by class ID. If the user has admin view permissions, the DMS model will be " +
                    "returned if it exists, regardless of the user's management permissions for that DMS model. " +
                    "Otherwise, the DMS model will only be returned if the user has management permissions for it. The " +
                    "view mode can be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or " +
                    "'management'" ,
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = "classId", in = ParameterIn.PATH, description = "ID of the DMS model to retrieve", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of DMS model data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the DMS model or the DMS model does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam("classId") String classId
    ) {
        if (isAdminViewMode(viewMode)) {
            return dmsModelWs_adm.read(classId);
        }
        return dmsModelWs_mng.read(classId);
    }

    @POST
    @Path(EMPTY)
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new DMS model",
            description = "Create a new DMS model. The request body should contain the data for the DMS model to create, including the class ID and the attributes to include in the model. The class ID must be unique and not already used by another DMS model. The user must have admin permissions to create a DMS model",
            requestBody = @RequestBody(description = "Data for the new DMS model", required = true, content = @Content(schema = @Schema(implementation = WsClassData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of DMS model"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the class ID is not unique or the request body is invalid"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create DMS models"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object create(
            WsClassData data
    ) {
        return dmsModelWs_adm.create(data);
    }

    @PUT
    @Path("{"+ CLASS_ID + "}/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update a DMS model",
            description = "Update a DMS model. The request body should contain the new data for the DMS model, including " +
                    "the attributes to update. The user must have admin permissions to update a DMS model. The class ID " +
                    "in the path parameter identifies the DMS model to update and cannot be changed. If the request body " +
                    "contains a class ID, it must match the class ID in the path parameter, otherwise the request will " +
                    "be rejected" ,
            parameters = {
                    @Parameter(name = "classId", in = ParameterIn.PATH, description = "ID of the DMS model to update", required = true)
            },
            requestBody = @RequestBody(description = "Data for updating the DMS model", required = true, content = @Content(schema = @Schema(implementation = WsClassData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid or the class ID in the path parameter does not match the class ID in the request body"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update DMS models or the DMS model does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object update(
            @PathParam("classId") String classId,
            WsClassData data
    ) {
        return dmsModelWs_adm.update(classId, data);
    }

    @DELETE
    @Path("{"+ CLASS_ID + "}/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete a DMS model",
            description = "Delete a DMS model. The user must have admin permissions to delete a DMS model. The class ID in the path parameter identifies the DMS model to delete" ,
            parameters = {
                    @Parameter(name = "classId", in = ParameterIn.PATH, description = "ID of the DMS model to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of DMS model"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete DMS models or the DMS model does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object delete(
            @PathParam("classId") String classId
    ) {
        return dmsModelWs_adm.delete(classId);
    }

    @GET
    @Path("{"+ CLASS_ID + "}/attributes/{attrId}/")
    public Object readAttribute(@HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode, @PathParam(CLASS_ID) String classId, @PathParam("attrId") String attrId) {
        if (isAdminViewMode(viewMode)) {
            return dmsModelWs_adm.readAttribute(classId, attrId);
        }
        return dmsModelWs_mng.readAttribute(classId, attrId);
    }

    @GET
    @Path("{"+ CLASS_ID + "}/attributes/")
    @Operation(
            summary = "Get all attributes of a DMS model",
            description = "Get all attributes of a DMS model identified by the class ID in the path parameter. If the " +
                    "user has admin view permissions, all attributes of the DMS model will be returned. Otherwise, only " +
                    "the attributes for which the user has management permissions will be returned. The view mode can " +
                    "be forced by setting the '" + VIEW_MODE_HEADER_PARAM + "' header to 'admin' or 'management'. " +
                    "Additionally, pagination can be applied to the results by using the '" + LIMIT + "' and '" + START +
                    "' query parameters, where '" + LIMIT + "' specifies the maximum number of results to return and '" +
                    START + "' specifies the offset for pagination" ,
            parameters = {
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode to use for the request. Can be 'admin' or 'management'. If not set, the view mode will be determined based on the user's permissions"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "ID of the DMS model to retrieve the attributes for", required = true),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of results to return", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Offset for pagination of results", schema = @Schema(ref = "DefaultLimitExample"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of DMS model attributes data"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to view the attributes of the DMS model or the DMS model does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllAttributes(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            @QueryParam(LIMIT) Integer limit,
            @QueryParam(START) Integer offset
    ) {
        if (isAdminViewMode(viewMode)) {
            return dmsModelWs_adm.readAllAttributes(classId, limit, offset);
        }
        return dmsModelWs_mng.readAllAttributes(classId, limit, offset);
    }

    @POST
    @Path("{"+ CLASS_ID + "}/attributes/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Create a new attribute for a DMS model",
            description = "Create a new attribute for a DMS model. The request body should contain the data for the " +
                    "attribute to create, including the name and type of the attribute. The user must have admin " +
                    "permissions to create an attribute for a DMS model. The class ID in the path parameter identifies " +
                    "the DMS model to which the attribute will be added" ,
            parameters = {
                    @Parameter(name = "classId", in = ParameterIn.PATH, description = "ID of the DMS model to which the attribute will be added", required = true)
            },
            requestBody = @RequestBody(description = "Data for the new attribute", required = true, content = @Content(schema = @Schema(implementation = WsAttributeData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful creation of attribute"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to create attributes for DMS models or the DMS model does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object createAttribute(
            @PathParam(CLASS_ID) String classId,
            WsAttributeData data
    ) {
        return dmsModelWs_adm.createAttribute(classId, data);
    }

    @PUT
    @Path("{"+ CLASS_ID + "}/attributes/{attrId}/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Update an attribute of a DMS model",
            description = "Update an attribute of a DMS model. The request body should contain the new data for the " +
                    "attribute. The user must have admin permissions to update an attribute of a DMS model. The class ID " +
                    "in the path parameter identifies the DMS model to which the attribute belongs, and the attrId in " +
                    "the path parameter identifies the attribute to update. If the request body contains an attrId, it " +
                    "must match the attrId in the path parameter, otherwise the request will be rejected" ,
            parameters = {
                    @Parameter(name = "classId", in = ParameterIn.PATH, description = "ID of the DMS model to which the attribute belongs", required = true),
                    @Parameter(name = "attrId", in = ParameterIn.PATH, description = "ID of the attribute to update", required = true)
            },
            requestBody = @RequestBody(description = "Data for updating the attribute", required = true, content = @Content(schema = @Schema(implementation = WsAttributeData.class))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid or the attrId in the path parameter does not match the attrId in the request body"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to update attributes for DMS models or the DMS model or attribute does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object updateAttributes(
            @PathParam(CLASS_ID) String classId,
            @PathParam("attrId") String attrId,
            WsAttributeData data
    ) {
        return dmsModelWs_adm.updateAttributes(classId, attrId, data);
    }

    @DELETE
    @Path("{"+ CLASS_ID + "}/attributes/{attrId}/")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Delete an attribute of a DMS model",
            description = "Delete an attribute of a DMS model. The user must have admin permissions to delete an " +
                    "attribute of a DMS model. The class ID in the path parameter identifies the DMS model to which the " +
                    "attribute belongs, and the attrId in the path parameter identifies the attribute to delete" ,
            parameters = {
                    @Parameter(name = "classId", in = ParameterIn.PATH, description = "ID of the DMS model to which the attribute belongs", required = true),
                    @Parameter(name = "attrId", in = ParameterIn.PATH, description = "ID of the attribute to delete", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful deletion of attribute"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to delete attributes for DMS models or the DMS model or attribute does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object deleteAttributes(
            @PathParam(CLASS_ID) String classId,
            @PathParam("attrId") String attrId
    ) {
        return dmsModelWs_adm.deleteAttributes(classId, attrId);
    }

    @POST
    @Path("{"+ CLASS_ID + "}/attributes/order")
    @RolesAllowed(ADMIN_DMS_MODIFY_AUTHORITY)
    @Operation(
            summary = "Reorder attributes of a DMS model",
            description = "Reorder attributes of a DMS model. The request body should contain a list of attribute IDs in " +
                    "the desired order. The user must have admin permissions to reorder attributes of a DMS model. The " +
                    "class ID in the path parameter identifies the DMS model for which the attributes will be reordered" ,
            parameters = {
                    @Parameter(name = "classId", in = ParameterIn.PATH, description = "ID of the DMS model for which the attributes will be reordered", required = true)
            },
            requestBody = @RequestBody(description = "List of attribute IDs in the desired order", required = true, content = @Content(schema = @Schema(implementation = List.class, example = "[\"attrId1\", \"attrId2\", \"attrId3\"]"))),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful reordering of attributes"),
                    @ApiResponse(responseCode = "400", description = "Bad request - the request body is invalid"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to reorder attributes for DMS models or the DMS model does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object reorderAttributes(
            @PathParam(CLASS_ID) String classId,
            List<String> attrOrder
    ) {
        return dmsModelWs_adm.reorderAttributes(classId, attrOrder);
    }

    @GET
    @Path("{"+ CLASS_ID + "}/print_schema/{file}")
    @Produces(APPLICATION_OCTET_STREAM)
    @Operation(
            summary = "Print DMS model schema report",
            description = "Print a report of the schema of a DMS model. The report will include the attributes of the " +
                    "DMS model and their types. The user must have admin permissions to print a DMS model schema report. " +
                    "The class ID in the path parameter identifies the DMS model for which the schema report will be " +
                    "printed, and the file in the path parameter specifies the name of the file to which the report will " +
                    "be written. The extension query parameter can be used to specify the format of the report, such as " +
                    "'pdf' or 'xlsx'" ,
            parameters = {
                    @Parameter(name = "classId", in = ParameterIn.PATH, description = "ID of the DMS model for which the schema report will be printed", required = true),
                    @Parameter(name = "file", in = ParameterIn.PATH, description = "Name of the file to which the report will be written", required = true),
                    @Parameter(name = EXTENSION, in = ParameterIn.QUERY, description = "Format of the report, such as 'pdf' or 'xlsx'")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful generation of DMS model schema report"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - the user does not have permissions to print DMS mode schema reports or the DMS model does not exist"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {}), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler printModelSchemaReport(@PathParam(CLASS_ID) String classId, @PathParam("file") String fileName, @QueryParam(EXTENSION) String extension) {
        return dmsModelWs_adm.printModelSchemaReport(classId, fileName, extension);
    }
}
