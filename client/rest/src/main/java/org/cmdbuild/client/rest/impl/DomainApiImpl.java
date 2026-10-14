/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.impl;

import com.fasterxml.jackson.databind.JsonNode;
import static java.lang.String.format;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.core.AbstractServiceClientImpl;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.cmdbuild.client.rest.api.DomainApi;
import static org.cmdbuild.client.rest.impl.ClasseApiImpl.attrDataToRequest;
import org.cmdbuild.client.rest.model.AttributeRequestData;
import org.cmdbuild.client.rest.model.DomainInfo;
import org.cmdbuild.dao.entrytype.DomainDefinition;
import static org.cmdbuild.dao.utils.DomainUtils.serializeDomainCardinality;

public class DomainApiImpl extends AbstractServiceClientImpl implements DomainApi {

    public DomainApiImpl(RestWsClient restClient) {
        super(restClient);
    }

    @Override
    public DomainInfo create(DomainDefinition domain) {
        JsonNode response = post("administration/domains/", map(
                "name", domain.getName(),
                "source", domain.getSourceClassName(),
                "destination", domain.getTargetClassName(),
                "cardinality", serializeDomainCardinality(domain.getMetadata().getCardinality()),
                "active", domain.getMetadata().isActive()
        //TODO other params
        )).asJackson();
        return new DomainInfoImpl(response.get("data").get("name").asText());
    }

    @Override
    public void createAttr(String domainId, AttributeRequestData data) {
        JsonNode response = post(format("administration/domains/%s/attributes/", encodeUrlPath(checkNotBlank(domainId, "domainId cannot be blank"))), attrDataToRequest(data)).asJackson();
//        AttributeData responseData = responseToAttrData(response);
//        return new ClassApiWithAttrDataImpl(responseData);
    }

    protected static class DomainInfoImpl implements DomainInfo {

        final String name;

        public DomainInfoImpl(String name) {
            this.name = checkNotBlank(name);
        }

        @Override
        public String getName() {
            return name;
        }

    }

}
