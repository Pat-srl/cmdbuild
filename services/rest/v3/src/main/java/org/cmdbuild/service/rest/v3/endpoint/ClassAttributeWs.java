package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.service.rest.v4.endpoint.ClassAttributeWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.ClassAttributeWs_Management;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_MODIFY_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsRequestUtils.isAdminViewMode;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

@Path("{type:classes|processes}/{" + CLASS_ID + "}/attributes/")
@Tag(name = "Class attributes", description = "Operations related to attributes of classes")
@Produces(APPLICATION_JSON)
public class ClassAttributeWs {

    private final ClassAttributeWs_Administration classAttributeWs_adm;
    private final ClassAttributeWs_Management classAttributeWs_mng;

    public ClassAttributeWs(ClassAttributeWs_Administration classAttributeWs_adm, ClassAttributeWs_Management classAttributeWs_mng) {
        this.classAttributeWs_adm = checkNotNull(classAttributeWs_adm);
        this.classAttributeWs_mng = checkNotNull(classAttributeWs_mng);
    }

    @GET
    @Path("{attrId}/")
    @Operation(
            summary = "Get class attribute",
            description = "Get a specific attribute of a class by its ID. The response includes the attribute details " +
                    "such as name, type, and other relevant information.",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "viewMode", in = ParameterIn.HEADER, description = "View mode header to determine the context of the operation (admin or management)"),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode header to determine the context of the operation (admin or management)"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = "attrId", in = ParameterIn.PATH, description = "ID of the attribute to retrieve")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of the class attribute"),
                    @ApiResponse(responseCode = "404", description = "Class or attribute not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            @PathParam("attrId") String attrId
    ) {
        if (isAdminViewMode(viewMode)) {
            return classAttributeWs_adm.read(classId, attrId);
        }
        return classAttributeWs_mng.read(classId, attrId);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all class attributes",
            description = "Get a list of all attributes for a specific class. The response includes details of each " +
                    "attribute such as name, type, and other relevant information. Supports pagination through 'limit' " +
                    "and 'offset' query parameters.",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode header to determine the context of the operation (admin or management)"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to query"),
                    @Parameter(name = LIMIT, in = ParameterIn.QUERY, description = "Maximum number of attributes to return"),
                    @Parameter(name = START, in = ParameterIn.QUERY, description = "Number of attributes to skip before starting to collect the result set")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of class attributes"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @HeaderParam(VIEW_MODE_HEADER_PARAM)  String viewMode,
            @PathParam(CLASS_ID)  String classId,
            @QueryParam(LIMIT)  Integer limit,
            @QueryParam(START)  Integer offset
    ) {
        if (isAdminViewMode(viewMode)) {
            return classAttributeWs_adm.readAll(classId, limit, offset);
        }
        return classAttributeWs_mng.readAll(classId, limit, offset);
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create class attribute",
            description = "Create a new attribute for a specific class. The request body should contain the details of " +
                    "the attribute to be created, such as name, type, and other relevant information. This endpoint is " +
                    "accessible only to users with the ADMIN_CLASSES_MODIFY_AUTHORITY role. Upon successful creation, " +
                    "the response includes the details of the newly created attribute.",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "viewMode", in = ParameterIn.HEADER, description = "View mode header to determine the context of the operation (admin or management)"),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode header to determine the context of the operation (admin or management)"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to create the attribute for"),
            },
            responses = {
                    @ApiResponse(responseCode = "201", description = "Class attribute created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have the necessary permissions"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object create(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId, WsAttributeData data
    ) {
        if (isAdminViewMode(viewMode)) {
            return classAttributeWs_adm.create(classId, data);
        }
        throw runtime("Operation blocked. You don't have the permissions");
    }

    @PUT
    @Path("{attrId}/")
    @Operation(
            summary = "Update class attribute",
            description = "Update an existing attribute of a specific class. The request body should contain the updated " +
                    "details of the attribute, such as name, type, and other relevant information. This endpoint is " +
                    "accessible only to users with the ADMIN_CLASSES_MODIFY_AUTHORITY role. Upon successful update, the " +
                    "response includes the details of the updated attribute.",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode header to determine the context of the operation (admin or management)"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to update the attribute for"),
                    @Parameter(name = "attrId", in = ParameterIn.PATH, description = "ID of the attribute to be updated")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Class attribute updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have the necessary permissions"),
                    @ApiResponse(responseCode = "404", description = "Class or attribute not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object update(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            @PathParam("attrId") String attrId, WsAttributeData data
    ) {
        if (isAdminViewMode(viewMode)) {
            return classAttributeWs_adm.update(classId, attrId, data);
        }
        throw runtime("Operation blocked. You don't have the permissions");
    }

    @DELETE
    @Path("{attrId}/")
    @Operation(
            summary = "Delete class attribute",
            description = "Delete an existing attribute of a specific class. This endpoint is accessible only to users " +
                    "with the ADMIN_CLASSES_MODIFY authority role. Upon successful deletion, the response confirms that " +
                    "the attribute has been removed.",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = "viewMode", in = ParameterIn.HEADER, description = "View mode header to determine the context of the operation (admin or management)"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class to delete the attribute from"),
                    @Parameter(name = "attrId", in = ParameterIn.PATH, description = "ID of the attribute to be deleted")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Class attribute deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have the necessary permissions"),
                    @ApiResponse(responseCode = "404", description = "Class or attribute not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(CLASS_ID) String classId,
            @PathParam("attrId") String attrId
    ) {
        return classAttributeWs_adm.delete(classId, attrId);
    }

    /**
     * Reorders the attributes of a class according to the provided list of
     * attribute IDs.
     * <p>
     * This endpoint is accessible only to users with the
     * {@code ADMIN_CLASSES_MODIFY_AUTHORITY} role. It updates the order (index)
     * of the attributes for the specified class to match the order of IDs in
     * {@code attrOrder}. After updating, it retrieves the class again and
     * returns a list of the reordered attributes, each serialized as a map,
     * along with the total count.
     * </p>
     *
     * @param viewMode the view mode header parameter (used for context, not
     * directly in this method)
     * @param classId the ID of the class whose attributes are to be reordered
     * @param attrOrder the list of attribute IDs in the desired order
     * @return a REST response containing the list of serialized attributes in
     * the new order and the total count
     * @throws NullPointerException if {@code attrOrder} is null
     */
    @POST
    @Path("order")
    @Operation(
            summary = "Reorder class attributes",
            description = "Reorder the attributes of a specific class according to the provided list of attribute IDs. " +
                    "This endpoint is accessible only to users with the ADMIN_CLASSES_MODIFY authority role. Upon " +
                    "successful reordering, the response includes the list of attributes in the new order along with the " +
                    "total count.",
            parameters = {
                    @Parameter(name = "type", in = ParameterIn.PATH, description = "processes or classes", schema = @Schema(allowableValues = {"processes", "classes"})),
                    @Parameter(name = VIEW_MODE_HEADER_PARAM, in = ParameterIn.HEADER, description = "View mode header to determine the context of the operation (admin or management)"),
                    @Parameter(name = CLASS_ID, in = ParameterIn.PATH, description = "Name of the class whose attributes are to be reordered"),
                    @Parameter(name = "attrOrder", in = ParameterIn.DEFAULT, description = "List of attribute IDs in the desired order", schema = @Schema(type = "array", example = "[\"attrId1\", \"attrId2\", \"attrId3\"]"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Class attributes reordered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid request body or parameters"),
                    @ApiResponse(responseCode = "403", description = "Forbidden - user does not have the necessary permissions"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object reorder(
            @HeaderParam(VIEW_MODE_HEADER_PARAM) String viewMode,
            @PathParam(CLASS_ID) String classId,
            List<String> attrOrder
    ) {
        if (isAdminViewMode(viewMode)) {
            return classAttributeWs_adm.reorder(classId, attrOrder);
        }
        throw runtime("Operation blocked. You don't have the permissions");
    }
}
