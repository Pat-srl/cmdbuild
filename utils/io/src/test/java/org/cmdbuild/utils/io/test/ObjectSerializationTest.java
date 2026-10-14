/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.io.test;

import java.io.Serializable;
import static org.cmdbuild.utils.io.CmIoUtils.deserializeObject;
import static org.cmdbuild.utils.io.CmIoUtils.serializeObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ObjectSerializationTest {

	@Test
	public void testObjectSerialization() {
		TestBean source = new TestBean("test one", 123);
		byte[] data = serializeObject(source);
		assertTrue(data.length > 0);
		TestBean target = deserializeObject(data);
		assertNotNull(target);
		assertEquals(source.value, target.value);
		assertEquals(source.number, target.number);
	}

	public static class TestBean implements Serializable {

		private String value;
		private int number;

		public TestBean() {
		}

		public TestBean(String value, int number) {
			this.value = value;
			this.number = number;
		}

	}

}
