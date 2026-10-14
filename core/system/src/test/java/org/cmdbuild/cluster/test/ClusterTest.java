package org.cmdbuild.cluster.test;

import org.junit.Test;

public class ClusterTest {

    @Test
    public void testIgniteJdbcDriver() throws ClassNotFoundException {
        Class.forName("org.apache.ignite.IgniteJdbcThinDriver");
    }

}
