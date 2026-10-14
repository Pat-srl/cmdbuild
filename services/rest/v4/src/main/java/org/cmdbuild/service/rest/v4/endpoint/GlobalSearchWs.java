package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.data.filter.SorterElement;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardSerializer;
import org.cmdbuild.service.rest.v4.command.GlobalSearchWsCommand;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import java.util.Comparator;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.auth.role.RolePrivilegeAuthority.ADMIN_ACCESS_AUTHORITY;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.v4.command.GlobalSearchWsCommand.reorderComparator;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;

@Path("administration/globalsearch")
@Tag(name = "Global Search", description = "Global search for all classes and attributes")
@Produces(APPLICATION_JSON)
@RolesAllowed(ADMIN_ACCESS_AUTHORITY)
@Component
public class GlobalSearchWs {

    private final GlobalSearchWsCommand command;

    public GlobalSearchWs(GlobalSearchWsCommand command) {
        this.command = checkNotNull(command);
    }

    @GET
    @Path("")
    @Operation(
            summary = "Global search",
            description = "Global search for all classes and attributes",
            responses = {
                    @ApiResponse( responseCode = "200", description = "Successful retrieval of global search data"),
                    @ApiResponse( responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object globalSearch(
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        List<Card> cardList = command.doGlobalSearch(wsQueryOptions);

        SorterElement sorter = wsQueryOptions.getQuery().getSorter().getElements().stream().collect(onlyElement("global search cannot be ordered with more than one attribute"));
        Comparator<Card> cardSorter = reorderComparator(Comparator.comparing(c -> c.getString(sorter.getProperty())), sorter);
        Comparator<Card> classSorter = Comparator.comparing(Card::getTypeName);

        return response(cardList.stream().sorted(cardSorter).sorted(classSorter).map(CardSerializer::cardToMap));
    }
}
