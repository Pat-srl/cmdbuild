package org.cmdbuild.service.rest.v2.endpoint;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.apache.commons.lang3.StringUtils.EMPTY;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import static org.cmdbuild.service.rest.v2.endpoint.CardWsV2.mapSorterAttributeNames;
import org.cmdbuild.service.rest.v2.serializationhelpers.CardWsSerializationHelperV2;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

@Path("cql/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CqlWsV2 {

    private final DaoService dao;
    private final CardWsSerializationHelperV2 helper;

    public CqlWsV2(DaoService dao, CardWsSerializationHelperV2 helper) {
        this.dao = checkNotNull(dao);
        this.helper = checkNotNull(helper);
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@QueryParam(FILTER) String filter, @QueryParam(SORT) String sort, @QueryParam(LIMIT) Integer limit, @QueryParam(START) Integer offset) {
        CmdbFilter cmFilter = CmFilterUtils.parseFilter(filter);
        CmdbSorter sorter = mapSorterAttributeNames(CmSorterUtils.parseSorter(sort));

        List<Card> cards = dao.selectAll()
                .orderBy(sorter)
                .where(cmFilter)
                .paginate(offset, limit)
                .getCards();
        return map("data", cards.stream().map(helper::serializeCard).collect(toList()), "meta", map("total", cards.size()));
    }

}
