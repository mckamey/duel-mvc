package org.duelengine.duel.mvc;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import com.google.inject.Inject;

/**
 * Manages the action filter chain
 */
final class ActionFilterInterceptor implements MethodInterceptor {

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

		mvcContext.ensureFilters(invocation);

		List<ActionFilter> chain = mvcContext.getActionFilters();

		Object result;
		try {
			for (ActionFilter filter : chain) {
				filter.onActionExecuting(context);
			}
			result = invocation.proceed();

		} catch (Throwable ex) {
			// TODO: call ExceptionFilter.onException()
			throw ex;

		} finally {
			for (ActionFilter filter : chain) {
				filter.onActionExecuted(context);
			}
		}

		return result;
	}
}