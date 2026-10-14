package org.cmdbuild.service.rest.v2.endpoint;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Function;
import static com.google.common.base.MoreObjects.firstNonNull;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
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
import jakarta.ws.rs.QueryParam;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.cardfilter.CardFilterService;
import org.cmdbuild.classe.access.UserCardAccess;
import org.cmdbuild.classe.access.UserCardQueryOptions;
import org.cmdbuild.classe.access.UserCardQueryOptionsImpl;
import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.common.utils.PagedElements;
import static org.cmdbuild.common.utils.PagedElements.hasLimit;
import static org.cmdbuild.common.utils.PagedElements.isPaged;
import org.cmdbuild.common.utils.PositionOf;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.common.beans.IdAndDescription;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_IDCLASS;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_USER;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.WhereOperator.EQ;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.data.filter.beans.CmdbSorterImpl;
import org.cmdbuild.data.filter.beans.SorterElementImpl;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import static org.cmdbuild.service.rest.common.utils.WsResponseUtils.success;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.FILTER;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.LIMIT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.POSITION_OF;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.SORT;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.START;
import org.cmdbuild.service.rest.v2.serializationhelpers.CardWsSerializationHelperV2;
import org.cmdbuild.utils.date.CmDateUtils;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;

@Path("classes/{classId}/cards/")
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
public class CardWsV2 {

    private final UserCardService cardService;
    private final DaoService dao;
    private final CardFilterService filterService;
    private final CardWsSerializationHelperV2 helper;

    public CardWsV2(UserCardService cardService, DaoService dao, CardFilterService filterService, CardWsSerializationHelperV2 helper) {
        this.cardService = checkNotNull(cardService);
        this.dao = checkNotNull(dao);
        this.filterService = checkNotNull(filterService);
        this.helper = checkNotNull(helper);
    }

    @POST
    @Path(EMPTY)
    public Object create(@PathParam("classId") String classId, WsCardData card) {
        return map("data", helper.serializeCard(cardService.createCard(classId, card.getValues())), "meta", map());
    }

    @GET
    @Path("{cardId}/")
    public Object readOne(@PathParam("classId") String classId, @PathParam("cardId") Long id, @QueryParam("includeModel") Boolean includeModel) {
        UserCardAccess cardAccess = cardService.getUserCardAccess(classId);
        Card card = cardAccess.addCardAccessPermissionsFromSubfilterMark(dao.selectAll().from(classId)
                .accept(cardAccess.addSubsetFilterMarkersToQueryVisitor()::accept)
                .where(cardAccess.getWholeClassFilter())
                .where(ATTR_ID, EQ, checkNotNull(id))
                .getCard());
        checkArgument(card.getType().hasServiceReadPermission(), "user not authorized to access card %s.%s", classId, id);
        Map metaRef = map();
        for (Entry<String, Object> attribute : card.getAttributeValues()) {
            if (card.get(attribute.getKey()) instanceof IdAndDescription) {
                IdAndDescription idAndDesc = (IdAndDescription) card.get(attribute.getKey());
                metaRef.put(idAndDesc.getId(), map("description", idAndDesc.getDescription()));
            }
        }
        return map("data", helper.serializeCard(card), "meta", map("total", null, "references", metaRef));
    }

