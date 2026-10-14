package org.cmdbuild.services.rest.test.v4;

import jakarta.activation.DataHandler;
import org.cmdbuild.modeldiff.diff.data.GeneratedDiffData;
import org.cmdbuild.offline.loader.OfflineLoaderService;
import org.cmdbuild.service.rest.v4.command.OfflineDataWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.OfflineDataWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResult;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.A_KNOWN_CODE1;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.A_KNOWN_CODE2;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class OfflineDataWsTest extends WsTestBase {

    private final OfflineDataWs instance;

    private final GeneratedDiffData generatedDiffData;

    public OfflineDataWsTest() {
        OfflineDataWsCommand command = new OfflineDataWsCommand(offlineLoaderService);
        this.instance = new OfflineDataWs(command);

        this.generatedDiffData = new GeneratedDiffData();
    }

    @Test
    public void testLoad() {
        System.out.println("testLoad");

        //act:
        Object resultObject = instance.load(A_KNOWN_CODE1, Collections.emptyMap());

        //assert:
        verify(offlineLoaderService).executeDataFromDataset(A_KNOWN_CODE1, Collections.emptyMap());
        checkResult(A_KNOWN_CODE1, resultObject, "_id");
    }

    @Test
    public void testDiff() {
        System.out.println("testDiff");

        //arrange:
        when(offlineLoaderService.executeDiffFromData(A_KNOWN_CODE1, map(), A_KNOWN_CODE2)).thenReturn("{\"this\": \"is a test\"}");

        //act:
        Object resultObject = instance.diff(A_KNOWN_CODE1, A_KNOWN_CODE2);

        //assert:
        verify(offlineLoaderService).executeDiffFromData(A_KNOWN_CODE1, map(), A_KNOWN_CODE2);
        checkResult(A_KNOWN_CODE1, resultObject, "_id");
    }

    @Test
    public void testMerge() {
        System.out.println("testMerge");

        //arrange:
        List<Map<String, Object>> mapList = list(map("val1", 1), map("val2", 2), map("val3", 3));
        when(offlineLoaderService.executeMergeFromDiff(A_KNOWN_CODE1, generatedDiffData, A_KNOWN_CODE2)).thenReturn(mapList);

        //act:
        Object resultObject = instance.merge(A_KNOWN_CODE1, A_KNOWN_CODE2, generatedDiffData);

        //assert:
        verify(offlineLoaderService).executeMergeFromDiff(A_KNOWN_CODE1, generatedDiffData, A_KNOWN_CODE2);
    }

    @Test
    public void testNotify() {
        System.out.println("testNotify");

        //arrange:
        DataHandler dataHandler = mock(DataHandler.class);
        when(offlineLoaderService.uploadToTempService(dataHandler)).thenReturn("nicest string ever");

        //act:
        Object resultObject = instance.notify(dataHandler, A_KNOWN_CODE1);

        //assert:
        verify(offlineLoaderService).uploadToTempService(dataHandler);
        verify(offlineLoaderService).sendNotificationForDiff(A_KNOWN_CODE1, "nicest string ever");

    }
}

