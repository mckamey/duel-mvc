package org.duelengine.duel.mvc;

abstract class ErrorFilterInterceptor {

	protected Object processErrors(DuelMvcContext mvcContext, Throwable error, Object result) throws Throwable {

		ErrorFilterContext context = new ErrorFilterContext(mvcContext);
		context.setError(error);
		context.setResult(result);

		for (ErrorFilter filter : mvcContext.getErrorFilters()) {
			filter.onError(context);
		}

		// allow error filters to handle exception
		if (!context.isHandled()) {
			// unhandled exception
			throw context.getError();
		}

		return context.getResult();
	}
}