    @GET
    @Path(EMPTY)
    public Object readMany(@PathParam("classId") String classId,
            @QueryParam(FILTER) String filterStr,
            @QueryParam(SORT) String sort,
            @QueryParam(LIMIT) Long limit,
            @QueryParam(START) Long offset,
            @QueryParam(POSITION_OF) Long positionOfCard,
            @QueryParam("positionOf_goToPage") Boolean goToPage,
            @QueryParam("forDomain_name") String forDomainName,
            @QueryParam("forDomain_direction") String forDomainDirection,
            @QueryParam("forDomain_originId") Long forDomainOriginId
    ) {
        CmdbFilter filter = CmFilterUtils.parseFilter(getFilterOrNull(filterStr));//TODO map filter attribute names
        CmdbSorter sorter = mapSorterAttributeNames(CmSorterUtils.parseSorter(sort));

        UserCardAccess cardAccess = cardService.getUserCardAccess(classId);
        CmdbFilter cardAccessFilter = cardAccess.getWholeClassFilter();

        filter = filter.and(cardAccessFilter);

        CmMapUtils.FluentMap meta = map();
        if (positionOfCard != null && limit == null) {
            limit = Long.MAX_VALUE;
        }
        DaoQueryOptions queryOptions = DaoQueryOptionsImpl.builder()
                .withFilter(filter)
                .withSorter(sorter)
                .withPaging(offset, limit)
                .withPositionOf(positionOfCard, Boolean.TRUE)
                .build();

        UserCardQueryOptions userQueryOptions = UserCardQueryOptionsImpl.builder()
                .withQueryOptions(queryOptions)
                .build();

        PagedElements<Card> cards = cardService.getUserCards(classId, userQueryOptions);

        Map metaRef = map();
        for (Card card : cards) {
            for (Entry<String, Object> attribute : card.getAttributeValues()) {
                if (card.get(attribute.getKey()) instanceof IdAndDescription) {
                    IdAndDescription idAndDesc = (IdAndDescription) card.get(attribute.getKey());
                    metaRef.put(idAndDesc.getId(), map("description", idAndDesc.getDescription()));
                }
            }
        }

        long total;
        if (isPaged(offset, limit)) {
            total = dao.selectCount()
                    .from(classId)
                    .where(filter)
                    .getCount();
        } else {
            total = cards.size();
        }
        Map positionMeta = map();
        if (isNotNullAndGtZero(positionOfCard)) {
            checkArgument(hasLimit(limit), "must set valid 'limit' along with 'positionOf'");
            Long rowNumber = dao.selectRowNumber().where(ATTR_ID, EQ, positionOfCard).then()
                    .from(classId)
                    .orderBy(sorter)
                    .where(filter)
                    .build().getRowNumberOrNull();
            if (rowNumber == null) {
                positionMeta = map("found", false);
            } else {
                positionMeta = map("found", true,
                        "position", rowNumber);
            }
        }

        return map("data", cards.stream().map(helper::serializeCard).collect(toList()), "meta", map("total", total, "positions", positionMeta));
    }

    @PUT
    @Path("{cardId}/")
    public Object update(@PathParam("classId") String classId, @PathParam("cardId") Long id, WsCardData card) {
        return map("data", helper.serializeCard(cardService.updateCard(classId, id, card.getValues())), "meta", map());
    }

    @DELETE
    @Path("{cardId}/")
    public Object delete(@PathParam("classId") String classId, @PathParam("cardId") Long id) {
        cardService.deleteCard(classId, id);
        return success();
    }

    public static Map<String, Object> handlePositionOfAndGetMeta(DaoQueryOptions queryOptions, PagedElements paged) {
        long offset = queryOptions.getOffset();
        if (paged.hasPositionOf()) {
            PositionOf positionOf = paged.getPositionOf();
            Map positionMeta;
            if (positionOf.foundCard()) {
                offset = positionOf.getActualOffset();
                positionMeta = map("hasPosition", true,
                        "position", positionOf.getPositionInTable() - 1);
            } else {
                positionMeta = map("found", false);
            }
            return map(queryOptions.getPositionOf(), positionMeta);
        } else {
            return emptyMap();
        }
    }

    public static class WsCardData {

        private final Map<String, Object> values;

        @JsonCreator
        public WsCardData(Map<String, Object> values) {
//            this.values = mapClassValues(checkNotNull(values)).immutable(); TODO
            this.values = values;
        }

        public Map<String, Object> getValues() {
            return values;
        }

    }

    private final static Map<String, String> SYSTEM_ATTR_NAME_MAPPING = ImmutableMap.of("_id", ATTR_ID,
            "_type", ATTR_IDCLASS,
            "_user", ATTR_USER,
            "_beginDate", ATTR_BEGINDATE
    );

    public static CmdbSorter mapSorterAttributeNames(CmdbSorter sorter) {//TODO use this everywhere
        return new CmdbSorterImpl(sorter.getElements().stream().map((e) -> new SorterElementImpl(firstNonNull(SYSTEM_ATTR_NAME_MAPPING.get(e.getProperty()), e.getProperty()), e.getDirection())).collect(toImmutableList()));
    }

    @Nullable
    private String getFilterOrNull(@Nullable String filter) {
        return getFilterOrNull(filter, (id) -> filterService.getById(id).getConfiguration());
    }

    @Nullable
    public static String getFilterOrNull(@Nullable String filter, Function<Long, String> filterRepo) {
        if (isBlank(filter)) {
            return null;
        } else {
            JsonPrimitive filterId = new JsonParser().parse(filter).getAsJsonObject().getAsJsonPrimitive("_id");
            if (filterId != null && !filterId.isJsonNull()) {
                return filterRepo.apply(filterId.getAsLong());
            } else {
                return filter;
            }
        }
    }

    public CmMapUtils.FluentMap serializeCard(Card card, CardWsSerializationHelperV2.ExtendedCardOptions... extendedCardOptions) {
        return map("_id", card.getId(),
                "_type", card.getType().getName(),
                "_user", card.getUser(),
                "_beginDate", CmDateUtils.toIsoDate(card.getBeginDate())
        );
    }

}
