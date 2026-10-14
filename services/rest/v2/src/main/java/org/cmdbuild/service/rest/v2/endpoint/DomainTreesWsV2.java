package org.cmdbuild.service.rest.v2.endpoint;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;

@Path("domainTrees")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class DomainTreesWsV2 {

    @GET
    @Path(EMPTY)
    public Object readMany(@QueryParam(FILTER) String filter, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        return null;
    }

    @GET
    @Path("{id}/")
    public Object readOne(@PathParam("id") String id) {
        return null;
    }

}
