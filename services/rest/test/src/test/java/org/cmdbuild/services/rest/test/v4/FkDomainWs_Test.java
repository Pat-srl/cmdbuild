/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.dao.entrytype.FkDomain;
import org.cmdbuild.service.rest.v4.command.FkDomainWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.FkDomainWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkListIds;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildFkDomain;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class FkDomainWs_Test extends WsTestBase {

    private final FkDomainWs instance;

    private final List<String> A_KNOWN_ID_LIST;

    public FkDomainWs_Test() {
        FkDomainWsCommand command = new FkDomainWsCommand(fkDomainRepository, daoService);
        instance = new FkDomainWs(userClassService, objectTranslationService, command);
        A_KNOWN_ID_LIST = list("exampClasseName_exampAttributeName");
    }

    @Test
    public void testReadAll() {
        System.out.println("readAll");

        //arrange:
        List<FkDomain> list = list(mockBuildFkDomain());
        when(fkDomainRepository.getAllFkDomains()).thenReturn(list);

        //act:
        Object resultObject = instance.readAll(A_TEST_EMPTY_FILTER, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(fkDomainRepository).getAllFkDomains();
        checkListIds(A_KNOWN_ID_LIST, resultObject);
    }
}
