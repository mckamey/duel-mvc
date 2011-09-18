package org.duelengine.duel.mvc;

public interface ErrorFilter extends DuelMvcFilter {

	void onError(ErrorFilterContext context);
}
