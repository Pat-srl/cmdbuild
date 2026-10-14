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
import jakarta.activation.DataHandler;
import jakarta.ws.rs.*;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ClassSerializationHelper;
import org.cmdbuild.service.rest.v4.command.DmsModelWsCommand;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FILTER_DEVICE;
import static org.cmdbuild.classe.access.UserClassService.ClassQueryFeatures.CQ_FOR_USER;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.service.rest.v4.command.WsUtils.filterSerializations;

/**
 *
 * @author ldare
 */
@Path("dms/models/")
@Tag(name = "DMS Models")
@Produces(APPLICATION_JSON)
@Component
public class DmsModelWs_Management {

    private static final Boolean ONLY_ACTIVE = true;

    private final DmsModelWsCommand command;
    private final ClassSerializationHelper classSerializationHelper;
    private final AttributeTypeConversionService attributeTypeConversionService;

    public DmsModelWs_Management(ClassSerializationHelper classSerializationHelper, AttributeTypeConversionService attributeTypeConversionService, DmsModelWsCommand command) {
        this.classSerializationHelper = checkNotNull(classSerializationHelper);
        this.attributeTypeConversionService = attributeTypeConversionService;
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all classes for the current user",
            description = "Obtain a list of all classes for the current user",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of classes"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @QueryParam(DETAILED) @DefaultValue(FALSE) @Parameter(description = "Include or not full details in the response") Boolean detailed,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @QueryParam(FILTER) @Parameter(description = "Filter classes by name") String filterStr
    ) {
        List<Classe> listClasse = command.doReadAll(ONLY_ACTIVE);

        List<CmMapUtils.FluentMap<String, Object>> listDmsSerialization = classSerializationHelper.serialize(listClasse, detailed);
        listDmsSerialization = filterSerializations(listDmsSerialization, filterStr);
        return response(paged(listDmsSerialization, offset, limit));
    }

    @GET
    @Path("{" + CLASS_ID + "}/")
    @Operation(
            summary = "Get a specific class",
            description = "Obtain details of a specific class",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of class"),
                @ApiResponse(responseCode = "404", description = "The class was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object read(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId
    ) {
        ExtendedClass extendedClass = command.doRead(classId, CQ_FOR_USER, CQ_FILTER_DEVICE);
        return response(classSerializationHelper.buildFullDetailExtendedResponse(extendedClass));
    }

    @GET
    @Path("{" + CLASS_ID + "}/attributes/{attrId}/")
    @Operation(
            summary = "Get all data for a specific attribute",
            description = "Obtain the details of a specific attribute",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of attribute"),
                @ApiResponse(responseCode = "404", description = "The attribute was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAttribute(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam("attrId") @Parameter(description = "Id of the attribute to query") String attrId
    ) {
        Attribute attribute = command.doReadAttribute(classId, attrId);
        return response(attributeTypeConversionService.serializeAttributeType(attribute));
    }

    @GET
    @Path("{" + CLASS_ID + "}/attributes/")
    @Operation(
            summary = "Get all attributes for a specific class",
            description = "Obtain a list of all attributes for a specific class",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of attributes"),
                @ApiResponse(responseCode = "404", description = "The class was not found"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAllAttributes(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset
    ) {
        Collection<Attribute> attributeCollection = command.doReadAllAttributes(classId, EntryType::getActiveServiceAttributes);
        List<CmMapUtils.FluentMap<String, Object>> attributeSerializations = attributeCollection.stream().map(attributeTypeConversionService::serializeAttributeType).collect(toList());
        return response(paged(attributeSerializations, offset, limit));
    }

    @GET
    @Path("{" + CLASS_ID + "}/print_schema/{file}")
    @Operation(
            summary = "Print model schema report",
            description = "Print model schema report",
            responses = {
                @ApiResponse(responseCode = "200", description = "Successful retrieval of model schema report"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                @SecurityRequirement(name = "BasicAuth", scopes = {}),
                @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler printModelSchemaReport(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @PathParam("file") @Parameter(description = "Name of the file to generate") String fileName,
            @QueryParam(EXTENSION) @Parameter(description = "Extension of the file to generate") String extension
    ) {
        return command.doPrintModelSchemaReport(classId, fileName, extension);
    }
}
