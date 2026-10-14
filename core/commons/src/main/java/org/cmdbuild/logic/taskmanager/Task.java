package org.cmdbuild.logic.taskmanager;

public interface Task {

	Long getId();

	String getDescription();

	boolean isActive();

	boolean isExecutable();

	TaskType getType();

	enum TaskType {
		ASYNC_EVENT, CONNECTOR, GENERIC, READ_EMAIL, START_WORKFLOW, SYNC_EVENT
	}

	default boolean isScheduledTask() {
		return this instanceof ScheduledTask;
	}

	default ScheduledTask asScheduledTask() {
		return (ScheduledTask) this;
	}

	default boolean isSynchronousEventTask() {
		return this instanceof SynchronousEventTask;
	}

	default SynchronousEventTask asSynchronousEventTask() {
		return (SynchronousEventTask) this;
	}

}
