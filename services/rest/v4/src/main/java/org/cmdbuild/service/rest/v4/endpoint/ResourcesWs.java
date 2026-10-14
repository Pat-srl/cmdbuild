package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.activation.DataHandler;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.easyupload.EasyuploadItem;
import org.cmdbuild.easyupload.EasyuploadUtils;
import org.cmdbuild.service.rest.v4.command.ResourcesWsCommand;
import org.springframework.stereotype.Component;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.APPLICATION_OCTET_STREAM;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;

@Path("resources/")
@Tag( name = "Resources", description = "Operations related to resources management")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ResourcesWs {

    private final ResourcesWsCommand command;

    public ResourcesWs(ResourcesWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("company_logo/{file}")
    @Operation(
            summary = "Download company logo",
            description = "Download company logo",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful download of company logo" ),
                    @ApiResponse( responseCode = "404", description = "The company logo was not found" ),
                    @ApiResponse( responseCode = "500", description = "Internal server error" )
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    @Produces(APPLICATION_OCTET_STREAM)
    public DataHandler downloadCompanyLogo() {
        EasyuploadItem logo = command.doDownloadCompanyLogo();
        return EasyuploadUtils.toDataHandler(logo);
    }

}
