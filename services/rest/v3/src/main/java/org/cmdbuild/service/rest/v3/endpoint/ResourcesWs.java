package org.cmdbuild.service.rest.v3.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;

@Path("resources/")
@Tag( name = "Resources", description = "Operations related to resources management")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class ResourcesWs {

    private final org.cmdbuild.service.rest.v4.endpoint.ResourcesWs resourcesWs;

    public ResourcesWs(org.cmdbuild.service.rest.v4.endpoint.ResourcesWs resourcesWs) {
        this.resourcesWs = checkNotNull(resourcesWs);
    }

    @GET
    @Path("company_logo/{file}")
    @Operation(
            summary = "Download company logo",
            description = "Download company logo",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful download of company logo" ),
                    @ApiResponse( responseCode = "404", description = "Company logo not found" ),
                    @ApiResponse( responseCode = "500", description = "Internal server error" )
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadCompanyLogo() {
        return resourcesWs.downloadCompanyLogo();
    }

}
