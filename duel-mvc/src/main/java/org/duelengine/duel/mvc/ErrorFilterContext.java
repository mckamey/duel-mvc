package org.duelengine.duel.mvc;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ErrorFilterContext {

	private final DuelMvcContext context;
	private final Throwable error;
	private boolean handled;
	
	@AssistedInject
	public ErrorFilterContext(
			DuelMvcContext context,
			@Assisted Throwable error) {

		this.context = context;
		this.error = error;
	}

	public DuelMvcContext getMvcContext() {
		return context;
	}

	public Throwable getError() {
		return error;
	}

	public boolean isHandled() {
		return handled;
	}

	public void setHandled(boolean value) {
		this.handled = value;
	}
}
