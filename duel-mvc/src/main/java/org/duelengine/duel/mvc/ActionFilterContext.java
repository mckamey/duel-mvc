package org.duelengine.duel.mvc;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ActionFilterContext {

	private final DuelMvcContext context;
	private final MethodInvocation invocation;
	private Object result;

	@AssistedInject
	public ActionFilterContext(
			DuelMvcContext context,
			@Assisted MethodInvocation invocation) {

		if (context == null) {
			throw new NullPointerException("context");
		}
		if (invocation == null) {
			throw new NullPointerException("invocation");
		}

		this.context = context;
		this.invocation = invocation;
	}

	public DuelMvcContext getMvcContext() {
		return context;
	}

	public Object getController() {
		return invocation.getThis();
	}

	public Method getAction() {
		return invocation.getMethod();
	}

	public Object[] getArguments() {
		return invocation.getArguments();
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object value) {
		result = value;
	}
}
