/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.stats.DaoStatsQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.stats.StatsQueryResponse;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.ClassStatsWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ClassStatsWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkDetailed;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptions;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class ClassStatsWsTest extends WsTestBase {

    private final ClassStatsWs instance;

    private final WsQueryOptions wsQueryOptions;
    private final Classe classe;
    private final StatsQueryResponse statsQueryResponse;
    private final List<UserDomainService.CardDomainRelationStats> cardDomainRelationStatsList;

    private final String select = "{\"aggregate\":[{\"attribute\":\"price\",\"operation\":\"SUM\"},{\"attribute\":\"quantity\",\"operation\":\"SUM\"}]}";

    public ClassStatsWsTest() {
        CardWsSerializationHelperv3 cardWsSerializationHelperv3 = mockBuildCardWsSerializationHelperv3();
        ClassStatsWsCommand command = new ClassStatsWsCommand(userDomainService, userCardService);
        this.instance = new ClassStatsWs(userClassService, cardWsSerializationHelperv3, command);

        this.wsQueryOptions = mockBuildWsQueryOptions(DETAILED);
        this.classe = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME1);
        this.statsQueryResponse = mockBuildStatsQueryResponse();

        Classe sourceClasse1 = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME1);
        Classe targetClasse1 = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME2);
        Domain domainDirect = mockBuildDomainDirect(A_KNOWN_DOMAIN_NAME1, sourceClasse1, targetClasse1);

        Classe sourceClasse2 = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME1);
        Classe targetClasse2 = mockBuildClasseWithAttr(A_KNOWN_CLASS_NAME2);
        Domain domainInverse = mockBuildDomainInverse(A_KNOWN_DOMAIN_NAME2, sourceClasse2, targetClasse2);
        this.cardDomainRelationStatsList = list(
                mockBuildCardDomainRelationStats(A_KNOWN_LONG_ID1, RD_DIRECT, domainDirect),
                mockBuildCardDomainRelationStats(A_KNOWN_LONG_ID2, RD_INVERSE, domainInverse)
        );
    }

    @Test
    public void testStats() {
        System.out.println("stats");

        //arrange:
        when(userCardService.getStats(eq(A_KNOWN_CLASS_ID), any(DaoQueryOptions.class), any(DaoStatsQueryOptions.class))).thenReturn(statsQueryResponse);
        when(userClassService.getUserClass(A_KNOWN_CLASS_ID)).thenReturn(classe);

        //act:
        Object resultObject = instance.stats(A_KNOWN_CLASS_ID, wsQueryOptions, select);

        //assert:
        verify(userCardService).getStats(eq(A_KNOWN_CLASS_ID), any(DaoQueryOptions.class), any(DaoStatsQueryOptions.class));
        verify(userClassService).getUserClass(A_KNOWN_CLASS_ID);
        checkDetailed(resultObject, "aggregate");
    }

    @Test
    public void testRelations() {
        System.out.println("relations");

        //arrange:
        when(userDomainService.getRelationsStats(eq(A_KNOWN_CLASS_ID), any(CmdbFilter.class))).thenReturn(cardDomainRelationStatsList);

        //act:
        Object resultObject = instance.relations(A_KNOWN_CLASS_ID, wsQueryOptions);

        //assert:
        verify(userDomainService).getRelationsStats(eq(A_KNOWN_CLASS_ID), any(CmdbFilter.class));
        checkResultList(list(A_KNOWN_DOMAIN_NAME1, A_KNOWN_DOMAIN_NAME2), resultObject, "domain");
    }
}
