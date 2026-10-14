/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Streams.stream;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import org.cmdbuild.client.rest.api.CardApi;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.model.Card;
import org.cmdbuild.client.rest.model.RestApiCardImpl;
import org.cmdbuild.dao.utils.CmFilterUtils;
import org.cmdbuild.dao.utils.CmSorterUtils;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;

public class CardApiImpl extends AbstractServiceClientImpl implements CardApi {

    public CardApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public Card createCard(String classId, Card card) {
        logger.debug("delete card for classId = {} card data = {}", classId, card);
        Map map = map(card.getAttributes());
        Card newCard = responseToCard(post(format("classes/%s/cards", checkNotBlank(classId)), map).asJson());
        logger.info("created card with id = {} type =< {} >", newCard.getId(), newCard.getTypeName());
        return newCard;
    }

    @Override
    public Card updateCard(Card card) {
        return responseToCard(put(format("classes/%s/cards/%s", checkNotBlank(card.getClassName()), card.getId()), map(card.getAttributes())).asJson());
    }

    @Override
    public CardApi deleteCard(String classId, Object cardId) {
        logger.debug("delete card for classId = {} cardId = {}", classId, cardId);
        delete(format("classes/%s/cards/%s", checkNotBlank(classId), toStringNotBlank(cardId)));
        return this;
    }

    @Override
    public Card getCard(String classId, Object cardId) {
        logger.debug("get card for classId = {} cardId = {}", classId, cardId);
        JsonElement response = get(format("classes/%s/cards/%s", checkNotBlank(classId), toStringNotBlank(cardId))).asJson();
        Map<String, Object> attributes = readAttributes(response.getAsJsonObject().getAsJsonObject("data"));
        checkArgument(equal(toStringNotBlank(attributes.get("_id")), toStringNotBlank(cardId)));
        checkArgument(equal(toStringNotBlank(attributes.get("_type")), classId));
        return new RestApiCardImpl(classId, toLongOrNull(cardId), attributes);
    }

    @Override
    public List<Card> getCards(String classeId, String queryParams) {
        logger.debug("get cards for classeId = {}", classeId);
        JsonElement response = get(format("classes/%s/cards%s", checkNotBlank(classeId), queryParams)).asJson();
        return parseCardsResponse(response);
    }

    @Override
    public List<Card> getCards(String classeId) {
        return getCards(classeId, "");
    }

    @Override
    public CardQuery queryCards() {
        return new CardQueryImpl();
    }

    private Card responseToCard(JsonElement response) {
        String cardId = trimAndCheckNotBlank(toString(response.getAsJsonObject().getAsJsonObject("data").getAsJsonPrimitive("_id"))),
                type = toStringNotBlank(toString(response.getAsJsonObject().getAsJsonObject("data").getAsJsonPrimitive("_type")));
        Card newCard = new RestApiCardImpl(type, toLongOrNull(cardId), readAttributes(response.getAsJsonObject().getAsJsonObject("data")));
        return newCard;
    }

    private List<Card> parseCardsResponse(JsonElement response) {
        return stream(response.getAsJsonObject().getAsJsonArray("data")).map(JsonElement::getAsJsonObject).map((card) -> {
            return new RestApiCardImpl(toString(card.get("_type")), toLong(card.get("_id")), readAttributes(card));
        }).collect(toList());
    }

    private Map<String, Object> readAttributes(JsonObject jsonObject) {
        Map<String, Object> attributes = map();
        jsonObject.entrySet().forEach((entry) -> {
            attributes.put(entry.getKey(), toString(entry.getValue()));
        });
        return attributes;
    }

    private class CardQueryImpl implements CardQuery {

        private Integer limit, start;
        private CmdbFilter filter;
        private CmdbSorter sort;

        @Override
        public CardQuery limit(@Nullable Integer limit) {
            this.limit = limit;
            return this;
        }

        @Override
        public CardQuery offset(@Nullable Integer start) {
            this.start = start;
            return this;
        }

        @Override
        public CardQuery filter(@Nullable CmdbFilter filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public CardQuery sort(@Nullable CmdbSorter sort) {
            this.sort = sort;
            return this;
        }

        @Override
        public List<Card> getCards() {
            logger.debug("get cards for query");
            JsonElement response = get(format("cql?filter=%s&sort=%s&limit=%s&start=%s",
                    encodeUrlQuery(filter == null ? "" : CmFilterUtils.serializeFilter(filter)),
                    encodeUrlQuery(sort == null ? "" : CmSorterUtils.serializeSorter(sort)),
                    firstNonNull(limit, ""),
                    firstNonNull(start, ""))).asJson();
            return parseCardsResponse(response);
        }

    }

}
