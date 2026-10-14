package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import org.cmdbuild.cardfilter.StoredFilterImpl;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Path("classes/{classId}/temporary_filters/")
@Produces(APPLICATION_JSON)
public class ClassTemporaryFiltersWsV2 {

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("classId") String classId, WsFilterData element) {
        return null;
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("classId") String classId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        return null;
    }

    @GET
    @Path("{filterId}/")
    public Object readOne(@PathParam("classId") String classId, @PathParam("filterId") Long filterId) {
        return null;
    }

    @PUT
    @Path("{filterId}/")
    public Object update(@PathParam("classId") String classId, @PathParam("filterId") Long filterId, WsFilterData element) {
        return null;
    }

    @DELETE
    @Path("{filterId}/")
    public Object delete(@PathParam("classId") String classId, @PathParam("filterId") Long filterId) {
        return null;
    }

    public static class WsFilterData {

        private final String name;
        private final String description;
        private final String target;
        private final String configuration;
        private final boolean shared;

        public WsFilterData(@JsonProperty("name") String name,
                @JsonProperty("description") String description,
                @JsonProperty("target") String target,
                @JsonProperty("configuration") String configuration,
                @JsonProperty("shared") Boolean shared) {
            this.name = checkNotBlank(name, "missing required param 'name'");
            this.description = description;
            this.target = checkNotBlank(target, "missing required param 'target'");
            this.configuration = configuration;
            this.shared = shared;
        }

        public StoredFilterImpl.StoredFilterImplBuilder toCardFilter() {
            return StoredFilterImpl.builder()
                    .withOwnerName(target)
                    .withConfiguration(configuration)
                    .withDescription(description)
                    .withName(name)
                    .withShared(shared);
        }
    }
}
