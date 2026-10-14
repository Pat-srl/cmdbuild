package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import jakarta.ws.rs.DefaultValue;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.lookup.LookupService;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.ACTIVE;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.v2.endpoint.LookupTypesWsV2.decodeIfHex;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.lookup.LookupValue;

@Path("lookup_types/{lookupTypeId}/values/")
@Produces(APPLICATION_JSON)
public class LookupTypeValuesWsV2 {

    private final LookupService lookupService;
    private final TranslationService translationService;

    public LookupTypeValuesWsV2(LookupService lookupLogic, TranslationService translationService) {
        this.lookupService = checkNotNull(lookupLogic);
        this.translationService = checkNotNull(translationService);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("lookupTypeId") String lookupTypeId, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset, @QueryParam(FILTER) String filterStr, @QueryParam(ACTIVE) @DefaultValue(TRUE) Boolean activeOnly) {
        CmdbFilter filter = CmFilterUtils.parseFilter(filterStr);
        PagedElements<LookupValue> lookups = activeOnly ? lookupService.getActiveLookup(decodeIfHex(lookupTypeId), offset, limit, filter) : lookupService.getAllLookup(decodeIfHex(lookupTypeId), offset, limit, filter);
        return map("data", lookups.stream().map(this::serializeResponse).collect(toList()), "meta", map("total", lookups.size()));
    }

    @GET
    @Path("{lookupValueId}/")
    public Object readOne(@PathParam("lookupTypeId") String lookupTypeId, @PathParam("lookupValueId") Long lookupValueId) {
        LookupValue lookup = lookupService.getLookup(lookupValueId);
        return map("data", serializeResponse(lookup), "meta", map("total", null));
    }

    private Object serializeResponse(LookupValue lookup) {
        return map(
                "code", lookup.getCode(),
                "description", translationService.translateLookupDescriptionSafe(lookup.getType().getName(), lookup.getCode(), lookup.getDescription()),
                "number", lookup.getIndex(),
                "active", true,
                "default", lookup.isDefault(),
                "_id", lookup.getId(),
                "_type", lookup.getType().getName(),
                "parent_id", lookup.getParentId(),
                "parent_type", Optional.ofNullable(lookup.getParentTypeOrNull()).map(p -> lookupService.getLookupType(p).getName()).orElse(null));
    }

}
