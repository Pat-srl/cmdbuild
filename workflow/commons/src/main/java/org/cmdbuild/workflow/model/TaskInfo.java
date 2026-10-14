package org.cmdbuild.workflow.model;

import static java.util.Arrays.asList;
import java.util.Collection;

public interface TaskInfo {

	String getFlowId();

	String getTaskDefinitionId();

	String getTaskId();

	String getTaskName();

	String getTaskDescription();

	default Collection<String> getParticipantList() {
		return asList(getParticipants());
	}

	@Deprecated
	default String[] getParticipants() {
		return getParticipantList().toArray(new String[]{});
	}
}
