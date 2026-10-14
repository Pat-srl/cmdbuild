package org.cmdbuild.fault.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import static com.google.common.collect.Iterables.getOnlyElement;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.cmdbuild.fault.FaultEventImpl;
import static org.cmdbuild.fault.FaultLevel.FL_ERROR;
import org.cmdbuild.fault.FaultMessage;
import org.cmdbuild.fault.FaultUtils;
import static org.cmdbuild.fault.FaultUtils.errorToJsonMessages;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class FaultUtilsTest {

    @Test
    public void testFaultUtils1() {
        FaultMessage message = FaultUtils.errorToMessages(FaultEventImpl.error("org.cmdbuild.dao.DaoException: error executing insert for entry = CardImpl{id=null, type=TestClass1}, caused by: org.springframework.jdbc.UncategorizedSQLException: StatementCallback; uncategorized SQLException; SQL state [P0001]; error code [0]; ERRORE: CM[my.translation.code]: other!; nested exception is org.postgresql.util.PSQLException: ERRORE: CM[my.translation.code]: other!, caused by: org.postgresql.util.PSQLException: ERRORE: CM[my.translation.code]: other!")).get(0);
        assertEquals(FL_ERROR, message.getLevel());
        assertEquals("other!", message.getMessage());
        assertEquals("my.translation.code", message.getCode());

        message = FaultUtils.errorToMessages(FaultEventImpl.error("org.postgresql.util.PSQLException: ERRORE: CM my.translation.code: other!")).get(0);
        assertEquals("other!", message.getMessage());
        assertEquals("my.translation.code", message.getCode());

        message = FaultUtils.errorToMessages(FaultEventImpl.error("org.postgresql.util.PSQLException: ERRORE: CM:my.translation.code: other!")).get(0);
        assertEquals("other!", message.getMessage());
        assertEquals("my.translation.code", message.getCode());

    }

    @Test
    public void testErrorToMessage1() {
        List<Map<String, Object>> messages = errorToJsonMessages(FaultEventImpl.error(new NullPointerException("attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}")));

        assertEquals(1, messages.size());

        Map<String, String> element = list(messages).collect(onlyElement());

        assertEquals(false, element.get("show_user"));
        assertEquals("ERROR", element.get("level"));
        assertEquals("java.lang.NullPointerException: attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}", element.get("message"));
    }

    @Test
    public void testErrorToMessage2() {
        List<Map<String, Object>> messages = errorToJsonMessages(FaultEventImpl.error(new NullPointerException("CM: attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}")));

        assertEquals(2, messages.size());

        {
            Map<String, String> element = getSingleElementWithShowUser(messages);

            assertEquals(true, element.get("show_user"));
            assertEquals("ERROR", element.get("level"));
            assertEquals("attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}", element.get("message"));
        }

        {
            Map<String, String> element = getSingleElementWithoutShowUser(messages);

            assertEquals(false, element.get("show_user"));
            assertEquals("ERROR", element.get("level"));
            assertEquals("java.lang.NullPointerException: CM: attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}", element.get("message"));
        }
    }

    @Test
    public void testErrorToMessage3() {
        List<Map<String, Object>> messages = errorToJsonMessages(FaultEventImpl.error(new IOException(new RuntimeException("uncategorized SQLException for SQL []; SQL state [P0001]; error code [0]; ERRORE: CM_CUSTOM_EXCEPTION: friendly exception!; nested exception is org.postgresql.util.PSQLException: ERRORE: CM_CUSTOM_EXCEPTION: friendly exception!, caused by: org.postgresql.util.PSQLException: ERRORE: CM_CUSTOM_EXCEPTION: friendly exception!"))));

        assertEquals(2, messages.size());

        {
            Map<String, String> element = getSingleElementWithShowUser(messages);

            assertEquals(true, element.get("show_user"));
            assertEquals("ERROR", element.get("level"));
            assertEquals("friendly exception!", element.get("message"));
        }

        {
            Map<String, String> element = getSingleElementWithoutShowUser(messages);

            assertEquals(false, element.get("show_user"));
            assertEquals("ERROR", element.get("level"));
            assertEquals("java.io.IOException: java.lang.RuntimeException: uncategorized SQLException for SQL []; SQL state [P0001]; error code [0]; ERRORE: CM_CUSTOM_EXCEPTION: friendly exception!; nested exception is org.postgresql.util.PSQLException: ERRORE: CM_CUSTOM_EXCEPTION: friendly exception!, caused by: org.postgresql.util.PSQLException: ERRORE: CM_CUSTOM_EXCEPTION: friendly exception!, caused by: java.lang.RuntimeException: uncategorized SQLException for SQL []; SQL state [P0001]; error code [0]; ERRORE: CM_CUSTOM_EXCEPTION: friendly exception!; nested exception is org.postgresql.util.PSQLException: ERRORE: CM_CUSTOM_EXCEPTION: friendly exception!, caused by: org.postgresql.util.PSQLException: ERRORE: CM_CUSTOM_EXCEPTION: friendly exception!", element.get("message"));
        }
    }

    @Test
    public void testErrorToMessage4() {
        List<Map<String, Object>> messages = errorToJsonMessages(FaultEventImpl.error(new IOException(new RuntimeException("uncategorized SQLException for SQL []; SQL state [P0001]; error code [0]; ERROR: CM_CUSTOM_EXCEPTION: friendly exception!\n  Where: PL/pgSQL function exception_test_function() line 1 at RAISE"))));

        assertEquals(2, messages.size());

        {
            Map<String, String> element = getSingleElementWithShowUser(messages);

            assertEquals(true, element.get("show_user"));
            assertEquals("ERROR", element.get("level"));
            assertEquals("friendly exception!", element.get("message"));
        }

        {
            Map<String, String> element = getSingleElementWithoutShowUser(messages);

            assertEquals(false, element.get("show_user"));
            assertEquals("ERROR", element.get("level"));
            assertEquals("java.io.IOException: java.lang.RuntimeException: uncategorized SQLException for SQL []; SQL state [P0001]; error code [0]; ERROR: CM_CUSTOM_EXCEPTION: friendly exception!\n  Where: PL/pgSQL function exception_test_function() line 1 at RAISE, caused by: java.lang.RuntimeException: uncategorized SQLException for SQL []; SQL state [P0001]; error code [0]; ERROR: CM_CUSTOM_EXCEPTION: friendly exception!\n  Where: PL/pgSQL function exception_test_function() line 1 at RAISE", element.get("message"));
        }
    }

    @Test
    public void testErrorCodes1() {
        List<Map<String, Object>> messages = errorToJsonMessages(FaultEventImpl.error(new NullPointerException("CME 123: attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}")));

        assertEquals(1, messages.size());

        Map<String, String> element = messages.stream().collect(onlyElement());

        assertEquals(false, element.get("show_user"));
        assertEquals("ERROR", element.get("level"));
        assertEquals("123", element.get("code"));
        assertEquals("java.lang.NullPointerException: CME 123: attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}", element.get("message"));

        assertEquals("123", getOnlyElement(errorToJsonMessages(FaultEventImpl.error(new NullPointerException("CME 123; test")))).get("code"));
        assertEquals("123", getOnlyElement(errorToJsonMessages(FaultEventImpl.error(new NullPointerException("CME 123 test")))).get("code"));
        assertEquals("123", getOnlyElement(errorToJsonMessages(FaultEventImpl.error(new NullPointerException("CME[123] test")))).get("code"));
        assertEquals("123", getOnlyElement(errorToJsonMessages(FaultEventImpl.error(new NullPointerException("CME(123) test")))).get("code"));
    }

    @Test
    public void testErrorCodes2() {
        List<Map<String, Object>> messages = errorToJsonMessages(FaultEventImpl.error(new NullPointerException("CME 123: CM: attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}")));

        assertEquals(2, messages.size());

        {
            Map<String, String> element = getSingleElementWithShowUser(messages);

            assertEquals(true, element.get("show_user"));
            assertEquals("ERROR", element.get("level"));
            assertEquals("123", element.get("code"));
            assertEquals("attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}", element.get("message"));
        }

        {
            Map<String, String> element = getSingleElementWithoutShowUser(messages);;

            assertEquals(false, element.get("show_user"));
            assertEquals("ERROR", element.get("level"));
            assertEquals("123", element.get("code"));
            assertEquals("java.lang.NullPointerException: CME 123: CM: attribute value is null or missing for key = cm_filter_mark_ResponsabileMarketing+role:Direzione within entry = CardImpl{id=6230503, code=AD01 - AperturaPortale, type=AccountDemoCMDBuild}", element.get("message"));
        }
    }

    private Map<String, String> getSingleElementWithShowUser(List<Map<String, Object>> messages) {
        return messages.stream().filter(m -> toBoolean(m.get("show_user"))).collect(onlyElement());
    }

    private Map<String, String> getSingleElementWithoutShowUser(List<Map<String, Object>> messages) {
        return messages.stream().filter(m -> !toBoolean(m.get("show_user"))).collect(onlyElement());
    }

}
