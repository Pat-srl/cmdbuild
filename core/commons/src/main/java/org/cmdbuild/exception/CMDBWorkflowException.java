package org.cmdbuild.exception;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CMDBWorkflowException extends CMDBException {

	public enum WorkflowExceptionType {
		CQL_COMPILATION_FAILED, //
		WF_CANNOT_ABORT_PROCESS, //
		WF_CANNOT_ACCEPT_WORKITEM, //
		WF_CANNOT_COMPLETE_WORKITEM, //
		WF_CANNOT_CONFIGURE_CMDBEXTATTR, //
		WF_CANNOT_GET_WORKITEM, //
		WF_CANNOT_GET_WORKITEMS, //
		WF_CANNOT_GET_WORKITEM_VARIABLES, //
		WF_CANNOT_GET_PROCESSCARDID, //
		WF_CANNOT_GET_PROCESS_VARIABLES, //
		WF_CANNOT_GETWAPI_CONNECTION, //
		WF_CANNOT_FIND_XPDL, //
		WF_CANNOT_LOAD_ACTIVITIES, //
		WF_CANNOT_LOAD_PROCESSES, //
		WF_CANNOT_REACT_CMDBEXTATTR, //
		WF_CANNOT_REASSIGN_WORKITEM, //
		WF_CANNOT_RESOLVE_PARTICIPANT, //
		WF_CANNOT_RESUME_WORKITEM, //
		WF_CANNOT_START, //
		WF_CANNOT_SUSPEND_PROCESS, //
		WF_CANNOT_UPDATE_WORKITEM_VARIABLES, //
		WF_CANNOT_UPLOAD_PACKAGE, //
		WF_EMAIL_CANNOT_RETRIEVE_MAIL, //
		WF_EMAIL_NOT_CONFIGURED, //
		WF_EMAIL_NOT_SENT, //
		WF_EXTATTR_CREATEREPORT_FACTORYNOTINSESSION, //
		WF_GENERIC_ERROR, //
		WF_PROCESSINFO_NOT_FOUND, //
		WF_XPDL_CLASSNAME_MISSING, //
		WF_XPDL_USERSTOP_MISSING, //
		WF_PACKAGE_ERROR, //
		WF_START_ACTIVITY_NOT_FOUND, //
		WF_ACTIVITY_NOT_FOUND, //
		WF_WAPI_CONNECTION_ERROR, //
		WF_WORKITEM_VARIABLES_REQUIRED, //
		WF_WRONG_CREDENTIALS, //
		WF_WRONG_SUPERCLASS_OPERATION, //
		WF_WRONG_XPDL_CLASSNAME, //
		; //

		public CMDBWorkflowException createException(final String... parameters) {
			return new CMDBWorkflowException(this, parameters);
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	WorkflowExceptionType type;

	public CMDBWorkflowException() {
		this(WorkflowExceptionType.WF_GENERIC_ERROR);
	}

	public CMDBWorkflowException(final WorkflowExceptionType type, final String... parameters) {
		this.type = type;
		this.parameters = parameters;
	}

	public WorkflowExceptionType getExceptionType() {
		return this.type;
	}

	@Override
	public String getExceptionTypeText() {
		return type.name();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof CMDBWorkflowException)) {
			return false;
		}
		final CMDBWorkflowException other = CMDBWorkflowException.class.cast(obj);
		return new EqualsBuilder() //
				.append(this.type, other.type) //
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder() //
				.append(type) //
				.toHashCode();
	}

	@Override
	public String toString() {
		return reflectionToString(this, SHORT_PREFIX_STYLE);
	}

}
