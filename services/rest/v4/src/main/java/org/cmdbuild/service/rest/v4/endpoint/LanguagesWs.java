package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import org.cmdbuild.common.localization.LanguageInfo;
import org.cmdbuild.service.rest.v4.command.LanguagesWsCommand;
import org.cmdbuild.services.serialization.LanguageSerializer;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

@Path("administration/languages")
@Tag( name = "Languages", description = "Operations related to languages")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ACCESS_AUTHORITY)
@Component
public class LanguagesWs {

    private final LanguagesWsCommand command;

    public LanguagesWs(LanguagesWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get all languages",
            description = "Get all languages",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of language data")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object readLanguages(
            @QueryParam("active") @Parameter(description = "Only active languages", schema = @Schema(type = "boolean")) @DefaultValue(FALSE) Boolean activeOnly
    ) {
        Collection<LanguageInfo> list = command.doReadLanguages(activeOnly);
        return response(list.stream().map(LanguageSerializer::languageInfoToResponse).collect(toList()));
    }

}
