/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.test;

import org.cmdbuild.dao.config.inner.Patch;
import org.cmdbuild.dao.config.inner.PatchImpl;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PatchParsingTest {

	@Test
	public void patchParsing1() {
		Patch patch = PatchImpl.builder()
				.withVersion("1.0")
				.withContent(readToString(getClass().getResourceAsStream("/org/cmdbuild/dao/config/test/test_patch_1.sql")))
				.build();
		assertEquals("postgis fix 2", patch.getDescription());
		assertEquals("true", patch.getParam("REQUIRE_SUPERUSER"));
	}

}
