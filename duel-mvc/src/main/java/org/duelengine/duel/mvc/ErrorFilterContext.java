package org.duelengine.duel.mvc;

import com.google.inject.Inject;

public class ErrorFilterContext {

	private final DuelMvcContext context;
	private Throwable error;
	private Object result;
	private boolean handled;

	@Inject
	public ErrorFilterContext(DuelMvcContext context) {

		this.context = context;
	}

	public DuelMvcContext getMvcContext() {
		return context;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object value) {
		result = value;
	}

	public Throwable getError() {
		return error;
	}

	public void setError(Throwable value) {
		error = value;
	}

	public boolean isHandled() {
		return handled;
	}

	public void setHandled(boolean value) {
		this.handled = value;
	}
}
