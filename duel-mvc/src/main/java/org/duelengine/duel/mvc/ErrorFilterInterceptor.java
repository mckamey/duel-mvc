package org.duelengine.duel.mvc;

import java.util.List;

import com.google.inject.Inject;

abstract class ErrorFilterInterceptor {

	private ErrorFilterContextFactory factory;

	@Inject
	void init_ErrorFilterInterceptor(ErrorFilterContextFactory factory) {
		if (factory == null) {
			throw new NullPointerException("factory");
		}

		this.factory = factory;
	}

	protected void processErrors(List<ErrorFilter> errorFilters, Throwable error) throws Throwable {
		ErrorFilterContext context = factory.create(error);

		for (ErrorFilter filter : errorFilters) {
			filter.onError(context);
		}

		// allow error filters to handle exception
		if (!context.isHandled()) {
			// unhandled exception
			throw error;
		}
	}
}
