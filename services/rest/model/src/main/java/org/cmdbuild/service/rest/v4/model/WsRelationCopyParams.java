/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import static org.cmdbuild.utils.lang.CmCollectionUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;

/**
 * @author ldare
 */
public class WsRelationCopyParams {

    private final List<WsDomainAndDirectionInfo> domains;

    private final long sourceCardId, destinationCardId;

    public WsRelationCopyParams(
            @JsonProperty("domains") List<WsDomainAndDirectionInfo> domains,
            @JsonProperty("source") Long sourceCardId,
            @JsonProperty("destination") Long destinationCardId) {
        this.domains = nullToEmpty(domains);
        this.sourceCardId = checkNotNullAndGtZero(sourceCardId);
        this.destinationCardId = checkNotNullAndGtZero(destinationCardId);
    }

    public List<WsDomainAndDirectionInfo> getDomains() {
        return domains;
    }

    public long getSourceCardId() {
        return sourceCardId;
    }

    public long getDestinationCardId() {
        return destinationCardId;
    }
}
