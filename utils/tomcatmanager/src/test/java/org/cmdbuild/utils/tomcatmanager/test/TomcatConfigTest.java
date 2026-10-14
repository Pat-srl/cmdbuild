/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.tomcatmanager.test;

import org.cmdbuild.utils.tomcatmanager.TomcatConfig;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_HTTP_PORT;
import static org.cmdbuild.utils.tomcatmanager.TomcatConfig.TOMCAT_PORT_OFFSET;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TomcatConfigTest {

	@Test
	public void testTomcatConfigCopy() {
		TomcatConfig first = TomcatConfig.builder()
				.withProperty(TOMCAT_PORT_OFFSET, "13")
				.withProperty(TOMCAT_HTTP_PORT, String.valueOf(9000))
				.skipPortCheck()
				.build();

		assertEquals(9013, first.getHttpPort());

		TomcatConfig second = TomcatConfig.copyOf(first).build();

		assertEquals(first.getHttpPort(), second.getHttpPort());
		assertEquals(first.getShutodownPort(), second.getShutodownPort());
		assertEquals(first.getInstallDir().getAbsolutePath(), second.getInstallDir().getAbsolutePath());

	}

	@Test
	public void testTomcatConfigCopy2() {
		TomcatConfig first = TomcatConfig.builder().build();

		TomcatConfig second = TomcatConfig.copyOf(first).build();

		assertEquals(first.getHttpPort(), second.getHttpPort());
		assertEquals(first.getShutodownPort(), second.getShutodownPort());
		assertEquals(first.getInstallDir().getAbsolutePath(), second.getInstallDir().getAbsolutePath());

	}
}
