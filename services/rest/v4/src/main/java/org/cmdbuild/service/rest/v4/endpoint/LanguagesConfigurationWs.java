package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.common.localization.LanguageInfo;
import org.cmdbuild.service.rest.v4.command.LanguagesConfigurationWsCommand;
import org.cmdbuild.services.serialization.LanguageSerializer;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

@Path("configuration/languages/")
@Tag(name = "Languages configuration", description = "Operations related to languages configuration")
@Produces(APPLICATION_JSON)
@Component
public class LanguagesConfigurationWs {

    private final LanguagesConfigurationWsCommand command;

    public LanguagesConfigurationWs(LanguagesConfigurationWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Get list of available languages",
            description = "Get list of available languages",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of languages data"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {}), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object getLoginLanguages() {
        Collection<LanguageInfo> list = command.doGetLoginLanguages();
        return response(list.stream().map(LanguageSerializer::languageInfoToResponse).collect(toList()));
    }


}
