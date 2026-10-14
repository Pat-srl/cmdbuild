package org.cmdbuild.service.rest.v4.endpoint;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.*;
import org.cmdbuild.classe.access.UserClassService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.driver.postgres.q3.stats.StatsQueryResponse;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.ClassStatsWsCommand;
import org.cmdbuild.utils.lang.CmPreconditions;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import static org.cmdbuild.dao.utils.DomainUtils.getActualCascadeAction;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.CLASS_ID;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SELECT;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("classes/{" + CLASS_ID + "}/")
@Tag(name = "Class stats", description = "Operations related to stats of classes")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@Component
public class ClassStatsWs {

    private final UserClassService userClassService;
    private final CardWsSerializationHelperv3 cardWsSerializationHelperv3;
    private final ClassStatsWsCommand command;

    public ClassStatsWs(UserClassService userClassService, CardWsSerializationHelperv3 cardWsSerializationHelperv3, ClassStatsWsCommand command) {
        this.userClassService = checkNotNull(userClassService);
        this.cardWsSerializationHelperv3 = checkNotNull(cardWsSerializationHelperv3);
        this.command = CmPreconditions.checkNotNull(command);
    }

    @GET
    @Path("stats")
    @Operation(
            summary = "Get stats for a class",
            description = "Get stats for a class",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of stats data"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object stats(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @BeanParam WsQueryOptions wsQueryOptions,
            @QueryParam(SELECT) @Parameter(description = "Attribute to select") String select
    ) {
        StatsQueryResponse response = command.doStats(classId, wsQueryOptions, select);

        Classe classe = userClassService.getUserClass(classId);
        return response(map("aggregate", response.getAggregateResults().stream().map(r -> map("attribute", r.getAttribute(), "operation", serializeEnum(r.getOperation())).accept(m -> {
            cardWsSerializationHelperv3.serializeAttributeValue(classe, r.getAttribute(), r.getResult()).mapKeys(k -> k.replaceFirst(Pattern.quote(r.getAttribute()), "result")).forEach(m::put);
        })).collect(toImmutableList())));
    }

    @GET
    @Path("relations")
    @Operation(
            summary = "Get relations stats for a class",
            description = "Get relations stats for a class",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successful retrieval of relations stats data"),
                    @ApiResponse(responseCode = "404", description = "Class not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameters"),
                    @ApiResponse(responseCode = "403", description = "Forbidden"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            },
            security = {@SecurityRequirement( name = "BasicAuth", scopes = {} ), @SecurityRequirement( name = "BearerAuth", scopes = {})}
    )
    public Object relations(
            @PathParam(CLASS_ID) @Parameter(description = "Name of the class to query") String classId,
            @BeanParam WsQueryOptions wsQueryOptions
    ) {
        List<UserDomainService.CardDomainRelationStats> relationStatsList = command.doRelations(classId, wsQueryOptions);
        return response(list(relationStatsList).map(s -> map(
                "domain", s.getDomain().getName(),
                "direction", serializeEnum(s.getDirection()),
                "cardinality", serializeDomainCardinality(s.getDomain().getCardinality()),
                "cascadeAction", s.getDirection().equals(RD_DIRECT)
                ? serializeEnum(getActualCascadeAction(s.getDomain(), s.getDomain().getMetadata().getCascadeActionDirect(), RD_DIRECT))
                : serializeEnum(getActualCascadeAction(s.getDomain(), s.getDomain().getMetadata().getCascadeActionInverse(), RD_INVERSE)),
                "count", s.getRelationCount()
        )));
    }

}
