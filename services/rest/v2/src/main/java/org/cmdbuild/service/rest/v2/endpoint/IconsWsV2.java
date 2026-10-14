package org.cmdbuild.service.rest.v2.endpoint;

import javax.swing.Icon;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;

@Path("icons/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class IconsWsV2 {

    @POST
    @Path(EMPTY)
    public Object create(Icon icon) {
        return null;
    }

    @GET
    @Path(EMPTY)
    public Object readMany() {
        return null;
    }

    @GET
    @Path("{iconId}/")
    public Object readOne(@PathParam("iconId") Long id) {
        return null;
    }

    @PUT
    @Path("{iconId}/")
    public Object update(@PathParam("iconId") Long id, Icon icon) {
        return null;
    }

    @DELETE
    @Path("{iconId}/")
    public Object delete(@PathParam("iconId") Long id) {
        return null;
    }

}
