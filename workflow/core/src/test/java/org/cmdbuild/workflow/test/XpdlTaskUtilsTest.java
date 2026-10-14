package org.cmdbuild.workflow.test;

import org.cmdbuild.workflow.xpdl.XpdlTaskUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.cmdbuild.workflow.model.TaskAttribute;

public class XpdlTaskUtilsTest {

	@Test
	public void returnsNullForInvalidEntries() {
		assertNull(XpdlTaskUtils.taskVariableFromXpdlKeyValue("Rubbish", "Foo"));
		assertNull(XpdlTaskUtils.taskVariableFromXpdlKeyValue("VariableToProcess_VIEW", null));
	}

	@Test
	public void testViewVariableType() {
		TaskAttribute taskVariable = XpdlTaskUtils.taskVariableFromXpdlKeyValue("VariableToProcess_VIEW", "Foo");

		assertNotNull(taskVariable);
		assertEquals(false, taskVariable.isAction());
		assertEquals(false, taskVariable.isMandatory());
		assertEquals(false, taskVariable.isWritable());
		assertEquals("Foo", taskVariable.getName());
	}

	@Test
	public void testUpdateVariableType() {
		TaskAttribute taskVariable = XpdlTaskUtils.taskVariableFromXpdlKeyValue("VariableToProcess_UPDATE", "Bar");

		assertNotNull(taskVariable);
		assertEquals(false, taskVariable.isAction());
		assertEquals(false, taskVariable.isMandatory());
		assertEquals(true, taskVariable.isWritable());
		assertEquals("Bar", taskVariable.getName());
	}

	@Test
	public void testUpdateRequiredVariableType() {
		TaskAttribute taskVariable = XpdlTaskUtils.taskVariableFromXpdlKeyValue("VariableToProcess_UPDATEREQUIRED", "Baz");

		assertNotNull(taskVariable);
		assertEquals(false, taskVariable.isAction());
		assertEquals(true, taskVariable.isMandatory());
		assertEquals(true, taskVariable.isWritable());
		assertEquals("Baz", taskVariable.getName());
	}

	@Test
	public void testActionVariableType() {
		TaskAttribute taskVariable = XpdlTaskUtils.taskVariableFromXpdlKeyValue("VariableToProcess_ACTION", "Something");

		assertNotNull(taskVariable);
		assertEquals(true, taskVariable.isAction());
		assertEquals(true, taskVariable.isMandatory());
		assertEquals(true, taskVariable.isWritable());
		assertEquals("Something", taskVariable.getName());
	}

}
