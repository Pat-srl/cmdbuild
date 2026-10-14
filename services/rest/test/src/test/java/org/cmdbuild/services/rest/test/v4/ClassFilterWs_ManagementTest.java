/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.auth.user.OperationUser;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClass;
import org.cmdbuild.cardfilter.CardFilterAsDefaultForClassImpl;
import org.cmdbuild.cardfilter.StoredFilter;
import org.cmdbuild.service.rest.v4.command.ClassFilterWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ClassFilterWs_Management;
import org.cmdbuild.service.rest.v4.model.WsDefaultStoredFilter;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsFilterData;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author ldare
 */
public class ClassFilterWs_ManagementTest extends WsTestBase {

    private final ClassFilterWs_Management instance;

    private final List<StoredFilter> listFilters;
    private final StoredFilter storedFilterActive;
    private final OperationUser operationUser;

    public ClassFilterWs_ManagementTest() {

        ClassFilterWsCommand command = new ClassFilterWsCommand(cardFilterService, operationUserSupplier);
        instance = new ClassFilterWs_Management(objectTranslationService, command);

        listFilters = mockBuildTwoStoredFilters(A_KNOWN_FILTER_ID1, A_KNOWN_FILTER_ID2);
        storedFilterActive = mockBuildActiveStoredFilter(A_KNOWN_FILTER_ID1);
        operationUser = mockBuildOperationUser();
    }

    @Test
    public void testReadAll_anyClass() {
        System.out.println("readAll_anyClass_Management");

        //arrange:
        List<String> expListNames = list("Active Filter 1");
        when(cardFilterService.readAllSharedFilters()).thenReturn(listFilters);

        //act:
        Object resultObject = instance.readAll(A_KNOWN_ANY_ID, A_TEST_LIMIT, A_TEST_OFFSET, NOT_SHARED);

        //assert:
        verify(cardFilterService).readAllSharedFilters();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_specificClass_sharedOnly() {
        System.out.println("readAll_specificClass_sharedOnly_Management");

        //arrange:
        List<String> expListNames = list("Active Filter 1");
        when(cardFilterService.readSharedForCurrentUser(A_KNOWN_CLASS_ID)).thenReturn(listFilters);

        //act:
        Object resultObject = instance.readAll(A_KNOWN_CLASS_ID, A_TEST_LIMIT, A_TEST_OFFSET, SHARED);

        //assert:
        verify(cardFilterService).readSharedForCurrentUser(A_KNOWN_CLASS_ID);
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testReadAll_specificClass_all() {
        System.out.println("readAll_specificClass_all_Management");

        //arrange:
        List<String> expListNames = list("Active Filter 1");
        when(cardFilterService.readAllForCurrentUser(A_KNOWN_CLASS_ID)).thenReturn(listFilters);

        //act:
        Object resultObject = instance.readAll(A_KNOWN_CLASS_ID, A_TEST_LIMIT, A_TEST_OFFSET, NOT_SHARED);

        //assert:
        verify(cardFilterService).readAllForCurrentUser(A_KNOWN_CLASS_ID);
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testRead() {
        System.out.println("read");

        //arrange:
        String expName = "Active Filter 1";
        when(cardFilterService.getById(A_KNOWN_FILTER_ID1)).thenReturn(storedFilterActive);

        //act:
        Object resultObject = instance.read(A_KNOWN_CLASS_ID, A_KNOWN_FILTER_ID1);

        //assert:
        verify(cardFilterService).getById(A_KNOWN_FILTER_ID1);
        checkName(expName, resultObject);
    }

    @Test
    public void testCreate() {
        System.out.println("create_Management");

        //arrange:
        String expName = "Active Filter 1";
        when(operationUserSupplier.getUser()).thenReturn(operationUser);
        when(cardFilterService.create(any())).thenReturn(storedFilterActive);

        //act:
        Object resultObject = instance.create(A_KNOWN_ANY_ID, mockBuildWsFilterData());

        //assert:
        verify(operationUserSupplier).getUser();
        verify(cardFilterService).create(any());
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        String expName = "Active Filter 1";
        when(operationUserSupplier.getUser()).thenReturn(operationUser);
        when(cardFilterService.update(any())).thenReturn(storedFilterActive);

        //act:
        Object resultObject = instance.update(A_KNOWN_CLASS_ID, A_KNOWN_FILTER_ID2, mockBuildWsFilterData());

        //assert:
        verify(operationUserSupplier).getUser();
        verify(cardFilterService).update(any());
        checkName(expName, resultObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_CLASS_ID, A_KNOWN_FILTER_ID1);

        //assert:
        verify(cardFilterService).delete(A_KNOWN_FILTER_ID1);
        checkSuccess(resultObject);
    }

    @Test
    public void testGetDefaultForRoles() {
        System.out.println("getDefaultForRoles");

        //arrange:
        List<Long> expListIds = list(777L);
        CardFilterAsDefaultForClass defaultForFilter = mockBuildCardFilterAsDefaultForClass();
        when(cardFilterService.getDefaultFiltersForFilter(A_KNOWN_FILTER_ID1)).thenReturn(list(defaultForFilter));

        //act:
        Object resultObject = instance.getDefaultForRoles(A_KNOWN_FILTER_ID1);

        //assert:
        verify(cardFilterService).getDefaultFiltersForFilter(anyLong());
        checkListIds(expListIds, resultObject);
    }

    @Test
    public void testUpdateWithPost() {
        System.out.println("updateWithPost");

        //arrange:
        List<Long> expListIds = list(777L);
        WsDefaultStoredFilter wsDefaultStoredFilter = new WsDefaultStoredFilter(777L);
        List<WsDefaultStoredFilter> roles = list(wsDefaultStoredFilter);
        when(cardFilterService.getById(A_KNOWN_FILTER_ID1)).thenReturn(storedFilterActive);
        CardFilterAsDefaultForClassImpl cardFilter
                = new CardFilterAsDefaultForClassImpl(storedFilterActive, storedFilterActive.getOwnerName(), wsDefaultStoredFilter.getId());
        when(cardFilterService.getDefaultFiltersForFilter(A_KNOWN_FILTER_ID1)).thenReturn(singletonList(cardFilter));

        //act:
        Object resultObject = instance.updateWithPost(A_KNOWN_FILTER_ID1, roles);

        //assert:
        verify(cardFilterService).getById(A_KNOWN_FILTER_ID1);
        verify(cardFilterService).getDefaultFiltersForFilter(A_KNOWN_FILTER_ID1);
        verify(cardFilterService).setDefaultFiltersForFilterWithMatchingClass(eq(A_KNOWN_FILTER_ID1), anyList());
        checkListIds(expListIds, resultObject);
    }
}
