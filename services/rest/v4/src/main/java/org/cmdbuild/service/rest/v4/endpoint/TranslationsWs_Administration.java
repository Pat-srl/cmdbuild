/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.media.SchemaProperty;
import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.Nullable;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.preload.PreloadService;
import org.cmdbuild.service.rest.v4.command.TranslationsWsCommand;
import org.cmdbuild.translation.ExportRecord;
import org.cmdbuild.translation.dao.Translation;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOCALIZATION_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOCALIZATION_VIEW_AUTHORITY;
import static org.cmdbuild.common.utils.PagedElements.paged;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
@Path("administration/translations/")
@Tag(name = "Translations", description = "Operations related to translations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class TranslationsWs_Administration {


    private final TranslationsWsCommand command;

    public TranslationsWs_Administration(TranslationsWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("loadTranslations")
    @Operation(
            summary = "Load translations for languages",
            description = "Load translations for languages",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public Object loadAllTranslationsForLanguages(
            @QueryParam("lang") @Parameter(description = "Comma-separated list of language codes to load") String languages
    ) {
        command.doLoadAllTranslationsForLanguages(languages);
        return response(success());
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all translations",
            description = "Get all translations",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public Object getAll(
            @Nullable @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @Nullable @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @Nullable @QueryParam(FILTER) @Parameter(description = "Filter translations by code", schema = @Schema(ref = "DefaultFilterExample")) String filter
    ) {
        PagedElements<Translation> translations = command.doGetAll(limit, offset, filter);
        return response(translations.map((t) -> map("code", t.getCode(), "lang", t.getLang(), "value", t.getValue())));
    }

    @GET
    @Path("by-code")
    @Operation(
            summary = "Get all translations aggregated by code",
            description = "Get all translations aggregated by code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public Object getAllAggregateByCode(
            @Nullable @QueryParam(LIMIT) @Parameter(description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")) Integer limit,
            @Nullable @QueryParam(START) @Parameter(description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0") Integer offset,
            @Nullable @QueryParam(FILTER) @Parameter(description = "Filter translations by code", schema = @Schema(ref = "DefaultFilterExample")) String filter,
            @Nullable @QueryParam("lang") @Parameter(description = "Filter translations by language", schema = @Schema(ref = "DefaultFilterExample")) String languages,
            @QueryParam("includeRecordsWithoutTranslation") @Parameter(description = "Include records without translation", schema = @Schema(type = "boolean")) @DefaultValue(FALSE) Boolean includeRecordsWithoutTranslation,
            @Nullable @QueryParam("section") @Parameter(description = "Filter translations by section", schema = @Schema(ref = "DefaultFilterExample")) String section
    ) {
        List<ExportRecord> exportRecordList = command.doGetAllAggregateByCode(section, languages, includeRecordsWithoutTranslation, filter);
        return response(paged(exportRecordList.stream().sorted(Ordering.natural().onResultOf(ExportRecord::getCode)).collect(toList()), offset, limit).map((t) -> map(
                "code", t.getCode(),
                "default", t.getDefault(),
                "values", map(t.getTranslationsByLanguage()).withoutValues(Strings::isNullOrEmpty))));
    }

    @GET
    @Path("{code}/")
    @Operation(
            summary = "Get translation for code",
            description = "Get translation for code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Translation not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public Object getTranslationForKeyAndLang(
            @PathParam("code") @Parameter(description = "Code of the translation to query", required = true) String code,
            @Nullable @QueryParam("lang") @Parameter(description = "Filter translations by language", schema = @Schema(ref = "DefaultFilterExample")) String lang
    ) {
        Map<String, String> translationValueMap = command.doGetTranslationForKeyAndLang(code, lang);
        if (isNotBlank(lang)) {
            return response(map("code", code, "lang", lang).with(translationValueMap));
        } else {
            return serializeResponse(code, translationValueMap);
        }
    }

    @PUT
    @Path("{code}/")
    @Operation(
            summary = "Set translation for code",
            description = "Set translation for code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Translation not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_MODIFY_AUTHORITY)
    public Object setTranslation(
            @PathParam("code") @Parameter(description = "Code of the translation to query", required = true) String code,
            @RequestBody(description = "Map of language code to translated text", required = true) Map<String, String> data
    ) {
        return serializeResponse(code, command.doSetTranslation(code, data));
    }

    @DELETE
    @Path("{code}/")
    @Operation(
            summary = "Delete translation for code",
            description = "Delete translation for code",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Translation not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_MODIFY_AUTHORITY)
    public Object deleteTranslation(
            @PathParam("code") @Parameter(description = "Code of the translation to query", required = true) String code,
            @Nullable @QueryParam("lang") @Parameter(description = "Filter translations by language", schema = @Schema(ref = "DefaultFilterExample")) String lang
    ) {
        command.doDeleteTranslation(code, lang);
        return success();
    }

    @GET
    @Path("export")
    @Operation(
            summary = "Export translations",
            description = "Export translations",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public DataHandler export(
            @QueryParam("lang") @Parameter(description = "Filter translations by language", schema = @Schema(ref = "DefaultFilterExample")) String languages,
            @QueryParam("format") @Parameter(description = "Format of the export", schema = @Schema(ref = "DefaultFilterExample")) String format,
            @QueryParam(FILTER) @Parameter(description = "Filter translations by code", schema = @Schema(ref = "DefaultFilterExample")) String filter,
            @QueryParam("separator") @Parameter(description = "Separator to use in the export", schema = @Schema(ref = "DefaultFilterExample")) String separator,
            @QueryParam("section") @Parameter(description = "Filter translations by section", schema = @Schema(ref = "DefaultFilterExample")) String section,
            @QueryParam("includeRecordsWithoutTranslation") @Parameter(description = "Include records without translation", schema = @Schema(type = "boolean")) @DefaultValue(FALSE) Boolean includeRecordsWithoutTranslation
    ) {
        checkArgument(isBlank(format) || equal(format.toLowerCase(), "csv"), "invalid format = %s", format);
        //TODO filter
        return command.doExport(languages, format, separator, section, includeRecordsWithoutTranslation);
    }

    @POST
    @Path("import")
    @Operation(
            summary = "Import translations",
            description = ",Import translations",
            requestBody = @RequestBody(description = "Multipart body: the translations file (csv) to import", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = FILE, schema = @Schema(type = "string", format = "binary"))
            })),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_LOCALIZATION_MODIFY_AUTHORITY)
    public Object importTranslations(
            @QueryParam("separator") @Parameter(description = "Separator to use in the import", schema = @Schema(ref = "DefaultFilterExample")) String separator,
            @Multipart(FILE) DataHandler dataHandler
    ) {
        command.doImportTranslations(separator, dataHandler);
        return success();
    }

    private Object serializeResponse(String code, Map<String, String> map) {
        return response(map("_id", code).with(map));
    }
}
