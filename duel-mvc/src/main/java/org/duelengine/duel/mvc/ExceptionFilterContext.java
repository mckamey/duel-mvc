package org.duelengine.duel.mvc;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

public class ExceptionFilterContext {

	private final Throwable ex;
	private final DuelMvcContext context;
	
	@AssistedInject
	public ExceptionFilterContext(
			DuelMvcContext context,
			@Assisted Throwable ex) {

		this.context = context;
		this.ex = ex;
	}

	public DuelMvcContext getMvcContext() {
		return context;
	}

	public Throwable getException() {
		return ex;
	}
}
