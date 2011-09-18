package org.duelengine.duel.mvc;

/**
 * Authorization filters intercept controller actions before the action or other filters execute
 */
public interface AuthFilter extends DuelMvcFilter {

	void onAuthorization(ActionFilterContext context);
}
