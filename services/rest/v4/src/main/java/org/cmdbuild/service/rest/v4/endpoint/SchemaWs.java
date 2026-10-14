/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import static jakarta.ws.rs.core.MediaType.MULTIPART_FORM_DATA;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.apache.cxf.jaxrs.ext.multipart.Multipart;
import org.cmdbuild.modeldiff.diff.schema.GeneratedDiffSchema;
import org.cmdbuild.modeldiff.schema.SchemaConfiguration;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.lang.String.format;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ALL_AUTHORITY;
import org.cmdbuild.service.rest.v4.command.SchemaWsCommand;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.lang.String.format;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ALL_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.v4.endpoint.SchemaWs.SchemaExportType.ST_JSON;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.io.CmIoUtils.*;
import static org.cmdbuild.utils.json.CmJsonUtils.*;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

/**
 *
 * @author ataboga
 */
@Path("administration/schema/")
@Tag( name = "Schema", description = "Schema")
@RolesAllowed(ADMIN_ALL_AUTHORITY)
@Component
public class SchemaWs {

    private final SchemaWsCommand command;

    public enum SchemaExportType {
        ST_JSON, ST_FILE
    }

    public SchemaWs(SchemaWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("load")
    @Operation(
            summary = "Load schema",
            description = "Load schema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of schema data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                    @SecurityRequirement( name = "BasicAuth", scopes = {} ),
                    @SecurityRequirement( name = "BearerAuth", scopes = {} )
            }
    )
    public Object load(
            @QueryParam("name") @Parameter(description = "Name of the schema to load") @DefaultValue("schema") String name,
            @QueryParam("mode") @Parameter(description = "Mode of the schema to load") @DefaultValue("json") String mode
    ) {
        SchemaConfiguration config = command.doLoad(name);
        return switch (parseEnumOrDefault(mode, ST_JSON)) {
            case ST_JSON -> response(config);
            case ST_FILE -> newDataHandler(toJson(config).getBytes(), "application/json", format("%s.json", name));
        };
    }

    @POST
    @Path("diff")
    @Operation(
            summary = "Diff schema",
            description = "Diff schema",
            requestBody = @RequestBody(description = "Multipart body: the schema file to compare with the current schema", required = true, content = @Content(mediaType = MULTIPART_FORM_DATA, schemaProperties = {
                    @SchemaProperty(name = "file", schema = @Schema(type = "string", format = "binary"))
            })),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of diff data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                    @SecurityRequirement( name = "BasicAuth", scopes = {} ),
                    @SecurityRequirement( name = "BearerAuth", scopes = {} )
            }
    )
    public Object diff(
            @Multipart("file") DataHandler dataHandler
    ) throws IOException {
        GeneratedDiffSchema diffSchema = command.doDiff(dataHandler);
        return response(diffSchema);
    }

    @POST
    @Path("merge")
    @Operation(
            summary = "Merge schema",
            description = "Merge schema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful merge of schema data"),
                    @ApiResponse(responseCode = "400", description = "Bad request"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {
                    @SecurityRequirement( name = "BasicAuth", scopes = {} ),
                    @SecurityRequirement( name = "BearerAuth", scopes = {} )
            }
    )
    public Object merge(
            @RequestBody(description = "Schema differences to merge into the current schema", required = true) GeneratedDiffSchema diff
    ) {
        command.doMerge(diff);
        return response(serializeSchema(diff));
    }

    private CmMapUtils.FluentMap<String, String> serializeSchema(GeneratedDiffSchema diff) {
        return map("_id", diff.name, "description", diff.description);
    }
}
