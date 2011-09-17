package org.duelengine.duel.mvc;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;

/**
 * Manages the result filter chain
 */
final class ResultFilterInterceptor implements MethodInterceptor {

	private ResultFilterContextFactory factory;

	@Inject
	void init(ResultFilterContextFactory factory) {
		if (factory == null) {
			throw new NullPointerException("factory");
		}

		this.factory = factory;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		ResultFilterContext context = factory.create((ViewResult)invocation.getThis());
		DuelMvcContext mvcContext = context.getMvcContext();

		List<ResultFilter> chain = mvcContext.getResultFilters();

		Object result;
		try {
			for (ResultFilter filter : chain) {
				filter.onResultExecuting(context);
			}
			result = invocation.proceed();

		} catch (Throwable ex) {
			// TODO: call ExceptionFilter.onException()
			throw ex;

		} finally {
			for (ResultFilter filter : chain) {
				filter.onResultExecuted(context);
			}
		}

		return result;
	}
}