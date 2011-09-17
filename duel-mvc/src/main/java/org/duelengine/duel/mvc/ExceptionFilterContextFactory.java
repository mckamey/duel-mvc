package org.duelengine.duel.mvc;

interface ExceptionFilterContextFactory {

	ExceptionFilterContext create(Throwable ex);
}
