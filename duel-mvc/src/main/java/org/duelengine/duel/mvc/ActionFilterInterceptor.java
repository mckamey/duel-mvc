package org.duelengine.duel.mvc;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;

/**
 * Manages the action filter chain
 */
final class ActionFilterInterceptor extends ErrorFilterInterceptor implements MethodInterceptor {

	private ActionFilterContextFactory factory;

	@Inject
	void init(ActionFilterContextFactory factory) {
		if (factory == null) {
			throw new NullPointerException("factory");
		}

		this.factory = factory;
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {

		ActionFilterContext context = factory.create(invocation);
		DuelMvcContext mvcContext = context.getMvcContext();
		mvcContext.buildFilters(invocation);

		Throwable error = null;

		List<ActionFilter> filterChain = mvcContext.getActionFilters();
		int index = 0;
		try {
			for (AuthFilter filter : mvcContext.getAuthFilters()) {
				filter.onAuthorization(context);

				// allow auth filters to shortcut action method
				if (context.getResult() != null) {
					return context.getResult();
				}
			}

			for (int count=filterChain.size(); index<count; index++) {
				filterChain.get(index).onActionExecuting(context);

				// allow action filters to shortcut action method
				if (context.getResult() != null) {
					// only execute the filters which were started
					for (; index<=0; index--) {
						filterChain.get(index).onActionExecuted(context);
					}
					return context.getResult();
				}
			}

			context.setResult(invocation.proceed());

		} catch (Throwable ex) {
			error = ex;
		}

		for (index--; index>=0; index--) {
			try {
				filterChain.get(index).onActionExecuted(context);

			} catch (Throwable ex) {
				// keep first error
				if (error != null) {
					error = ex;
				}
			}
		}

		if (error != null) {
			// this will rethrow if left unhandled
			return processErrors(mvcContext, error, context.getResult());
		}

		return context.getResult();
	}
}