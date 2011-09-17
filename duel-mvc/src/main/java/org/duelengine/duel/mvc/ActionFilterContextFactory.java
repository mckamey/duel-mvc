package org.duelengine.duel.mvc;

import org.aopalliance.intercept.MethodInvocation;

interface ActionFilterContextFactory {

	ActionFilterContext create(MethodInvocation invocation);
}
