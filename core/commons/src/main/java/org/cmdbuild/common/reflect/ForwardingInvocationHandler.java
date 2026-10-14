package org.cmdbuild.common.reflect;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public abstract class ForwardingInvocationHandler implements InvocationHandler {

	private final InvocationHandler delegate;

	protected ForwardingInvocationHandler(final InvocationHandler delegate) {
		this.delegate = delegate;
	}

	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		return delegate.invoke(proxy, method, args);
	}

}
