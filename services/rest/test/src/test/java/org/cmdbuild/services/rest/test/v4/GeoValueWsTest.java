package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.gis.*;
import org.cmdbuild.service.rest.v4.command.GeoValueWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.GeoValueWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultStructure;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildGisNavTreeNodeList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildGisValueList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GeoValueWsTest extends WsTestBase {

    private final Set<Long> set = set(1L, 2L, 3L);

    private final GeoValueWs instance;

    private final List<GisValue> gisValueList;
    private final List<GisNavTreeNode> gisNavTreeNodeList;
    private final GisValuesAndNavTree gisValuesAndNavTree;
    private final Area area;

    public GeoValueWsTest() {
        GeoValueWsCommand command = new GeoValueWsCommand(gisService);
        instance = new GeoValueWs(command);

        this.gisValueList = mockBuildGisValueList();
        this.gisNavTreeNodeList = mockBuildGisNavTreeNodeList();
        this.area = new AreaImpl(0.0, 0.0, 1.0, 1.0);
        this.gisValuesAndNavTree = new GisValuesAndNavTreeImpl(gisValueList, gisNavTreeNodeList);
    }

    @Test
    public void testQuery_attachNavTreeFalse() {
        System.out.println("testQuery_attachNavTreeFalse");

        //arrange:
        when(gisService.getGisValues(eq(set(1L, 2L, 3L)), eq("area"), any(CmdbFilter.class), eq("owner"))).thenReturn(gisValueList);

        //act:
        Object resultObject = instance.query(set(1L, 2L, 3L), "area", false, A_TEST_EMPTY_FILTER, "owner");

        //assert:
        verify(gisService).getGisValues(eq(set(1L, 2L, 3L)), eq("area"), any(CmdbFilter.class), eq("owner"));
        checkResultList(list(A_KNOWN_NAME1, A_KNOWN_NAME2, A_KNOWN_NAME3), resultObject, "_attr");
    }

    @Test
    public void testQuery_attachNavTreeTrue() {
        System.out.println("testQuery_attachNavTreeTrue");

        //arrange:
        when(gisService.getGisValuesAndNavTree(eq(set), eq("area"), any(CmdbFilter.class), eq("owner"))).thenReturn(gisValuesAndNavTree);

        //act:
        Object resultObject = instance.query(set, "area", true, A_TEST_EMPTY_FILTER, "owner");

        //assert:
        verify(gisService).getGisValuesAndNavTree(eq(set), eq("area"), any(CmdbFilter.class), eq("owner"));
        checkResultList(list(A_KNOWN_NAME1, A_KNOWN_NAME2, A_KNOWN_NAME3), resultObject, "_attr");
    }

    @Test
    public void testQueryArea() {
        System.out.println("testQueryArea");

        //arrange:
        Map<String, Object> expMap = map(
                "x1", null,
                "y1", null,
                "x2", null,
                "y2", null
        );
        when(gisService.getAreaForValues(eq(set), any(CmdbFilter.class), eq("owner"))).thenReturn(area);

        //act:
        Object resultObject = instance.queryArea(set, A_TEST_EMPTY_FILTER, "owner");

        //assert:
        verify(gisService).getAreaForValues(eq(set), any(CmdbFilter.class), eq("owner"));
        checkResultStructure(expMap,(Map<String, Object>) ((Map<String, Object>) resultObject).get("data"));
    }

    @Test
    public void testQueryCenter() {
        System.out.println("testQueryCenter");

        //arrange:
        Map<String, Object> expMap = map(
                "x", null,
                "y", null
        );
        when(gisService.getAreaForValues(eq(set), any(CmdbFilter.class
        ), eq("owner"))).thenReturn(area);

        //act:
        Object resultObject = instance.queryCenter(set, A_TEST_EMPTY_FILTER, "owner");

        //assert:
        verify(gisService).getAreaForValues(eq(set), any(CmdbFilter.class
        ), eq("owner"));
        checkResultStructure(expMap,(Map<String, Object>) ((Map<String, Object>) resultObject).get("data"));

    }
}

