package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("domains/")
@Produces(APPLICATION_JSON)
public class DomainsWsV2 {

    private final UserDomainService domainService;

    public DomainsWsV2(UserDomainService domainService) {
        this.domainService = checkNotNull(domainService);
    }

    @GET
    @Path(EMPTY)
    public Object readMany() {
        List<Domain> domains = domainService.getUserDomains();
        List list = domains.stream().map(this::serializeDomain).collect(toList());
        return map("data", list, "meta", map("total", list.size()));
    }

    @GET
    @Path("{domainId}/")
    public Object readOne(@PathParam("domainId") String domainId) {
        Domain domain = domainService.getUserDomain(domainId);
        return map("data", serializeDomain(domain));
    }

    private CmMapUtils.FluentMap<String, Object> serializeDomain(Domain input) {
        return map(
                "name", input.getName(),
                "description", input.getDescription(),
                "_id", input.getName());
    }

}
