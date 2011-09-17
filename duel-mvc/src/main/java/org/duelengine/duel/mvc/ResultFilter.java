package org.duelengine.duel.mvc;

/**
 * Result filters intercept actions results allowing AOP code to execute before and after the result executes
 */
public interface ResultFilter extends DuelMvcFilter {

	void onResultExecuting(ResultFilterContext context);

	void onResultExecuted(ResultFilterContext context);
}
