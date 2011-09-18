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
		List<AuthFilter> authChain = mvcContext.getAuthFilters();
		List<ActionFilter> actionChain = mvcContext.getActionFilters();

		Throwable error = null;
		int index = 0;
		try {
			for (int i=0, count=authChain.size(); i<count; i++) {
				authChain.get(i).onAuthorization(context);
	
				// allow auth filters to shortcut action method
				if (context.getResult() != null) {
					return context.getResult();
				}
			}

			for (int count=actionChain.size(); index<count; index++) {
				actionChain.get(index).onActionExecuting(context);
	
				// allow action filters to shortcut action method
				if (context.getResult() != null) {
					// only execute the filters which were started
					for (; index<=0; index--) {
						actionChain.get(index).onActionExecuted(context);
					}
					return context.getResult();
				}
			}

			context.setResult(invocation.proceed());

		} catch (Throwable ex) {
			error = ex;
		}

		for (index-=1; index>=0; index--) {
			try {
				actionChain.get(index).onActionExecuted(context);

			} catch (Throwable ex) {
				// keep first error
				if (error != null) {
					error = ex;
				}
			}
		}

		if (error != null) {
			processErrors(mvcContext.getErrorFilters(), error);
		}

		return context.getResult();
	}
}