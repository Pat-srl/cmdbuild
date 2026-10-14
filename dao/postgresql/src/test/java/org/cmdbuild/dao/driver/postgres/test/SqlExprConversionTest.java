/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.test;

import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Base64;
import org.cmdbuild.dao.entrytype.attributetype.JsonAttributeType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.escapeLikeExpression;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class SqlExprConversionTest {

    @Test
    public void testEscapeLikeExpression() {
        assertEquals("asd\\%", escapeLikeExpression("asd%"));
        assertEquals("\\%as\\%d\\%", escapeLikeExpression("%as%d%"));
        assertEquals("\\_as\\_d\\_", escapeLikeExpression("_as_d_"));
        assertEquals("as\\\\d", escapeLikeExpression("as\\d"));
        assertEquals("\\\\as\\\\d\\\\", escapeLikeExpression("\\as\\d\\"));
        assertEquals("\\\\as\\_d\\%", escapeLikeExpression("\\as_d%"));
    }

    @Test
    public void testIntToSql() {
        assertEquals("123", systemToSqlExpr(123));
    }

    @Test
    public void testLongToSql() {
        assertEquals("123456789", systemToSqlExpr(123456789l));
    }

    @Test
    public void testByteaToSql() {
        assertEquals("decode('aGVsbG8h','base64')", systemToSqlExpr("hello!".getBytes(StandardCharsets.UTF_8)));
        assertEquals(new String(Base64.decodeBase64("aGVsbG8h"), StandardCharsets.UTF_8), "hello!");
    }

    @Test
    public void testByteaaToSql() {
        assertEquals("ARRAY[decode('aGVsbG8h','base64'),decode('dGhlcmUh','base64')]", systemToSqlExpr(list("hello!".getBytes(StandardCharsets.UTF_8), "there!".getBytes(StandardCharsets.UTF_8))));
        assertEquals(new String(Base64.decodeBase64("aGVsbG8h"), StandardCharsets.UTF_8), "hello!");
        assertEquals(new String(Base64.decodeBase64("dGhlcmUh"), StandardCharsets.UTF_8), "there!");
    }

    @Test
    public void testListOfStringsToSql() {
        assertEquals("ARRAY['asd','dsa','12']::varchar[]", systemToSqlExpr(list("asd", "dsa", "12")));
    }

    @Test
    public void testListOfIntsToSql() {
        assertEquals("ARRAY[12,34,56]", systemToSqlExpr(list(12, 34, 56)));
    }

    @Test
    public void testJsonToSql1() {
        assertEquals("NULL", systemToSqlExpr(null, JsonAttributeType.INSTANCE));
    }

    @Test
    public void testJsonToSql2() {
        assertEquals("NULL", systemToSqlExpr("", JsonAttributeType.INSTANCE));
    }

    @Test
    public void testJsonToSql3() {
        assertEquals("'{\"a\":1}'::jsonb", systemToSqlExpr(toJson(map("a", 1)), JsonAttributeType.INSTANCE));
    }
}
