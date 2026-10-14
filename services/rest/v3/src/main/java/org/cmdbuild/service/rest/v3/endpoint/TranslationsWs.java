package org.cmdbuild.service.rest.v3.endpoint;

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
import org.cmdbuild.service.rest.v4.endpoint.TranslationsWs_Administration;
import org.cmdbuild.service.rest.v4.endpoint.TranslationsWs_Management;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOCALIZATION_MODIFY_AUTHORITY;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_LOCALIZATION_VIEW_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.*;

@Path("translations/")
@Tag(name = "Translations", description = "Operations related to translations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class TranslationsWs {

    private final TranslationsWs_Administration translationsWs_adm;
    private final TranslationsWs_Management translationsWs_mng;

    public TranslationsWs(TranslationsWs_Administration translationsWs_adm, TranslationsWs_Management translationsWs_mng) {
        this.translationsWs_adm = checkNotNull(translationsWs_adm);
        this.translationsWs_mng = checkNotNull(translationsWs_mng);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all translations",
            description = "Get all translations",
            parameters = {
                    @Parameter(name = LIMIT, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = FILTER, description = "Filter to apply to the query")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public Object getAll(
            @Nullable @QueryParam(LIMIT) Integer limit,
            @Nullable @QueryParam(START) Integer offset,
            @Nullable @QueryParam(FILTER) String filter
    ) {
        return translationsWs_adm.getAll(limit, offset, filter);
    }

    @GET
    @Path("loadTranslations")
    @Operation(
            summary = "Load translations for languages",
            description = "Load translations for languages",
            parameters = {@Parameter(name = "lang", description = "Comma-separated list of languages to load translations for", schema = @Schema(example = "en,fr,de"))},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object loadAllTranslationsForLanguages(
            @QueryParam("lang") String languages
    ) {
        return translationsWs_mng.loadAllTranslationsForLanguages(languages);
    }

    @GET
    @Path("by-code")
    @Operation(
            summary = "Get all translations aggregated by code",
            description = "Get all translations aggregated by code",
            parameters = {
                    @Parameter(name = LIMIT, description = "Number of items to return in the response", schema = @Schema(ref = "DefaultLimitExample")),
                    @Parameter(name = START, description = "A long value to set an offset in the resultset", schema = @Schema(minimum = "0"), example = "0"),
                    @Parameter(name = FILTER, description = "Filter to apply to the query"),
                    @Parameter(name = "lang", description = "Comma-separated list of languages to include in the response", schema = @Schema(example = "en,fr,de")),
                    @Parameter(name = "includeRecordsWithoutTranslation", description = "Whether to include records without translation in the response", schema = @Schema(type = "boolean", defaultValue = FALSE)),
                    @Parameter(name = "section", description = "Section to filter by")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public Object getAllAggregateByCode(
            @Nullable @QueryParam(LIMIT) Integer limit,
            @Nullable @QueryParam(START) Integer offset,
            @Nullable @QueryParam(FILTER) String filter,
            @Nullable @QueryParam("lang") String languages,
            @QueryParam("includeRecordsWithoutTranslation") @DefaultValue(FALSE) Boolean includeRecordsWithoutTranslation,
            @Nullable @QueryParam("section") String section
    ) {
        return translationsWs_adm.getAllAggregateByCode(limit, offset, filter, languages, includeRecordsWithoutTranslation, section);
    }

    @GET
    @Path("{code}/")
    @Operation(
            summary = "Get translation for code",
            description = "Get translation for code",
            parameters = {
                    @Parameter(name = "code", description = "Code to retrieve the translation for", schema = @Schema(example = "card.name")),
                    @Parameter(name = "lang", description = "Language to retrieve the translation for", schema = @Schema(example = "en"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public Object getTranslationForKeyAndLang(
            @PathParam("code") String code,
            @Nullable @QueryParam("lang") String lang
    ) {
        return translationsWs_adm.getTranslationForKeyAndLang(code, lang);
    }

    @PUT
    @Path("{code}/")
    @Operation(
            summary = "Set translation for code",
            description = "Set translation for code",
            parameters = {
                    @Parameter(name = "code", description = "Code to set the translation for", schema = @Schema(example = "card.name")),
            },
            requestBody = @RequestBody(description = "Translation data", required = true, content = @Content(mediaType = APPLICATION_JSON)),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_MODIFY_AUTHORITY)
    public Object setTranslation(
            @PathParam("code") String code,
            Map<String, String> data
    ) {
        return translationsWs_adm.setTranslation(code, data);
    }

    @DELETE
    @Path("{code}/")
    @Operation(
            summary = "Delete translation for code",
            description = "Delete translation for code",
            parameters = {
                    @Parameter(name = "code", description = "Code to delete the translation for", schema = @Schema(example = "card.name")),
                    @Parameter(name = "lang", description = "Language to delete the translation for", schema = @Schema(example = "en"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_MODIFY_AUTHORITY)
    public Object deleteTranslation(
            @PathParam("code") String code,
            @Nullable @QueryParam("lang") String lang
    ) {
        return translationsWs_adm.deleteTranslation(code, lang);
    }

    @GET
    @Path("export")
    @Operation(
            summary = "Export translations",
            description = "Export translations",
            parameters = {
                    @Parameter(name = "lang", description = "Comma-separated list of languages to export translations for", schema = @Schema(example = "en,fr,de")),
                    @Parameter(name = "format", description = "Format to export translations in", schema = @Schema(allowableValues = {"csv", "json"})),
                    @Parameter(name = FILTER, description = "Filter to apply to the query"),
                    @Parameter(name = "separator", description = "Separator to use for CSV format", schema = @Schema(defaultValue = ",")),
                    @Parameter(name = "section", description = "Section to filter by"),
                    @Parameter(name = "includeRecordsWithoutTranslation", description = "Whether to include records without translation in the export", schema = @Schema(type = "boolean", defaultValue = FALSE))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @RolesAllowed(ADMIN_LOCALIZATION_VIEW_AUTHORITY)
    public DataHandler export(
            @QueryParam("lang") String languages,
            @QueryParam("format") String format,
            @QueryParam(FILTER) String filter,
            @QueryParam("separator") String separator,
            @QueryParam("section") String section,
            @QueryParam("includeRecordsWithoutTranslation") @DefaultValue(FALSE) Boolean includeRecordsWithoutTranslation
    ) {
        return translationsWs_adm.export(languages, format, filter, separator, section, includeRecordsWithoutTranslation);
    }

    @POST
    @Path("import")
    @Operation(
            summary = "Import translations",
            description = "Import translations",
            parameters = {
                    @Parameter(name = "separator", description = "Separator to use for CSV format", schema = @Schema(defaultValue = ","))
            },
            requestBody = @RequestBody(description = "File to import", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA)),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Bad request - invalid query parameters provided"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Consumes(MULTIPART_FORM_DATA)
    @RolesAllowed(ADMIN_LOCALIZATION_MODIFY_AUTHORITY)
    public Object importTranslations(
            @QueryParam("separator") String separator,
            @Multipart(FILE) DataHandler dataHandler
    ) {
        return translationsWs_adm.importTranslations(separator, dataHandler);
    }
}
