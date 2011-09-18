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
		mvcContext.ensureFilters();

		Throwable error = null;

		List<ResultFilter> renderChain = mvcContext.getResultFilters();
		int index = 0;
		try {
			for (int count=renderChain.size(); index<count; index++) {
				renderChain.get(index).onResultRendering(context);
			}

			invocation.proceed();

		} catch (Throwable ex) {
			error = ex;
		}

		for (index--; index>=0; index--) {
			try {
				renderChain.get(index).onResultRendered(context);

			} catch (Throwable ex) {
				// keep first error
				if (error == null) {
					error = ex;
				}
			}
		}

		if (error != null) {
			// this will rethrow if left unhandled
			ErrorFilterInterceptor.processErrors(mvcContext, error, null);
			return null;
		}

		// invocation is void method
		return null;
	}
}