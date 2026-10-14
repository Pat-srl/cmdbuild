package org.cmdbuild.service.rest.v2.endpoint;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Path("classes/{classId}/geocards/")
@Produces(APPLICATION_JSON)
public class GeoCardsWsV2 {

    @GET
    @Path(EMPTY)
    public Object readAllGeometries(@PathParam("classId") String classId) {
        return null;
    }

    @GET
    @Path("{cardId}/")
    public Object readGeometry(@PathParam("classId") String classId, @PathParam("cardId") Long cardId) {
        return null;
    }

}
