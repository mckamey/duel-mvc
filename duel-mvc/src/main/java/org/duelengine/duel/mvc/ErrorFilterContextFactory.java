package org.duelengine.duel.mvc;

interface ErrorFilterContextFactory {

	ErrorFilterContext create(Throwable ex);
}
