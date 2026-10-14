/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;

import java.util.Map;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.ltEqZeroToNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

/**
 * @author ldare
 */
public class WsRelationData {

    private final boolean isDirect;
    private final Long id, sourceId, destinationId;
    private final String sourceType, destinationType, domainType;
    @JsonAnySetter
    private final Map<String, Object> values = map();

    public WsRelationData(
            @JsonProperty("_id") Long id,
            @JsonProperty("_type") String domainType,
            @JsonProperty("_sourceType") String sourceType,
            @JsonProperty("_sourceId") Long sourceId,
            @JsonProperty("_destinationType") String destinationType,
            @JsonProperty("_destinationId") Long destinationId,
            @JsonProperty("_is_direct") Boolean isDirect) {
        this.id = ltEqZeroToNull(id);
        this.domainType = checkNotBlank(domainType, "missing '_type' param");
        this.sourceId = checkNotNullAndGtZero(sourceId, "missing '_sourceId' param");
        this.sourceType = checkNotBlank(sourceType, "missing '_sourceType' param");
        this.destinationId = checkNotNullAndGtZero(destinationId, "missing '_destinationId' param");
        this.destinationType = checkNotBlank(destinationType, "missing '_destinationType' param");
        this.isDirect = firstNonNull(isDirect, true);
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public Long getSourceCardId() {
        return sourceId;
    }

    public String getSourceClassId() {
        return sourceType;
    }

    public Long getDestinationCardId() {
        return destinationId;
    }

    public String getDestinationClassId() {
        return destinationType;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public String getDomainType() {
        return domainType;
    }

    public boolean isDirect() {
        return isDirect;
    }

    public WsRelationData getDataDirect() {
        if (isDirect) {
            return this;
        } else {
            WsRelationData res = new WsRelationData(id, domainType, destinationType, destinationId, sourceType, sourceId, true);
            res.getValues().putAll(values);
            return res;
        }
    }

}