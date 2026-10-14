/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import org.cmdbuild.etl.config.WaterwayDescriptorMeta;
import org.cmdbuild.etl.config.WaterwayItem;
import org.cmdbuild.etl.config.inner.WaterwayDescriptorRecord;
import org.cmdbuild.service.rest.common.beans.WsQueryOptions;
import org.cmdbuild.service.rest.v4.command.EtlConfigWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.EtlConfigWs;
import org.cmdbuild.service.rest.v4.model.WsConfigMeta;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsConfigMeta;
import static org.cmdbuild.services.rest.test.common.TestHelper_WSData.mockBuildWsQueryOptions;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class EtlConfigWsTest extends WsTestBase {

    private final EtlConfigWs instance;

    private final WsQueryOptions wsQueryOptionsDetailed;
    private final WsQueryOptions wsQueryOptionsNotDetailed;
    private final List<WaterwayDescriptorRecord> waterwayDescriptorRecordList;
    private final List<WaterwayItem> waterwayItemList;
    private final DataHandler dataHandler;
    private final WsConfigMeta wsConfigMeta;

    public EtlConfigWsTest() {
        EtlConfigWsCommand command = new EtlConfigWsCommand(waterwayDescriptorService);
        this.instance = new EtlConfigWs(waterwayDescriptorService, command);

        this.wsQueryOptionsDetailed = mockBuildWsQueryOptions(DETAILED);
        this.wsQueryOptionsNotDetailed = mockBuildWsQueryOptions(NOT_DETAILED);
        this.waterwayDescriptorRecordList = mockBuildWaterwayDescriptorRecordList();
        this.waterwayItemList = mockBuildWaterWayItemList();
        this.dataHandler = mockBuildDataHandler(A_TEST_DATAHANDLER_TYPE);
        this.wsConfigMeta = mockBuildWsConfigMeta(A_KNOWN_CODE1);
    }

    @Test
    public void testReadAll_detailed() {
        System.out.println("readAll");

        //arrange:
        when(waterwayDescriptorService.getAllDescriptors()).thenReturn(waterwayDescriptorRecordList);

        //act:
        Object resultObject = instance.readAll(wsQueryOptionsDetailed, INCLUDE);

        //assert:
        verify(waterwayDescriptorService).getAllDescriptors();
        checkResultList(list(A_KNOWN_CODE1, A_KNOWN_CODE2, A_KNOWN_CODE3), resultObject, "code");
        checkDetailedList(resultObject, "data");
    }

    @Test
    public void testReadAll_notDetailed() {
        System.out.println("readAll");

        //arrange:
        when(waterwayDescriptorService.getAllDescriptors()).thenReturn(waterwayDescriptorRecordList);

        //act:
        Object resultObject = instance.readAll(wsQueryOptionsNotDetailed, INCLUDE);

        //assert:
        verify(waterwayDescriptorService).getAllDescriptors();
        checkResultList(list(A_KNOWN_CODE1, A_KNOWN_CODE2, A_KNOWN_CODE3), resultObject, "code");
        checkNotDetailedList(resultObject, "data");
    }

    @Test
    public void testRead_check() {
        System.out.println("read_check");

        //arrange:
        when(waterwayDescriptorService.getDescriptorOrNull(A_KNOWN_CODE1)).thenReturn(waterwayDescriptorRecordList.get(0));

        //act:
        Object resultObject = instance.read(A_KNOWN_CODE1, INCLUDE, true);

        //assert:
        verify(waterwayDescriptorService).getDescriptorOrNull(A_KNOWN_CODE1);
        checkResult(A_KNOWN_CODE1, resultObject, "code");
    }

    @Test
    public void testRead_dontCheck() {
        System.out.println("read_dontCheck");

        //arrange:
        when(waterwayDescriptorService.getDescriptor(A_KNOWN_CODE1)).thenReturn(waterwayDescriptorRecordList.get(0));

        //act:
        Object resultObject = instance.read(A_KNOWN_CODE1, INCLUDE, false);

        //assert:
        verify(waterwayDescriptorService).getDescriptor(A_KNOWN_CODE1);
        checkResult(A_KNOWN_CODE1, resultObject, "code");
    }

    @Test
    public void testRead_nullConfigFile() {
        System.out.println("read_nullConfigFile");

        //arrange:

        //act:
        Object resultObject = instance.read(A_KNOWN_CODE1, INCLUDE, true);

        //assert:
        verify(waterwayDescriptorService).getDescriptorOrNull(A_KNOWN_CODE1);
        checkResult(false, resultObject, "exists");
    }

    @Test
    public void testReadItems() {
        System.out.println("readItems");

        //arrange:
        when(waterwayDescriptorService.getAllItems()).thenReturn(waterwayItemList);

        //act:
        Object resultObject = instance.readItems(wsQueryOptionsDetailed, A_KNOWN_CODE1);

        //assert:
        verify(waterwayDescriptorService).getAllItems();
    }

    @Test
    public void testCreate() {
        System.out.println("create");

        //arrange:
        when(waterwayDescriptorService.createUpdateDescriptor(any(DataSource.class), any(WaterwayDescriptorMeta.class), anyBoolean())).thenReturn(waterwayDescriptorRecordList.get(0));

        //act:
        Object resultObject = instance.create(dataHandler, wsConfigMeta, true);

        //assert:
        verify(waterwayDescriptorService).createUpdateDescriptor(any(DataSource.class), any(WaterwayDescriptorMeta.class), anyBoolean());
        checkResult(A_KNOWN_CODE1, resultObject, "code");
    }

    @Test
    public void testUpdate_dataNotNull() {
        System.out.println("update_dataNotNull");

        //arrange:
        when(waterwayDescriptorService.createUpdateDescriptor(any(DataSource.class), any(WaterwayDescriptorMeta.class))).thenReturn(waterwayDescriptorRecordList.get(0));

        //act:
        Object resultObject = instance.update(A_KNOWN_CODE1, dataHandler, wsConfigMeta);

        //assert:
        verify(waterwayDescriptorService).createUpdateDescriptor(any(DataSource.class), any(WaterwayDescriptorMeta.class));
        checkResult(A_KNOWN_CODE1, resultObject, "code");
    }

    @Test
    public void testUpdate_dataNull() {
        System.out.println("update_dataNull");

        //arrange:
        when(waterwayDescriptorService.getDescriptor(A_KNOWN_CODE1)).thenReturn(waterwayDescriptorRecordList.get(0));

        //act:
        Object resultObject = instance.update(A_KNOWN_CODE1, null, wsConfigMeta);

        //assert:
        verify(waterwayDescriptorService).updateDescriptorMeta(anyString(), any(WaterwayDescriptorMeta.class));
        verify(waterwayDescriptorService).getDescriptor(A_KNOWN_CODE1);
        checkResult(A_KNOWN_CODE1, resultObject, "code");
    }

    @Test
    public void testDelete() {
        System.out.println("delete");

        //act:
        Object resultObject = instance.delete(A_KNOWN_CODE1);

        //assert:
        verify(waterwayDescriptorService).deleteDescriptor(A_KNOWN_CODE1);
        checkSuccess(resultObject);
    }
}
