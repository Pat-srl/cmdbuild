package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import org.cmdbuild.config.GraphConfiguration;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("configuration/graph/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class GraphConfigurationWsV2 {

    GraphConfiguration graph;

    public GraphConfigurationWsV2(GraphConfiguration graph) {
        this.graph = checkNotNull(graph);
    }

    @GET
    @Path(EMPTY)
    public Object read() {
        return map("data", graph.getConfig(), "meta", map("total", graph.getConfig().size()));
    }

}
