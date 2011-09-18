package org.duelengine.duel.mvc;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;

/**
 * Manages the result filter chain
 */
final class ResultFilterInterceptor extends ErrorFilterInterceptor implements MethodInterceptor {

	private ResultFilterContextFactory resultCxFactory;

	@Inject
	void init(ResultFilterContextFactory resultCxFactory, ErrorFilterContextFactory errorCxFactory) {
		if (resultCxFactory == null) {
			throw new NullPointerException("resultCxFactory");
		}

		this.resultCxFactory = resultCxFactory;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		ResultFilterContext context = resultCxFactory.create((ViewResult)invocation.getThis());
		DuelMvcContext mvcContext = context.getMvcContext();

		List<ResultFilter> renderChain = mvcContext.getResultFilters();

		Throwable error = null;
		int index = 0;
		try {
			for (int count=renderChain.size(); index<count; index++) {
				renderChain.get(index).onResultRendering(context);
			}

			invocation.proceed();

		} catch (Throwable ex) {
			error = ex;
		}

		for (index-=1; index>=0; index--) {
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
			processErrors(mvcContext.getErrorFilters(), error);
		}

		// invocation is void method
		return null;
	}
}