/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Streams.stream;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.client.rest.api.RelationApi;
import org.cmdbuild.client.rest.model.RelationInfo;
import org.cmdbuild.common.beans.CardIdAndClassName;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

public class RelationApiImpl extends AbstractServiceClientImpl implements RelationApi {

    public RelationApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public RelationInfo createRelation(String domain, CardIdAndClassName source, CardIdAndClassName destination, Map<String, Object> data) {
        return toRelationInfo(post(format("domains/%s/relations", encodeUrlPath(checkNotBlank(domain))), map(data).with(
                "_type", domain,
                "_sourceType", source.getClassName(),
                "_sourceId", source.getId(),
                "_destinationType", destination.getClassName(),
                "_destinationId", destination.getId()
        )).asJackson().get("data"));
    }

    @Override
    public List<RelationInfo> getRelationsForCard(CardIdAndClassName card) {
        return stream(get(format("classes/%s/cards/%s/relations", encodeUrlPath(card.getClassName()), checkNotNullAndGtZero(card.getId()))).asJackson().get("data").elements()).map(this::toRelationInfo).collect(toList());
    }

    private RelationInfo toRelationInfo(JsonNode data) {
        return new RelationInfoImpl(
                data.get("_type").asText(),
                card(data.get("_sourceType").asText(), data.get("_sourceId").asLong()),
                card(data.get("_destinationType").asText(), data.get("_destinationId").asLong()),
                parseEnum(data.get("_direction").asText(), RelationDirection.class),
                data.get("_can_update").asBoolean());
    }

    private static class RelationInfoImpl implements RelationInfo {

        private final String domain;
        private final CardIdAndClassName source, destination;
        private final RelationDirection direction;
        private final boolean canUpdate;

        public RelationInfoImpl(String domain, CardIdAndClassName source, CardIdAndClassName destination, RelationDirection direction, boolean canUpdate) {
            this.domain = checkNotBlank(domain);
            this.source = checkNotNull(source);
            this.destination = checkNotNull(destination);
            this.direction = checkNotNull(direction);
            this.canUpdate = canUpdate;
        }

        @Override
        public String getDomain() {
            return domain;
        }

        @Override
        public CardIdAndClassName getSource() {
            return source;
        }

        @Override
        public CardIdAndClassName getDestination() {
            return destination;
        }

        @Override
        public RelationDirection getDirection() {
            return direction;
        }

        @Override
        public boolean canUpdate() {
            return canUpdate;
        }

    }

}
