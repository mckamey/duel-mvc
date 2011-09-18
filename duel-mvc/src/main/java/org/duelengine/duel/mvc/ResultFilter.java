package org.duelengine.duel.mvc;

/**
 * Result filters intercept actions results allowing AOP code to execute before and after the result executes
 */
public interface ResultFilter extends DuelMvcFilter {

	void onResultRendering(ResultFilterContext context);

	void onResultRendered(ResultFilterContext context);
}
