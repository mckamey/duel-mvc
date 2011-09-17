package org.duelengine.duel.mvc;

import org.duelengine.duel.DuelView;

/**
 * Guice assisted-injection will supply the implementation
 */
public interface ViewResultFactory {

	ViewResult create(Class<? extends DuelView> viewType);
}
