/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.homepage.HomepageData;
import org.cmdbuild.homepage.HomepageService;
import org.cmdbuild.service.rest.common.serializationhelpers.HomepageSerializationHelper;
import org.cmdbuild.service.rest.v4.command.HomepageWsCommand;
import org.cmdbuild.translation.ObjectTranslationService;
import org.springframework.stereotype.Component;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;

/**
 *
 * @author mbuzzulini
 */
@Path("homepage/")
@Tag(name = "Homepage", description = "APIs to manage the homepage")
@Produces(APPLICATION_JSON)
@Component
public class HomepageWs_Management {

    private final HomepageService homepageService;
    private final HomepageSerializationHelper helper;
    private final HomepageWsCommand command;

    public HomepageWs_Management(ObjectTranslationService objectTranslationService, HomepageService homepageService, HomepageWsCommand command) {
        this.homepageService = checkNotNull(homepageService);
        this.helper = new HomepageSerializationHelper(objectTranslationService);
        this.command = command;
    }

    @GET
    @Path(EMPTY)
    @Operation(
            summary = "Get the homepage for currentUser",
            description = "Retrieve details of the homepage for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                    @ApiResponse(responseCode = "404", description = "Homepage not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = { @SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object get(
    ) {
        HomepageData homepageData = command.doRead(homepageService::getForCurrentUser);
        return response(helper.serializeDetailedHomepageData(homepageData));
    }

}
