package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("configuration/languages/")
@Tag(name = "Languages configuration", description = "Operations related to languages configuration")
@Produces(APPLICATION_JSON)
public class LanguagesConfigurationWs {

    private final org.cmdbuild.service.rest.v4.endpoint.LanguagesConfigurationWs languagesConfigurationWs;

    public LanguagesConfigurationWs(org.cmdbuild.service.rest.v4.endpoint.LanguagesConfigurationWs languagesConfigurationWs) {
        this.languagesConfigurationWs = checkNotNull(languagesConfigurationWs);
    }

    /**
     * return a list of available languages. Does not require
     * authentication.<br>
     * return format is like this:
     * <pre><code>
     * {
     *  "data": [{
     *    "code": "en",
     *    "description": "English"
     *   },{
     *    "code": "sr",
     *    "description": "Srpski"
     *   },{
     *    "code": "ru",
     *    "description": "Русский"
     *   }]
     * }
     * </code></pre>
     *
     * @return language list
     */
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
        return languagesConfigurationWs.getLoginLanguages();
    }
}
