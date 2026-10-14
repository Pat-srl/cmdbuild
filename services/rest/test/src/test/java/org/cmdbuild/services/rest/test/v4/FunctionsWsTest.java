/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.v4;


import org.cmdbuild.common.utils.PagedElements;
import org.cmdbuild.dao.core.q3.PreparedQuery;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.attributetype.BooleanAttributeType;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.service.rest.common.serializationhelpers.AttributeTypeConversionService;
import org.cmdbuild.service.rest.common.serializationhelpers.card.CardWsSerializationHelperv3;
import org.cmdbuild.service.rest.v4.command.FunctionsWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.FunctionsWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Test;

import javax.xml.transform.Result;
import java.util.List;
import java.util.Map;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.*;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ldare
 */
public class FunctionsWsTest extends WsTestBase {

    private final FunctionsWs instance;

    private final PagedElements<StoredFunction> storedFunctionPagedElements;
    private final List<Attribute> attributeList;
    private final BooleanAttributeType booleanAttributeType;
    private final PreparedQuery preparedQuery;
    private final Map<String, Object> expCallMap;

    public FunctionsWsTest() {
        CardWsSerializationHelperv3 cardWsSerializationHelperv3 = mockBuildCardWsSerializationHelperv3();
        AttributeTypeConversionService attributeTypeConversionService = mockBuildAttributeTypeConversionService();
        FunctionsWsCommand command = new FunctionsWsCommand(daoService);
        this.instance = new FunctionsWs(cardWsSerializationHelperv3, attributeTypeConversionService, command);

        this.storedFunctionPagedElements = mockBuildStoredFunctionPagedElements();
        Classe classe = mockBuildClasse(A_KNOWN_CLASS_NAME1);
        this.attributeList = mockBuildAttributeList(classe);
        this.booleanAttributeType = new BooleanAttributeType();
        List<ResultRow> resultRowList = mockBuildResultRowList();
        this.preparedQuery = mockBuildPreparedQuery(resultRowList);
        this.expCallMap = map("success", null, "data", list(map("name2", null), map("name2", null), map("name2", null)), "meta", null);
    }

    @Test
    public void testReadAll_detailed() {
        System.out.println("readAll_detailed");

        //arrange:
        when(daoService.getAllFunctions()).thenReturn(storedFunctionPagedElements.elements());

        //act:
        Object resultObject = instance.readAll(A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_EMPTY_FILTER, DETAILED);

        //assert:
        verify(daoService).getAllFunctions();
        checkResultList(list(A_KNOWN_NAME1, A_KNOWN_NAME2, A_KNOWN_NAME3), resultObject, "name");
        checkDetailedList(resultObject, "tags");
    }

    @Test
    public void testReadAll_notDetailed() {
        System.out.println("readAll_notDetailed");

        //arrange:
        when(daoService.getAllFunctions()).thenReturn(storedFunctionPagedElements.elements());

        //act:
        Object resultObject = instance.readAll(A_TEST_LIMIT, A_TEST_OFFSET, A_TEST_EMPTY_FILTER, NOT_DETAILED);

        //assert:
        verify(daoService).getAllFunctions();
        checkResultList(list(A_KNOWN_NAME1, A_KNOWN_NAME2, A_KNOWN_NAME3), resultObject, "name");
        checkNotDetailedList(resultObject, "tags");
    }

    @Test
    public void testRead_creatable() {
        System.out.println("read_creatable");

        //arrange:
        when(daoService.getFunctionById(anyLong())).thenReturn(storedFunctionPagedElements.elements().get(0));

        //act:
        Object resultObject = instance.read(A_KNOWN_NUMBER);

        //assert:
        verify(daoService).getFunctionById(anyLong());
        checkResult(A_KNOWN_NAME1, resultObject, "name");
    }

    @Test
    public void testRead_nonCreatable() {
        System.out.println("read_nonCreatable");

        //arrange:
        when(daoService.getFunctionByName(anyString())).thenReturn(storedFunctionPagedElements.elements().get(0));

        //act:
        Object resultObject = instance.read(A_KNOWN_GENERIC_ID1);

        //assert:
        verify(daoService).getFunctionByName(anyString());
        checkResult(A_KNOWN_NAME1, resultObject, "name");
    }

    @Test
    public void testReadInputParameters() {
        System.out.println("readInputParameters");

        //arrange:
        when(daoService.getFunctionById(anyLong())).thenReturn(storedFunctionPagedElements.elements().get(0));

        //act:
        Object resultObject = instance.readInputParameters(A_KNOWN_NUMBER, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(daoService).getFunctionById(anyLong());
        checkResultList(list(A_KNOWN_NAME1), resultObject, "_id");
    }

    @Test
    public void testReadOutputParameters() {
        System.out.println("readOutputParameters");

        //arrange:
        when(daoService.getFunctionById(anyLong())).thenReturn(storedFunctionPagedElements.elements().get(0));

        //act:
        Object resultObject = instance.readOutputParameters(A_KNOWN_NUMBER, A_TEST_LIMIT, A_TEST_OFFSET);

        //assert:
        verify(daoService).getFunctionById(anyLong());
        checkResultList(list(A_KNOWN_NAME2), resultObject, "_id");
    }

    @Test
    public void testCall() {
        System.out.println("call");

        //arrange:


        when(daoService.getFunctionByName(A_KNOWN_GENERIC_ID1)).thenReturn(storedFunctionPagedElements.elements().get(0));
        when(daoService.selectFunction(any(StoredFunction.class), anyList(), anyList())).thenReturn(preparedQuery);

        //act:
        Object resultObject = instance.call(A_KNOWN_GENERIC_ID1, A_TEST_FILTER, A_TEST_MODEL_STRING);

        //assert:
        verify(daoService).getFunctionByName(A_KNOWN_GENERIC_ID1);
        verify(daoService).selectFunction(any(StoredFunction.class), anyList(), anyList());
        checkResultStructure(expCallMap, ((Map<String, Object>) resultObject));
    }

    @Test
    public void testCallAsPost() {
        System.out.println("callAsPost");

        //arrange:
        when(daoService.getFunctionByName(A_KNOWN_GENERIC_ID1)).thenReturn(storedFunctionPagedElements.elements().get(0));
        when(daoService.selectFunction(any(StoredFunction.class), anyList(), anyList())).thenReturn(preparedQuery);

        //act:
        Object resultObject = instance.callAsPost(A_KNOWN_GENERIC_ID1, A_TEST_MODEL_STRING, A_TEST_FILTER);

        //assert:
        verify(daoService).getFunctionByName(A_KNOWN_GENERIC_ID1);
        verify(daoService).selectFunction(any(StoredFunction.class), anyList(), anyList());
        checkResultStructure(expCallMap, ((Map<String, Object>) resultObject));
    }

}
