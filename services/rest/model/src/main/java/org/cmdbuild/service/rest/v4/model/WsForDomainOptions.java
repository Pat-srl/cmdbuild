/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import jakarta.ws.rs.QueryParam;
import org.cmdbuild.classe.access.UserCardQueryForDomain;
import org.cmdbuild.classe.access.UserCardQueryForDomainImpl;
import org.cmdbuild.dao.beans.RelationDirection;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;

/**
 * @author ldare
 */
public class WsForDomainOptions {

    private final UserCardQueryForDomain forDomain;

    public WsForDomainOptions(@QueryParam("forDomain_name") String forDomainName, @QueryParam("forDomain_direction") String forDomainDirection, @QueryParam("forDomain_originId") Long forDomainOriginId, @QueryParam("forDomain_all") Boolean forDomainAll) {
        if (isBlank(forDomainName)) {
            forDomain = null;
        } else {
            forDomain = UserCardQueryForDomainImpl.builder()
                    .withDomainName(forDomainName)
                    .withDirection(parseEnumOrNull(forDomainDirection, RelationDirection.class))
                    .withOriginId(forDomainOriginId)
                    .withAll(forDomainAll)
                    .build();
        }
    }

    public UserCardQueryForDomain getForDomain() {
        return forDomain;
    }

}
