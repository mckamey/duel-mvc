package org.duelengine.duel.mvc;

public interface ExceptionFilter extends DuelMvcFilter {

	void onException(ExceptionFilterContext context);
}
