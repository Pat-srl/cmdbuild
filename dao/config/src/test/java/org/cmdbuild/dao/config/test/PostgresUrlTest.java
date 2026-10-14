/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.test;

import org.cmdbuild.dao.config.utils.PostgresUrl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

public class PostgresUrlTest {

    @Test
    public void testPostgresUrlSerialize() {
        assertEquals("jdbc:postgresql://localhost:5432/myDb", new PostgresUrl("localhost", 5432, "myDb").toJdbcUrl());
        assertEquals("jdbc:postgresql://localhost:5432/myDb", new PostgresUrl(null, 5432, "myDb").toJdbcUrl());
        assertEquals("jdbc:postgresql://localhost:5432/myDb", new PostgresUrl("localhost", null, "myDb").toJdbcUrl());
        assertEquals("jdbc:postgresql://localhost:5432/myDb", new PostgresUrl(null, null, "myDb").toJdbcUrl());

        assertEquals("jdbc:postgresql://smoething:1234/myDb", new PostgresUrl("smoething", 1234, "myDb").toJdbcUrl());
    }

    @Test
    public void testPostgresUrlParse() {
        assertEquals("jdbc:postgresql://smoething:1234/myDb", PostgresUrl.parse("jdbc:postgresql://smoething:1234/myDb").toJdbcUrl());
        assertEquals("jdbc:postgresql://smoething:5432/myDb", PostgresUrl.parse("jdbc:postgresql://smoething/myDb").toJdbcUrl());
    }

    @Test(expected = RuntimeException.class)
    public void testPostgresUrlParseFail1() {
        PostgresUrl.parse("");
    }

    @Test(expected = RuntimeException.class)
    public void testPostgresUrlParseFail2() {
        PostgresUrl.parse(null);
    }
}
