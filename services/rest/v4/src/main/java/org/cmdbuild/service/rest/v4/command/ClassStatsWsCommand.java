/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.classe.access.UserCardService;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.driver.postgres.q3.stats.DaoStatsQueryOptionsUtils;
import org.cmdbuild.dao.driver.postgres.q3.stats.StatsQueryResponse;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class ClassStatsWsCommand {

    private final UserDomainService userDomainService;
    private final UserCardService userCardService;

    public ClassStatsWsCommand(UserDomainService userDomainService, UserCardService userCardService) {
        this.userDomainService = checkNotNull(userDomainService);
        this.userCardService = checkNotNull(userCardService);
    }

    public StatsQueryResponse doStats(String classId, WsQueryOptions wsQueryOptions, String select) {
        return userCardService.getStats(classId, wsQueryOptions.getQuery(), DaoStatsQueryOptionsUtils.statsQueryOptionsFromJson(select));
    }

    public List<UserDomainService.CardDomainRelationStats> doRelations(String classId, WsQueryOptions wsQueryOptions) {
        return userDomainService.getRelationsStats(classId, wsQueryOptions.getQuery().getFilter());
    }
}
