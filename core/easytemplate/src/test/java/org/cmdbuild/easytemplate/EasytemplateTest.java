/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import java.util.Map;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.EasytemplateProcessor;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import static org.cmdbuild.easytemplate.FtlUtils.isFtlTemplate;
import static org.cmdbuild.easytemplate.FtlUtils.prepareFtlTemplateFixHeaderIfRequired;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

public class EasytemplateTest {

    @Test
    public void testFtlDetection() {
        assertTrue(isFtlTemplate("<#ftl> something something\nsomething"));
        assertTrue(isFtlTemplate("[#ftl asd=\"aaa\"] something something\nsomething"));
        assertTrue(isFtlTemplate("<div>[ftl asd=\"aaa\"] something something\nsomething"));
        assertFalse(isFtlTemplate("<div>[asd asd=\"aaa\"] something something\nsomething"));
        assertFalse(isFtlTemplate("asd"));
        assertFalse(isFtlTemplate("   "));
        assertFalse(isFtlTemplate(""));
        assertFalse(isFtlTemplate(null));
    }

    @Test
    public void testFtlPreprocessing() {
        assertEquals("<#ftl> something something\nsomething", prepareFtlTemplateFixHeaderIfRequired("<#ftl> something something\nsomething"));
        assertEquals("[#ftl asd=\"aaa\"] something something\nsomething", prepareFtlTemplateFixHeaderIfRequired("[#ftl asd=\"aaa\"] something something\nsomething"));
        assertEquals("[ftl asd=\"aaa\"]<div> something something\nsomething", prepareFtlTemplateFixHeaderIfRequired("<div>[ftl asd=\"aaa\"] something something\nsomething"));
    }

    @Test
    public void testEasytemplateProcessing1() {
        EasytemplateProcessor processor = EasytemplateProcessorImpl.builder()
                .withResolver("test", (x) -> x.toUpperCase())
                .withResolver("", (x) -> x.toLowerCase())
                .build();

        assertEquals("ciao", processor.processExpression("ciao"));
        assertEquals("ciao {} asd", processor.processExpression("ciao {} asd"));
        assertEquals("ciao HELLO asd", processor.processExpression("ciao {test:Hello} asd"));
        assertEquals("ciao unsupported:hello asd", processor.processExpression("ciao {unsupported:Hello} asd"));
        assertEquals("ciao not an expression asd", processor.processExpression("ciao {not AN expression} asd"));
        assertEquals("ciao not an expression asd", processor.processExpression("ciao {{not AN expression}} asd"));
        assertEquals("ciao {not AN expression} asd", processor.processExpression("ciao {symbol:open}not AN expression{symbol:close} asd"));
        assertEquals("ciao {not AN expression} asd", processor.processExpression("{easytemplate:disable}ciao {not AN expression} asd"));
    }

    @Test
    public void testEasytemplateProcessing2() {
        EasytemplateProcessor processor = EasytemplateProcessorImpl.builder()
                .withResolver("test", (x) -> x.toUpperCase())
                .build();

        assertEquals("ciao", processor.processExpression("ciao"));
        assertEquals("ciao {} asd", processor.processExpression("ciao {} asd"));
        assertEquals("ciao HELLO asd", processor.processExpression("ciao {test:Hello} asd"));
        assertEquals("ciao {unsupported:Hello} asd", processor.processExpression("ciao {unsupported:Hello} asd"));
        assertEquals("ciao {not AN expression} asd", processor.processExpression("ciao {not AN expression} asd"));
        assertEquals("ciao {{not AN expression}} asd", processor.processExpression("ciao {{not AN expression}} asd"));
    }

    @Test(timeout = 10000)
    public void testEasytemplateRecursiveProcessingLimit() {
        Map<String, String> source = map("my_expr", "this is some content with an expr: {test:my_expr}. Bye!");

        EasytemplateProcessor processor = EasytemplateProcessorImpl.builder()
                .withResolver("test", source::get)
                .build();

        try {
            processor.processExpression("hello! {test:my_expr}");
            fail();
        } catch (RuntimeException ex) {
            assertTrue(ex.toString().contains("too many processing loops while resolving expr"));
        }
    }

    public void testEasytemplateRecursiveValProcessing1() {
        Map<String, String> source = map("my_expr", "this is some content with an expr pattern: {test:my_expr}. Bye!");

        EasytemplateProcessor processor = EasytemplateProcessorImpl.builder()
                .withResolver("test", source::get, false)
                .build();

        assertEquals("hello! this is some content with an expr pattern: {test:my_expr}. Bye!", processor.processExpression("hello! {test:my_expr}"));
    }

    public void testEasytemplateRecursiveValProcessing2() {
        Map<String, String> first = map(
                "one", "{upper:{second:two}}"
        );
        Map<String, String> second = map(
                "two", "This is some content with an expr pattern: {first:my_expr}. Bye!"
        );

        EasytemplateProcessor processor = EasytemplateProcessorImpl.builder()
                .withResolver("first", first::get)
                .withResolver("second", second::get, false)
                .withResolver("upper", (x) -> x.toUpperCase())
                .build();

        assertEquals("hello! THIS IS SOME CONTENT WITH AN EXPR PATTERN: {SECOND:MY_EXPR}. BYE!", processor.processExpression("hello! {first:one}"));
    }

    public void testEasytemplateRecursiveValProcessing3() {
        Map<String, String> first = map(
                "one", "{upper:{second:two}}"
        );
        Map<String, String> second = map(
                "two", "This is some content with an expr pattern: {first:my_expr}. Bye!"
        );

        EasytemplateProcessor processor = EasytemplateProcessorImpl.builder()
                .withResolver("first", first::get)
                .withResolver("second", second::get, false)
                .withResolver("upper", (x) -> x.toLowerCase())
                .build();

        assertEquals("hello! this is some content with an expr pattern: {second:my_expr}. bye!", processor.processExpression("hello! {first:one}"));
    }

}
