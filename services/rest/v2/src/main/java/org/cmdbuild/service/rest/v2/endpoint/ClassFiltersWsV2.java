package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.function.Consumer;
import static java.util.stream.Collectors.toList;
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
import org.cmdbuild.auth.user.OperationUserSupplier;
import org.cmdbuild.cardfilter.StoredFilterImpl;
import org.cmdbuild.cardfilter.CardFilterService;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.cardfilter.StoredFilter;

@Path("classes/{classId}/filters/")
@Produces(APPLICATION_JSON)
public class ClassFiltersWsV2 {

    private final CardFilterService filterService;
    private final OperationUserSupplier userStore;

    public ClassFiltersWsV2(CardFilterService filterService, OperationUserSupplier userStore) {
        this.filterService = checkNotNull(filterService);
        this.userStore = checkNotNull(userStore);
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("classId") String classId, WsFilterData element) {
        StoredFilter filter = filterService.create(element.toCardFilter().accept(setCurrentUserForNonSharedFiltersVisitor(element)).build());
        return serializeFilter(filter);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("classId") String classId, @QueryParam("shared") boolean sharedOnly) {
        List<StoredFilter> list;
        list = filterService.readAllSharedFilters();
        return map("data", list.stream().map(this::serializeFilter).collect(toList()), "meta", map("total", list.size()));
    }

    @GET
    @Path("{filterId}/")
    public Object readOne(@PathParam("classId") String classId, @PathParam("filterId") Long filterId) {
        return map("data", serializeFilter(filterService.getById(filterId)), "meta", map());
    }

    @PUT
    @Path("{filterId}/")
    public Object update(@PathParam("classId") String classId, @PathParam("filterId") Long filterId, WsFilterData element) {
        StoredFilter filter = filterService.update(element.toCardFilter().withId(filterId).accept(setCurrentUserForNonSharedFiltersVisitor(element)).build());
        return serializeFilter(filter);
    }

    @DELETE
    @Path("{filterId}/")
    public Object delete(@PathParam("classId") String classId, @PathParam("filterId") Long filterId) {
        filterService.delete(filterId);
        return success();
    }

    private Consumer<StoredFilterImpl.StoredFilterImplBuilder> setCurrentUserForNonSharedFiltersVisitor(WsFilterData data) {
        return (b) -> {
            if (!data.shared) {
                b.withUserId(userStore.getUser().getLoginUser().getId());
            }
        };
    }

    public static class WsFilterData {

        private final String name;
        private final String description;
        private final String target;
        private final String configuration;
        public final boolean shared;

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

    private CmMapUtils.FluentMap<String, Object> serializeFilter(StoredFilter filter) {
        return map(
                "_id", filter.getId(),
                "name", filter.getName(),
                "description", filter.getDescription(),
                "target", filter.getOwnerName(),
                "configuration", filter.getConfiguration(),
                "shared", filter.isShared()
        );
    }

}
