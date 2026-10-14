/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.postgres.test;

import static java.util.Collections.emptyList;
import org.cmdbuild.dao.entrytype.attributetype.ByteaArrayAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.StringArrayAttributeType;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.systemToSqlExpr;
import org.cmdbuild.utils.date.CmDateUtils;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

public class SystemToSqlExprTest {

	@Test
	public void testNullFail() {
		assertEquals("NULL", systemToSqlExpr(null));
	}

	@Test
	public void testStringEscape1() {
		String expr = systemToSqlExpr("Test");
		assertEquals("'Test'", expr);
	}

	@Test
	public void testStringEscape2() {
		String expr = systemToSqlExpr("T'es''t");
		assertEquals("'T''es''''t'", expr);
	}

	@Test
	public void testStringEscape3() {
		String expr = systemToSqlExpr("");
		assertEquals("''", expr);
	}

	@Test
	public void testIntegerEscape() {
		String expr = systemToSqlExpr(1);
		assertEquals("1", expr);
	}

	@Test
	public void testLongEscape() {
		String expr = systemToSqlExpr(1234567891232345346l);
		assertEquals("1234567891232345346", expr);
	}

	@Test
	public void testDateEscape() {
		String expr = systemToSqlExpr(CmDateUtils.toDate(1550483057134l));
		assertEquals("DATE '2019-02-18'", expr);
	}

	@Test
	public void testTimeEscape() {
		String expr = systemToSqlExpr(CmDateUtils.toTime(1550483057134l));
		assertEquals("TIME '09:44:17.134'", expr);
	}

	@Test
	public void testDateTimeEscape() {
		String expr = systemToSqlExpr(CmDateUtils.toDateTime(1550483057134l));
		assertEquals("TIMESTAMPTZ '2019-02-18T09:44:17.134Z'", expr);
	}

	@Test
	public void testLongArrayEscape() {
		String expr = systemToSqlExpr(list(1l, 2l, 1234567891232345346l));
		assertEquals("ARRAY[1,2,1234567891232345346]", expr);
	}

	@Test
	public void testStringArrayEscape() {
		String expr = systemToSqlExpr(list("a", "b", "c"));
		assertEquals("ARRAY['a','b','c']::varchar[]", expr);
	}

	@Test
	public void testBooleanEscape() {
		String expr = systemToSqlExpr(true);
		assertEquals("TRUE", expr);
	}

	@Test
	public void testEmptyStringArrayEscape2() {
		String expr = systemToSqlExpr(emptyList(), new StringArrayAttributeType());
		assertEquals("ARRAY[]::varchar[]", expr);
	}

	@Test
	public void testEmptyByteaArrayEscape() {
		String expr = systemToSqlExpr(emptyList(), new ByteaArrayAttributeType());
		assertEquals("ARRAY[]::bytea[]", expr);
	}

}
