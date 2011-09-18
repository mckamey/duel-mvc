package org.duelengine.duel.mvc;

final class ErrorFilterInterceptor {

	public static Object processErrors(DuelMvcContext mvcContext, Throwable error, Object result) throws Throwable {
		mvcContext.ensureFilters();

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
