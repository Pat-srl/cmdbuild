/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.media.SchemaProperty;
import com.google.firebase.database.utilities.Pair;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.etl.loader.EtlProcessingResult;
import org.cmdbuild.etl.loader.EtlTemplate;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.EtlTemplateWsCommand;
import org.cmdbuild.service.rest.v4.model.WsEtlTemplateData;
import org.cmdbuild.temp.TempService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.etl.utils.EtlResultUtils.etlProcessingResultToJsonObject;
import static org.cmdbuild.service.rest.common.serializationhelpers.EtlTemplateSerializationHelper.filterAndApplySerialization;
import static org.cmdbuild.service.rest.common.serializationhelpers.EtlTemplateSerializationHelper.serializeDetailedTemplate;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.temp.TempInfoSource.TS_SECURE;
import static org.cmdbuild.utils.io.CmIoUtils.toDataHandler;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Path("etl/templates/")
@Tag(name = "ETL Templates")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class EtlTemplateWs_Management {

    private final TempService tempService;
    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3;
    private final EtlTemplateWsCommand command;

    public EtlTemplateWs_Management(TempService tempService, CardWsSerializationHelperv3 cardWsSerializationHelperv3, EtlTemplateWsCommand command) {
        this.tempService = checkNotNull(tempService);
        this.cardWsSerializationHelperv3 = checkNotNull(cardWsSerializationHelperv3);
        this.command = checkNotNull(command);
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get all active ETL templates",
            description = "Get all active ETL templates",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL templates"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {} ), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readAll(
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        List<EtlTemplate> etlTemplateList = command.doReadAll(EtlTemplate::isActive);
        return response(paged(filterAndApplySerialization(etlTemplateList, wsQueryOptions)));
    }

    @GET
    @Path("by-class/{" + CLASS_ID + "}")
    @Operation(
            summary = "Get all ETL templates for a given class",
            description = "Get all ETL templates for a given class",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL templates"),
                    @ApiResponse(responseCode = "404", description = "The class was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAllForClass(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query")  String classId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam("include_related_domains") @DefaultValue(FALSE) @Parameter(description = "Include related domains", schema = @Schema(type = "boolean", defaultValue = "false")) boolean includeRelatedDomains
    ) {
        List<EtlTemplate> etlTemplateList = command.doReadAllForClass(classId, includeRelatedDomains, EtlTemplate::isActive);

        return response(paged(filterAndApplySerialization(etlTemplateList, wsQueryOptions)));
    }

    @GET
    @Path("by-process/{" + CLASS_ID + "}")
    @Operation(
            summary = "Get all ETL templates for a given process",
            description = "Get all ETL templates for a given process",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL templates"),
                    @ApiResponse(responseCode = "404", description = "The process was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAllForProcess(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query")  String classId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam("include_related_domains") @DefaultValue(FALSE) @Parameter(description = "Include related domains", schema = @Schema(type = "boolean", defaultValue = "false")) boolean includeRelatedDomains
    ) {
        List<EtlTemplate> etlTemplateList = command.doReadAllForProcess(classId, includeRelatedDomains, EtlTemplate::isActive);

        return response(paged(filterAndApplySerialization(etlTemplateList, wsQueryOptions)));
    }

    @GET
    @Path("by-view/{viewId}")
    @Operation(
            summary = "Get all ETL templates for a given view",
            description = "Get all ETL templates for a given view",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL templates"),
                    @ApiResponse(responseCode = "404", description = "The view was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readAllForView(
            @PathParam("viewId") @Parameter(description = "Id of the view to query") String viewId,
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        List<EtlTemplate> etlTemplateList = command.doReadAllForView(viewId, EtlTemplate::isActive);

        return response(paged(filterAndApplySerialization(etlTemplateList, wsQueryOptions)));
    }

    @GET
    @Path("{templateId}/")
    @Operation(
            summary = "Get a specific ETL template",
            description = "Get a specific ETL template",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of ETL template"),
                    @ApiResponse(responseCode = "404", description = "The ETL template was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {} ), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public Object readOne(
            @PathParam("templateId") @Parameter(description = "Id or code of the template", schema = @Schema(type = "string")) String idOrCode
    ) {
        EtlTemplate template = command.doReadOne(idOrCode);
        return response(serializeDetailedTemplate(template));
    }

    @GET
    @Path("{templateId}/export|{templateId}/export/{fileName}")
    @Operation(
            summary = "Export data using an ETL template",
            description = "Export data using an ETL template",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful export of data"),
                    @ApiResponse(responseCode = "404", description = "The ETL template was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {} ), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler executeExportTemplate(
            @PathParam("templateId") @Parameter(description = "Id or code of the template", schema = @Schema(type = "string")) String idOrCode,
            @QueryParam(FILTER) @Parameter(description = "Filter to apply to the export", schema = @Schema(type = "string")) String filterStr
    ) {
        return new DataHandler(command.doExecuteExportTemplate(idOrCode, filterStr));
    }

    @POST
    @Path("{templateId}/import")
    @Operation(
            requestBody = @RequestBody(description = "File containing the data to import", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED))
                    })),
            summary = "Import data using an ETL template",
            description = "Import data using an ETL template",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful import of data"),
                    @ApiResponse(responseCode = "404", description = "The ETL template was not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {} ), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    public Object executeImportTemplate(
            @PathParam("templateId") @Parameter(description = "Id or code of the template", schema = @Schema(type = "string")) String idOrCode,
            @Multipart(value = FILE, required = true) DataHandler dataHandler,
            @QueryParam("detailed_report") @DefaultValue(FALSE) @Parameter(description = "Include detailed report", schema = @Schema(type = "boolean", defaultValue = "false")) Boolean detailedReport
    ) {
        Pair<EtlProcessingResult, DataSource> pair = command.doExecuteImportTemplate(idOrCode, dataHandler);
        return response(etlProcessingResultToJsonObject(pair.getFirst(), detailedReport).with(
                "report", map(
                        "contentType", pair.getSecond().getContentType(),
                        "filename", pair.getSecond().getName(),
                        "content", tempService.putTempData(pair.getSecond(), TS_SECURE)
                )));
    }

    @POST
    @Path("inline/export|inline/export/{fileName}")
    @Operation(
            requestBody = @RequestBody(description = "Inline data to export and the template configuration to use", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "data", schema = @Schema(type = "string", requiredMode = Schema.RequiredMode.REQUIRED)),
                    @SchemaProperty(name = "config", schema = @Schema(implementation = WsEtlTemplateData.class, requiredMode = Schema.RequiredMode.REQUIRED))
                    })),
            summary = "Export data using an inline ETL template",
            description = "Export data using an inline ETL template",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful export of data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {} ), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    public DataHandler executeInlineExportTemplate(
            @Multipart(value = "data", required = true) String data,
            @Multipart(value = "config", required = true) WsEtlTemplateData config
    ) {
        DataSource dataSource = command.doExecuteInlineExportTemplate(data, config);
        return toDataHandler(dataSource);
    }

    @POST
    @Path("inline/import")
    @Operation(
            requestBody = @RequestBody(description = "File containing the data to import and the template configuration to use", required = true,
                    content = @Content(mediaType = jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary", requiredMode = Schema.RequiredMode.REQUIRED)),
                    @SchemaProperty(name = "config", schema = @Schema(implementation = WsEtlTemplateData.class, requiredMode = Schema.RequiredMode.REQUIRED))
                    })),
            summary = "Import data using an inline ETL template",
            description = "Import data using an inline ETL template",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful import of data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement(name = "BasicAuth", scopes = {} ), @SecurityRequirement(name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    public Object executeInlineImportTemplate(
            @Multipart(value = FILE, required = true) DataHandler data,
            @Multipart(value = "config", required = true) WsEtlTemplateData config
    ) {
        List<Card> list = command.doExecuteInlineImportTemplate(data, config);
        return response(list(list).map(cardWsSerializationHelperv3::serializeCard));
    }
}
