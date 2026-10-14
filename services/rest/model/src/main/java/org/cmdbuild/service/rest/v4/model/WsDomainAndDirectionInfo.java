/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.dao.beans.RelationDirection;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnum;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * @author ldare
 */
public class WsDomainAndDirectionInfo {

    private final String domainId;
    private final RelationDirection direction;

    @JsonCreator
    public WsDomainAndDirectionInfo(@JsonProperty("_id") String id, @JsonProperty("direction") String direction) {
        this(id, parseEnum(direction, RelationDirection.class));
    }

    public WsDomainAndDirectionInfo(String domainId, RelationDirection direction) {
        this.domainId = checkNotBlank(domainId);
        this.direction = checkNotNull(direction);
    }

    public String getDomainId() {
        return domainId;
    }

    public RelationDirection getDirection() {
        return direction;
    }
}
