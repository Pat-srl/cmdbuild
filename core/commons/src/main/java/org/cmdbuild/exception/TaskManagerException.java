package org.cmdbuild.exception;

public class TaskManagerException extends CMDBException {

	private static final long serialVersionUID = 1L;

	private final TaskManagerExceptionType type;

	public enum TaskManagerExceptionType {
		TASK_EXECUTION_ERROR, //
		;

		public TaskManagerException createException(final Throwable cause, final String... parameters) {
			return new TaskManagerException(this, cause, parameters);
		}

	}

	private TaskManagerException(final TaskManagerExceptionType type, final Throwable cause,
			final String... parameters) {
		this.type = type;
		this.parameters = parameters;
	}

	public TaskManagerExceptionType getExceptionType() {
		return this.type;
	}

	@Override
	public String getExceptionTypeText() {
		return this.type.toString();
	}

}
