/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import org.cmdbuild.auth.session.OperatorService;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.auth.user.OperationUserImpl.anonymousOperationUser;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

public class EasytemplateServiceTest {

    private String jsContext;

    private EasytemplateService easytemplateService;
    private EasytemplateProcessor processor;

    @Before
    public void init() {
        jsContext = toJson(map("client", map("one", "valOne", "two", map("inner", "innerValue"), "numberVal", 182, "stringNumb", "1827", "arrayLookup.Id", list(234523, 252345))));
        System.out.println(jsContext);

        OperationUser dummyUser = anonymousOperationUser();
        OperationUserSupplier operationUserSupplier = () -> dummyUser;
        OperatorService operatorService = mock(OperatorService.class);
        easytemplateService = new EasytemplateServiceImpl(operationUserSupplier, operatorService);
        processor = easytemplateService.getDefaultProcessorWithJsContext(jsContext);
    }

    @Test
    public void testSimpleJsContext() {
        assertEquals("valOne", processor.processExpression("{client:one}"));
    }

    @Test
    public void testInnerJsContext() {
        assertEquals("innerValue", processor.processExpression("{client:two.inner}"));
    }

    @Test
    public void testJsContextResolveToNull() {
        assertEquals("", processor.processExpression("{client:not.existing}"));
    }

    @Test
    public void testEvalJsSubcontext1() {
        assertEquals("182", processor.processExpression("{client:numberVal}"));
    }

    @Test
    public void testEvalJsSubcontext2() {
        assertEquals("1827", processor.processExpression("{client:stringNumb}"));
    }

    @Test
    public void testEvalJsSubcontext3() {
        assertEquals("182", processor.processExpression("{client:numberVal.Id}"));
    }

    @Test
    public void testEvalJsSubcontext4() {
        assertEquals("1827", processor.processExpression("{client:stringNumb.Id}"));
    }

    @Test
    public void testEvalJsSubcontext5() {
        assertEquals("", processor.processExpression("{client:stringNumb.Code}"));
    }

    @Test
    public void testEvalJsSubcontext6() {
        assertEquals("234523,252345", processor.processExpression("{client:arrayLookup.Id}"));
    }

}
