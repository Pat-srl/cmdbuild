/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;

/**
 *
 * @deprecated used by legacy shark code
*/
@Deprecated
public interface FlowData {

	FlowStatus getStatus();

	FlowInfo getFlowInfo();

	Map<String, ?> values();

	default boolean hasTasksToAdd() {
		return getTasksToAdd() != null;
	}

	default boolean hasTasksToSet() {
		return getTasksToSet() != null;
	}

	@Nullable
	List<TaskInfo> getTasksToAdd();

	@Nullable
	List<TaskInfo> getTasksToSet();

}
