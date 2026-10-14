package org.cmdbuild.logic.taskmanager;

import org.joda.time.DateTime;

public interface ScheduledTask extends Task {

	String getCronExpression();

	DateTime getLastExecution();

}
