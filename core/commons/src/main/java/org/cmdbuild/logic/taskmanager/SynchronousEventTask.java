/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.logic.taskmanager;

import java.util.Map;

public interface SynchronousEventTask extends Task {

	boolean isEmailEnabled();

	String getEmailAccount();

	String getEmailTemplate();

	boolean isWorkflowEnabled();

	Phase getPhase();

	String getWorkflowClassName();

	Map<String, String> getWorkflowAttributes();

	boolean isWorkflowAdvanceable();

	boolean isScriptingEnabled();

	String getScriptingEngine();

	String getScriptingScript();

	boolean isScriptingSafe();

	Iterable<String> getGroups();

	String getTargetClassname();

	String getFilter();

	interface PhaseIdentifier {

		void afterCreate();

		void beforeDelete();

		void afterUpdate();

		void beforeUpdate();

	}

	enum Phase {
		AFTER_CREATE,
		BEFORE_UPDATE,
		AFTER_UPDATE,
		BEFORE_DELETE
	}
}
