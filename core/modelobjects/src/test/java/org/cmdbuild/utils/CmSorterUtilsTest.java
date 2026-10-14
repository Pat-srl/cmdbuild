/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils;

import static com.google.common.collect.Iterables.getOnlyElement;
import org.cmdbuild.data.filter.CmdbSorter;
import static org.cmdbuild.data.filter.SorterElementDirection.ASC;
import static org.cmdbuild.data.filter.SorterElementDirection.DESC;
import static org.cmdbuild.dao.utils.CmSorterUtils.parseSorter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class CmSorterUtilsTest {

    @Test
    public void testSorterUtils() {
        assertTrue(parseSorter(null).isNoop());
        assertTrue(parseSorter("").isNoop());
        assertTrue(parseSorter("[]").isNoop());

        CmdbSorter sorter = parseSorter("[{property: \"Building.Name\", direction: \"ASC\"}]");
        assertEquals(1, sorter.count());
        assertEquals("Building.Name", getOnlyElement(sorter.getElements()).getProperty());
        assertEquals(ASC, getOnlyElement(sorter.getElements()).getDirection());

        sorter = parseSorter("[{property: \"Building.Name\", direction: \"DESC\"}]");
        assertEquals(1, sorter.count());
        assertEquals("Building.Name", getOnlyElement(sorter.getElements()).getProperty());
        assertEquals(DESC, getOnlyElement(sorter.getElements()).getDirection());

        sorter = parseSorter("[{attribute: \"Code\", direction: \"ascending\"}]");
        assertEquals(1, sorter.count());
        assertEquals("Code", getOnlyElement(sorter.getElements()).getProperty());
        assertEquals(ASC, getOnlyElement(sorter.getElements()).getDirection());

        sorter = parseSorter("[{attribute: \"Code\", direction: \"descending\"}]");
        assertEquals(1, sorter.count());
        assertEquals("Code", getOnlyElement(sorter.getElements()).getProperty());
        assertEquals(DESC, getOnlyElement(sorter.getElements()).getDirection());
    }

}
