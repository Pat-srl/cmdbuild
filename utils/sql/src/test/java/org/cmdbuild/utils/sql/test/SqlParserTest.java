/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.sql.test;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import org.cmdbuild.utils.sql.SqlScriptFunctionToken;
import static org.cmdbuild.utils.sql.SqlScriptUtils.parseSqlFunctionTokensFromScript;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

public class SqlParserTest {

    private String s;

    @Before
    public void setUp() throws URISyntaxException {
        s = readToString(getClass().getResourceAsStream("/org/cmdbuild/utils/sql/test/10_test.sql"));
        assertNotNull(s);
    }

    @Test
    public void testFunctionParsing1() {
        List<SqlScriptFunctionToken> functionTokenList = parseSqlFunctionTokensFromScript(s);
        assertNotNull(functionTokenList);
        assertEquals(false, functionTokenList.isEmpty());
    }

    @Test
    public void testFunctionParsing2() {
        List<SqlScriptFunctionToken> functionTokenList = parseSqlFunctionTokensFromScript(s);
        for (SqlScriptFunctionToken functionToken : functionTokenList) {
            assertNotNull("Cannot get function name", functionToken.getFunctionName());
            assertNotNull("Cannot get function definition", functionToken.getFunctionDefinition());
            assertNotNull("Cannot get function signature", functionToken.getFunctionSignature());
        }
    }

    @Test
    public void testFunctionParsing3() {
        List<SqlScriptFunctionToken> functionTokenList = parseSqlFunctionTokensFromScript(s);
        assertEquals("test_setup", functionTokenList.get(0).getFunctionName());
    }

    @Test
    public void testFunctionParsing4() {
        List<SqlScriptFunctionToken> functionTokenList = parseSqlFunctionTokensFromScript(s);
        Set<String> map = new HashSet<>(Arrays.asList("test_setup", "test_case_user_existance", "test_case_user_description_existance"));
        for (SqlScriptFunctionToken functionToken : functionTokenList) {
            if (!map.contains(functionToken.getFunctionName())) {
                fail("Parsed something wrong");
            }
        }
    }

    @Test
    public void testFunctionParsing5() {
        List<SqlScriptFunctionToken> functionTokenList = parseSqlFunctionTokensFromScript(s);
        assertEquals("Error parsing function definition of first function", "CREATE OR REPLACE FUNCTION test_setup() RETURNS void AS $$ BEGIN\n"
                + "    DROP TABLE IF EXISTS test CASCADE;\n"
                + "    CREATE TABLE test(my_id INT, description VARCHAR);\n"
                + "    INSERT INTO test(my_id, description) VALUES (100, 'test description');\n"
                + "END $$ LANGUAGE plpgsql;", functionTokenList.get(0).getFunctionDefinition());

        assertEquals("Error parsing function definition of second function", "CREATE OR REPLACE FUNCTION test_case_user_existance() RETURNS void AS $$\n"
                + "    DECLARE\n"
                + "        id INT;\n"
                + "    BEGIN\n"
                + "        SELECT my_id FROM test INTO id;\n"
                + "        perform test_assertNotNull('test id not found', id);\n"
                + "    END;\n"
                + "$$ LANGUAGE plpgsql;", functionTokenList.get(1).getFunctionDefinition());

        assertEquals("Error parsing function definition of third function", "CREATE OR REPLACE FUNCTION test_case_user_description_existance() RETURNS void AS $$\n"
                + "    DECLARE\n"
                + "        descr VARCHAR;\n"
                + "    BEGIN\n"
                + "        SELECT description FROM test INTO descr;\n"
                + "        perform test_assertNotNull('description not found', descr);\n"
                + "    END;\n"
                + "$$ LANGUAGE plpgsql;", functionTokenList.get(2).getFunctionDefinition());
    }
}
