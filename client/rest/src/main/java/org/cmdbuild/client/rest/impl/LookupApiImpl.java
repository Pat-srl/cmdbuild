/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Streams;
import com.google.common.net.UrlEscapers;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.client.rest.api.LookupApi;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.utils.lang.CmConvertUtils;

public class LookupApiImpl extends AbstractServiceClientImpl implements LookupApi {

    public LookupApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public List<LookupValue> getValues(String lookupTypeId) {
        checkNotBlank(lookupTypeId);
        logger.debug("get lookup values for type = {}", lookupTypeId);
        JsonNode response = get("lookup_types/" + UrlEscapers.urlPathSegmentEscaper().escape(lookupTypeId) + "/values").asJackson();
        return Streams.stream(response.get("data")).map((record) -> fromJson(record, SimpleLookupValue.class)).collect(toList());
    }

    @JsonDeserialize(builder = SimpleLookupValueBuilder.class)
    public static class SimpleLookupValue implements LookupValue {

        private final String code, description, type;
        private final long id;

        public SimpleLookupValue(String id, String code, String description, String type) {
            this.code = checkNotNull(code, "code cannot be null");
            this.description = description;
            this.type = checkNotBlank(type, "type cannot be null");
            this.id = CmConvertUtils.toLong(checkNotBlank(id, "id cannot be null"));
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public long getId() {
            return id;
        }

    }

    public static class SimpleLookupValueBuilder implements Builder<SimpleLookupValue, SimpleLookupValueBuilder> {

        private String code, description, type, id;

        @JsonProperty("_id")
        public void withId(String id) {
            this.id = id;
        }

        public void withCode(String code) {
            this.code = code;
        }

        public void withDescription(String description) {
            this.description = description;
        }

        @JsonProperty("_type")
        public void withType(String type) {
            this.type = type;
        }

        @Override
        public SimpleLookupValue build() {
            return new SimpleLookupValue(id, code, description, type);
        }

    }

}
