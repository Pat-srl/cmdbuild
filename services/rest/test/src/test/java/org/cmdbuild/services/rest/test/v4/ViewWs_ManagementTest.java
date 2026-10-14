/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.AttributeWithoutOwner;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.report.ReportFormat;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.ContextMenuSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.ViewSerializer;
import org.cmdbuild.service.rest.v4.command.ViewWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.ViewWs_Management;
import org.cmdbuild.service.rest.v4.model.WsViewData;
import org.cmdbuild.services.rest.test.common.TestHelper_WSData;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.cmdbuild.view.View;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.emptyList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class ViewWs_ManagementTest extends WsTestBase {

    private final ViewWs_Management instance;

    private final WsViewData viewData1;
    private final WsViewData viewData2;
    private final View view1;
    private final View view2;
    private final List<View> listView;
    private final Attribute attr1;
    private final Attribute attr2;
    private final Attribute attr3;
    private final Classe classe;
    private final List<Attribute> expListAttr;

    public ViewWs_ManagementTest() {
        ContextMenuSerializationHelper contextMenuSerializationHelper = mockBuildContextMenuSerializationHelper();
        ViewSerializer viewSerializer = mockBuildViewSerializer();
        AttributeTypeConversionService attributeTypeConversionService = mockBuildAttributeTypeConversionService();

        ViewWsCommand command = new ViewWsCommand(operationUserSupplier, viewService, formStructureService, contextMenuService, contextMenuSerializationHelper, sysReportService);
        instance = new ViewWs_Management(viewService, viewSerializer, attributeTypeConversionService, command);

        viewData1 = TestHelper_WSData.mockBuildWsViewData(A_KNOWN_WSVIEWDATA_NAME1);
        viewData2 = TestHelper_WSData.mockBuildWsViewData(A_KNOWN_WSVIEWDATA_NAME2);
        view1 = mockBuildView(A_KNOWN_VIEW_NAME1);
        view2 = mockBuildView(A_KNOWN_VIEW_NAME2);
        listView = list(view1, view2);

        List<AttributeWithoutOwner> listAttributesWO = list(
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME1),
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME2),
                mockBuildAttributeWithoutOwner(A_KNOWN_ATTR_NAME3)
        );
        classe = mockBuildClasse(A_KNOWN_CLASS_ID, emptyList(), listAttributesWO);
        attr1 = mockBuildAttribute(A_KNOWN_ATTR_NAME1, classe);
        attr2 = mockBuildAttribute(A_KNOWN_ATTR_NAME2, classe);
        attr3 = mockBuildAttribute(A_KNOWN_ATTR_NAME3, classe);
        expListAttr = list(attr1, attr2, attr3);
    }

    @Test
    public void testGetMany() {
        System.out.println("getMany");

        //arrange:
        List<String> expListNames = list(A_KNOWN_VIEW_NAME1, A_KNOWN_VIEW_NAME2);
        when(viewService.getActiveViewsForCurrentUser()).thenReturn(listView);

        //act:
        Object resultObject = instance.getMany(A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, DETAILED, SHARED);

        //assert:
        verify(viewService).getActiveViewsForCurrentUser();
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testGetOne() {
        System.out.println("getOne");

        //arrange:
        String expName = A_KNOWN_VIEW_NAME1;
        when(viewService.getForCurrentUserByNameOrId(A_KNOWN_VIEW_ID)).thenReturn(view1);

        //act:
        Object resultObject = instance.getOne(A_KNOWN_VIEW_ID);

        //assert:
        verify(viewService).getForCurrentUserByNameOrId(A_KNOWN_VIEW_ID);
        checkName(expName, resultObject);
    }

    @Test
    public void testGetAttributes() {
        System.out.println("getAttributes");

        //arrange:
        List<String> expListNames = list(A_KNOWN_ATTR_NAME1, A_KNOWN_ATTR_NAME2, A_KNOWN_ATTR_NAME3);
        when(viewService.getForCurrentUserByNameOrId(A_KNOWN_VIEW_ID)).thenReturn(view1);
        when(viewService.getAttributesForView(view1)).thenReturn(expListAttr);

        //act:
        Object resultObject = instance.getAttributes(A_KNOWN_VIEW_ID);

        //assert:
        verify(viewService).getForCurrentUserByNameOrId(A_KNOWN_VIEW_ID);
        verify(viewService).getAttributesForView(view1);
        checkListNames(expListNames, resultObject);
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        String expName = A_KNOWN_VIEW_NAME1;
        when(viewService.createForCurrentUser(any(View.class))).thenReturn(view1);

        //act:
        Object resultObject = instance.create(viewData1);

        //assert:
        verify(viewService).createForCurrentUser(any(View.class));
        verify(formStructureService).setFormForView(view1, viewData1.getFormStructure());
        verify(contextMenuService).updateContextMenuItemsForView(eq(view1), any());
        checkName(expName, resultObject);
    }

    @Test
    public void testUpdate() {
        System.out.println("update");

        //arrange:
        String expName = A_KNOWN_VIEW_NAME2;
        // returning different ViewImpl to simulate update
        when(viewService.updateForCurrentUser(any(View.class))).thenReturn(view2);

        //act:
        Object resultObject = instance.update(A_KNOWN_VIEW_ID, viewData2);

        //arrange:
        verify(viewService).updateForCurrentUser(any(View.class));
        verify(formStructureService).setFormForView(view2, viewData2.getFormStructure());
        verify(contextMenuService).updateContextMenuItemsForView(eq(view2), any());
        checkName(expName, resultObject);
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //arrange:
        when(viewService.getForCurrentUserByNameOrId(A_KNOWN_VIEW_ID)).thenReturn(view1);

        //act:
        Object resultObject = instance.delete(A_KNOWN_VIEW_ID);

        //assert:
        verify(viewService).getForCurrentUserByNameOrId(A_KNOWN_VIEW_ID);
        verify(viewService).delete(view1.getId());
        checkSuccess(resultObject);
    }

    @Test
    public void testPrintView() {
        System.out.println("printView");

        //arrange:
        when(viewService.getForCurrentUserByNameOrId(A_KNOWN_VIEW_ID)).thenReturn(view1);
        // following line mocks the return statement since it's a service
        // since we are mocking the return, we don't perform checks on returned value
        // only check that correct methods are called by entrypoint
        when(sysReportService.executeViewReport(eq(view1), eq(ReportFormat.ODT), any(DaoQueryOptions.class))).thenReturn(mock(DataHandler.class));

        //act:
        instance.printView(A_KNOWN_VIEW_ID, A_TEST_EMPTY_FILTER, A_TEST_SORT, A_TEST_LONG_LIMIT, A_TEST_LONG_OFFSET, "ODT", "");

        //assert:
        verify(viewService).getForCurrentUserByNameOrId(A_KNOWN_VIEW_ID);
        verify(sysReportService).executeViewReport(eq(view1), eq(ReportFormat.ODT), any(DaoQueryOptions.class));
    }
}
