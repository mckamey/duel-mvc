package org.duelengine.duel.mvc;

/**
 * Action filters intercept controller actions allowing AOP code to execute before and after the action executes
 */
public interface ActionFilter extends DuelMvcFilter {

	void onActionExecuting(ActionFilterContext context);

	void onActionExecuted(ActionFilterContext context);
}
