package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.RelationImpl;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

@Path("domains/{domainId}/relations/")
@Produces(APPLICATION_JSON)
public class RelationsWsV2 {

    private final DaoService dao;

    public RelationsWsV2(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("domainId") String domainId, WsRelationData relationData) {
        CMRelation relation = RelationImpl.builder()
                .withType(dao.getDomain(domainId))
                .withSourceCard(dao.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(dao.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();
        relation = dao.create(relation);
        return map("data", serializeRelation(relation), "meta", map());
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("domainId") String domainId) {
        Domain domain = dao.getDomain(domainId);
        List<CMRelation> relations = dao.selectAll().from(domain).getRelations();
        return map("data", relations.stream().map(this::serializeRelation).collect(toList()), "meta", map("total", relations.size()));
    }

    @GET
    @Path("{relationId}/")
    public Object read(@PathParam("domainId") String domainId, @PathParam("relationId") Long relationId) {
        CMRelation relation = dao.getRelation(domainId, relationId);
        return map("data", serializeRelation(relation), "meta", map());
    }

    @PUT
    @Path("{relationId}/")
    public Object update(@PathParam("domainId") String domainId, @PathParam("relationId") Long relationId, WsRelationData relationData) {
        CMRelation relation = dao.getRelation(domainId, relationId);
        relation = RelationImpl.copyOf(relation)
                .withSourceCard(dao.getCard(relationData.getSourceClassId(), relationData.getSourceCardId()))
                .withTargetCard(dao.getCard(relationData.getDestinationClassId(), relationData.getDestinationCardId()))
                .addAttributes(relationData.getValues())
                .build();
        relation = dao.update(relation);
        return map("data", serializeRelation(relation), "meta", map());
    }

    @DELETE
    @Path("{relationId}/")
    public Object delete(@PathParam("domainId") String domainId, @PathParam("relationId") Long relationId) {
        CMRelation relation = dao.getRelation(domainId, relationId);
        dao.delete(relation);
        return success();
    }

    public static class WsRelationData {

        private final Long id, sourceId, destinationId;
        private final String type, sourceType, destinationType;
        @JsonAnySetter
        private final Map<String, Object> values = map();

        public WsRelationData(
                @JsonProperty("_id") Long id,
                @JsonProperty("_type") String type,
                @JsonProperty("_sourceId") Long sourceId,
                @JsonProperty("_sourceType") String sourceType,
                @JsonProperty("_destinationId") Long destinationId,
                @JsonProperty("_destinationType") String destinationType) {
            this.id = ltEqZeroToNull(id);
            this.type = checkNotBlank(type);
            this.sourceId = checkNotNullAndGtZero(sourceId);
            this.sourceType = checkNotBlank(sourceType);
            this.destinationId = checkNotNullAndGtZero(destinationId);
            this.destinationType = checkNotBlank(destinationType);
        }

        public @Nullable
        Long getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public Long getSourceCardId() {
            return sourceId;
        }

        public String getSourceClassId() {
            return sourceType;
        }

        public Long getDestinationCardId() {
            return destinationId;
        }

        public String getDestinationClassId() {
            return destinationType;
        }

        public Map<String, Object> getValues() {
            return values;
        }

    }

    private CmMapUtils.FluentMap<String, Object> serializeRelation(CMRelation relation) {
        return map(
                "_type", relation.getType().getName(),
                "_sourceId", relation.getSourceId(),
                "_destinationId", relation.getTargetId(),
                "_sourceDescription", relation.getSourceDescription(),
                "_id", relation.getId(),
                "_sourceType", relation.getType().getSourceClass().getName(),
                "_destinationType", relation.getType().getTargetClass().getName(),
                "_destinationDescription", relation.getTargetDescription()
        );
    }

}
