/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.service.rest.v3.endpoint;

import jakarta.ws.rs.*;
import org.cmdbuild.bim.BimObject;
import org.cmdbuild.bim.BimService;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.response;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Deprecated // replace with GET bim/projects/123/values/myGlobalId (??)
@Path("bim/values/")
@Produces(APPLICATION_JSON)
public class BimValueWs {

    private final BimService service;

    public BimValueWs(BimService service) {
        this.service = checkNotNull(service);
    }

    @GET
    @Path("{globalId}")
    public Object getOne(@PathParam("globalId") String globalId, @QueryParam("if_exists") @DefaultValue(FALSE) Boolean checkIfExists) {
        BimObject value = service.getBimObjectForGlobalIdOrNull(globalId);
        if (checkIfExists) {
            if (value == null) {
                return response(map("exists", false));
            }
        } else {
            checkNotNull(value, "bim value not found for gid = %s", globalId);
        }
        return response(map(
                "_id", value.getId(),
                "ownerType", value.getOwnerClassId(),
                "ownerId", value.getOwnerCardId(),
                "projectId", value.getProjectId(),
                "globalId", value.getGlobalId()
        ).accept((m) -> {
            if (checkIfExists) {
                m.put("exists", true);
            }
        }));
    }

}
