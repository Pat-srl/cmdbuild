/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.sql.utils.test;

import java.util.Map;
import org.cmdbuild.dao.sql.utils.SqlFunction;
import static org.cmdbuild.dao.sql.utils.SqlFunctionUtils.readSqlFunctionsAsMap;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class TestSqlFunctionParsing {

    private String data_1, data_2;

    @Before
    public void init() {
        data_1 = checkNotBlank(readToString(getClass().getResourceAsStream("/org/cmdbuild/dao/sql/utils/test/test_functions_1.sql")));
        data_2 = checkNotBlank(readToString(getClass().getResourceAsStream("/org/cmdbuild/dao/sql/utils/test/test_functions_2.sql")));
    }

    @Test
    public void testSqlFunctionParsing1() {
        Map<String, SqlFunction> functions = readSqlFunctionsAsMap(data_1);
        assertNotNull(functions);
        assertEquals(6, functions.size());
        assertEquals(set(
                "_cm3_system_message_send(varchar,jsonb)",
                "_cm3_system_command_send(varchar,jsonb)",
                "_cm3_system_command_send(varchar)",
                "_cm3_system_command_send(varchar,varchar[])",
                "_cm3_system_reload()",
                "_cm3_system_login()"), functions.keySet());
        SqlFunction function = functions.get("_cm3_system_command_send(varchar,varchar[])");
        assertNotNull(function);
        assertEquals("_cm3_system_command_send(varchar,varchar[])", function.getSignature());
        assertEquals("3.0.0-03", function.getRequiredPatchVersion());
        assertEquals("fba3ywhs6w6nlupsmesau1ba", function.getHash());
        function = functions.get("_cm3_system_command_send(varchar)");
        assertNotNull(function);
        assertEquals("3.0.0-03", function.getRequiredPatchVersion());
        assertEquals("c9nopvgbv88d6lpatolh6g09", function.getHash());
    }

    @Test
    public void testSqlFunctionParsing2() {
        Map<String, SqlFunction> functions = readSqlFunctionsAsMap(data_2);
        assertNotNull(functions);
        assertEquals(6, functions.size());
        assertEquals(set(
                "_cm3_system_message_send(varchar,jsonb)",
                "_cm3_system_command_send(varchar,jsonb)",
                "_cm3_system_command_send(varchar)",
                "_cm3_system_command_send(varchar,varchar[])",
                "_cm3_system_reload()",
                "_cm3_system_login()"), functions.keySet());
        SqlFunction function = functions.get("_cm3_system_command_send(varchar,varchar[])");
        assertNotNull(function);
        assertEquals("_cm3_system_command_send(varchar,varchar[])", function.getSignature());
        assertEquals("3.0.0-03", function.getRequiredPatchVersion());
        assertEquals("fba3ywhs6w6nlupsmesau1ba", function.getHash());
        function = functions.get("_cm3_system_command_send(varchar)");
        assertNotNull(function);
        assertEquals("3.0.0-12", function.getRequiredPatchVersion());
        assertEquals("i3xgqktjzfbgk989klxbok6u", function.getHash());
        function = functions.get("_cm3_system_reload()");
        assertNotNull(function);
        assertEquals("TYPE: function", function.getComment());
    }

}
