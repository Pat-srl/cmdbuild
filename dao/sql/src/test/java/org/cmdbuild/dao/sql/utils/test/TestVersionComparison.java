/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.sql.utils.test;

import org.apache.maven.artifact.versioning.ComparableVersion;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class TestVersionComparison {

    @Test
    public void testVersionComparison1() {
        assertTrue(new ComparableVersion("3.0.1_01_something").compareTo(new ComparableVersion("3.0.1_01")) > 0);
        assertTrue(new ComparableVersion("3.0.1_01").compareTo(new ComparableVersion("3.0.1_01_something")) < 0);
        assertTrue(new ComparableVersion("3.0.1_02").compareTo(new ComparableVersion("3.0.1_01_something")) > 0);
        assertTrue(new ComparableVersion("3.0.1_01").compareTo(new ComparableVersion("3.0.1_02_something")) < 0);
        assertTrue(new ComparableVersion("3.0.1_02_something").compareTo(new ComparableVersion("3.0.1_01")) > 0);
        assertTrue(new ComparableVersion("3.0.1_01_something").compareTo(new ComparableVersion("3.0.1_02")) < 0);
    }

}
