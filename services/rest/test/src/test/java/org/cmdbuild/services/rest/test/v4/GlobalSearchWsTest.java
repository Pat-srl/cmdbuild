/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.*;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.dao.postgres.utils.MyJdbcTemplate;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.GlobalSearchWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.GlobalSearchWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.utils.lang.CmMapUtils;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.cmdbuild.common.Constants.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptions;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptionsWithFilter;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class GlobalSearchWsTest extends WsTestBase {

    private final GlobalSearchWs instance;

    private final WsQueryOptions wsQueryOptions;
    private final List<Domain> domainList;
    private final List<Classe> classeList;
    private final JdbcTemplate jdbcTemplate;
    private final QueryBuilder queryBuilder;
    private final List<Card> cardList;

    public GlobalSearchWsTest() {
        GlobalSearchWsCommand command = new GlobalSearchWsCommand(daoService, userClassService, userDomainService);
        this.instance = new GlobalSearchWs(command);

        this.wsQueryOptions = mockBuildWsQueryOptionsWithFilter(DETAILED);
        this.domainList = mockBuildDomainsList();
        this.classeList = list(
                mockBuildClasse(A_KNOWN_CLASS_NAME1)
        );
        this.jdbcTemplate = mock(JdbcTemplate.class);
        this.queryBuilder = mock(QueryBuilder.class);
        Classe classe = mockBuildClasse(A_KNOWN_CLASS_NAME1);
        this.cardList = list(mockBuildCard(A_KNOWN_CARD_ID1, classe, map("attr1", 1)));
    }

    @Test
    public void testGlobalSearch() {
        System.out.println("testGlobalSearch");

        //arrange:
        when(userDomainService.getActiveUserDomains()).thenReturn(domainList);
        when(userClassService.getActiveUserClasses()).thenReturn(classeList);
        when(daoService.getJdbcTemplate()).thenReturn(jdbcTemplate);
        when(jdbcTemplate.queryForList(anyString(), any(Class.class)))
                .thenReturn(Collections.singletonList(CmMapUtils.FluentMap.class));
        when(userClassService.getAllUserClasses()).thenReturn(classeList);
        when(daoService.select(anyCollection())).thenReturn(queryBuilder);
        when(queryBuilder.from(any(Classe.class))).thenReturn(queryBuilder);
        when(queryBuilder.whereExpr(any())).thenReturn(queryBuilder);
        when(queryBuilder.getCards()).thenReturn(cardList);

        //act:
        Object resultObject = instance.globalSearch(wsQueryOptions);

        //assert:
        verify(userDomainService).getActiveUserDomains();
        verify(userClassService, times(4)).getActiveUserClasses();
        verify(userClassService).getUserClassOrNull(LOOKUP_CLASS_NAME);
        verify(userClassService).getUserClassOrNull(USER_CLASS_NAME);
        verify(userClassService).getUserClassOrNull(ROLE_CLASS_NAME);
        verify(daoService).getClasse(BASE_CLASS_NAME);
        verify(daoService).getJdbcTemplate();
        verify(userClassService).getAllUserClasses();
        verify(daoService).select(anyCollection());
        checkResultList(list("class1"), resultObject, "_type");
    }
}

