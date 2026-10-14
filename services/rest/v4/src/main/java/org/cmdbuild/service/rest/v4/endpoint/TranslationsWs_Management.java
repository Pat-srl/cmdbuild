/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.service.rest.v4.command.TranslationsWsCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;

/**
 * @author ldare
 */
@Path("translations/")
@Tag(name = "Translations", description = "Operations related to translations")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class TranslationsWs_Management {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final TranslationsWsCommand command;

    public TranslationsWs_Management(TranslationsWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("loadTranslations")
    @Operation(
            summary = "Load translations for languages",
            description = "Load translations for languages",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object loadAllTranslationsForLanguages(
            @QueryParam("lang") @Parameter(description = "Comma separated list of languages to load translations for") String languages
    ) {
        command.doLoadAllTranslationsForLanguages(languages);
        return response(success());
    }
}