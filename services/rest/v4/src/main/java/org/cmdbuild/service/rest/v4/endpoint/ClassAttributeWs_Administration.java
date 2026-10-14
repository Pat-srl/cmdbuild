/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.WsAttributeData;
import org.cmdbuild.service.rest.v4.command.ClassAttributeWsCommand;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_CLASSES_VIEW_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

/**
 *
 * @author schursin
 */
@Path("administration/{" + TYPE + ":classes|processes}/{" + CLASS_ID + "}/attributes/")
@Tags({
        @Tag(
                name = "Class attributes",
                description = """
                              The following documentation aims to illustrate the usage of the REST APIs by CMDBuild in order to retrieve all the information regarding the attributes of a class.
        
                              These information concern the type of attribute, all the metadata, possible auto values, showIf, validation rules, if the attribute is hidden, read only or can be modified.
                              """
        ),
        @Tag(name = "Administration")
})
@Produces(APPLICATION_JSON)
@Component
public class ClassAttributeWs_Administration {

    private final UserClassService userClassService;
    private final AttributeTypeConversionService attributeTypeConversionService;
    private final ClassAttributeWsCommand command;

    public ClassAttributeWs_Administration(UserClassService userClassService, AttributeTypeConversionService attributeTypeConversionService, ClassAttributeWsCommand command) {
        this.userClassService = checkNotNull(userClassService);
        this.attributeTypeConversionService = checkNotNull(attributeTypeConversionService);
        this.command = checkNotNull(command);
    }

    @GET
    @Path("{attrId}/")
    @Operation(
            summary = "Get all data for the chosen attribute",
            description = "Obtain the details of a specific attribute",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APIAttributesResponse"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Attribute not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public Object read(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @PathParam("attrId") @Parameter(required = true, description = "Name of the attribute to query") String attrId
    ) {
        Attribute attribute = command.doRead(classId, attrId);
        return response(attributeTypeConversionService.serializeAttributeType(attribute));
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all the attributes for the chosen class",
            description = "Obtain a list of every available attribute for the specified class",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful operation", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APIAttributesResponse"))),
                @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                @ApiResponse(responseCode = "404", description = "Attribute not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_VIEW_AUTHORITY)
    public Object readAll(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @QueryParam(LIMIT) @Parameter(description = "Number of attributes to return", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset
    ) {
        List<Attribute> listAttribute = command.doReadAll(classId, userClassService::getUserAttributes);
        return response(attributeTypeConversionService.serializeAndSort(listAttribute, limit, offset, true));
    }

    @POST
    @Path(EMPTY)
    @Operation(
            summary = "Create new attribute for the chosen class",
            description = "Create a new attribute for a specific class with the provided data. Some parameters are mandatory such as name, description, mode and type. Please note that for reference and lookup attributes, there will be more mandatory attributes",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attribute created successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APIAttributesResponse"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Attribute not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultError500Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object create(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @RequestBody(description = "Attribute data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultAttributeCreation"))) WsAttributeData data
    ) {
        Attribute attribute = command.doCreate(classId, data);
        return response(attributeTypeConversionService.serializeAttributeType(attribute));
    }

    @PUT
    @Path("{attrId}/")
    @Operation(
            summary = "Update an existing attribute",
            description = "Update an existing attribute with the provided data",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attribute updated successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APIAttributesResponse"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Attribute not found", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultClassAttributeError404Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal attribute type change", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultAttribute500TypeChangeError")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object update(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @PathParam("attrId") @Parameter(required = true, description = "Name of the attribute to update") String attrId,
            @RequestBody(description = "Attribute data", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultAttributeUpdate"))) WsAttributeData data
    ) {
        Attribute attribute = command.doUpdate(classId, data);
        return response(attributeTypeConversionService.serializeAttributeType(attribute));
    }

    @DELETE
    @Path("{attrId}/")
    @Operation(
            summary = "Delete an attribute",
            description = "Delete a specific attribute",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attribute deleted successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APIDeleteSuccessExample"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Attribute not found", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultClassAttributeError404Example"))),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultAttribute500DeleteError")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object delete(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @PathParam("attrId") @Parameter(required = true, description = "Name of the attribute to delete") String attrId
    ) {
        command.doDelete(classId, attrId);
        return success();
    }

    @POST
    @Path("order")
    @Operation(
            summary = "Reorder a list of attributes",
            description = "Reorder the list of attributes for a specific class with the new order set in the parameter attrOrder",
            parameters = {
                    @Parameter(name = TYPE, in = ParameterIn.PATH, description = "Type of the target: classes or processes", required = true, schema = @Schema(type = "string", allowableValues = {"classes", "processes"}))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Attributes reordered successfully", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "APIAttributesResponse"))),
                    @ApiResponse(responseCode = "401", description = "Full authentication is required to access this resource", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "Default401Example"))),
                    @ApiResponse(responseCode = "404", description = "Attribute not found", content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultClassAttributeError404Example")))
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {} )}
    )
    @RolesAllowed(ADMIN_CLASSES_MODIFY_AUTHORITY)
    public Object reorder(
            @PathParam(CLASS_ID) @Parameter(required = true, description = "Name of the class to query") String classId,
            @RequestBody(description = "Attribute order", required = true, content = @Content(mediaType = APPLICATION_JSON, schema = @Schema(ref = "DefaultAttributeOrder"))) List<String> attrOrder
    ) {
        Classe classe = command.doReorder(classId, attrOrder);
        return response(attrOrder.stream().map(classe::getAttribute).map(attributeTypeConversionService::serializeAttributeType).collect(toList()), attrOrder.size());
    }
}
