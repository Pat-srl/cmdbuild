package org.cmdbuild.exception;

import static java.lang.String.format;

public class WidgetException extends RuntimeException {

    public WidgetException() {
    }

    public WidgetException(String message) {
        super(message);
    }

    public WidgetException(Throwable cause, String message, Object... args) {
        super(format(message, args), cause);
    }

//
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//
//	private final WidgetExceptionType type;
//
//	public enum WidgetExceptionType {
//		WIDGET_SERVICE_MALFORMED_REQUEST, WIDGET_SERVICE_CONNECTION_ERROR;
//
//		public WidgetException createException(final String... parameters) {
//			return new WidgetException(this, parameters);
//		}
//	}
//
//	private WidgetException(final WidgetExceptionType type, final String... parameters) {
//		this.type = type;
//		this.parameters = parameters;
//	}
//
//	public WidgetExceptionType getExceptionType() {
//		return this.type;
//	}
//
//	@Override
//	public String getExceptionTypeText() {
//		return this.type.toString();
//	}
}
