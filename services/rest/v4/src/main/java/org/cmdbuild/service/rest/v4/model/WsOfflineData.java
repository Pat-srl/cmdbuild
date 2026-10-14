/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.cmdbuild.modeldiff.dataset.data.DataDataset;
import org.cmdbuild.offline.OfflineDataImpl;

import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

/**
 * @author ldare
 */
public class WsOfflineData extends DataDataset {

    public final Boolean isActive;
    public final String masterClass;

    protected WsOfflineData(
            @JsonProperty("active") Boolean isActive,
            @JsonProperty("masterClass") String masterClass
    ) {
        this.isActive = firstNotNull(isActive, true);
        this.masterClass = masterClass;
    }

    public OfflineDataImpl.OfflineDataImplBuilder toOfflineData() {
        ObjectNode metadataNode = (new ObjectMapper()).createObjectNode();
        metadataNode.putPOJO("classes", super.classes);
        metadataNode.putPOJO("processes", super.processes);
        metadataNode.putPOJO("views", super.views);
        metadataNode.put("masterClass", masterClass);
        return OfflineDataImpl.builder().withCode(super.getName()).withDescription(super.getDescription()).withMetadata(toJson(metadataNode)).withEnabled(isActive);
    }
}
