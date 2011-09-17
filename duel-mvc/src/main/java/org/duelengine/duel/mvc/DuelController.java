package org.duelengine.duel.mvc;

import org.duelengine.duel.DuelView;

import com.google.inject.Inject;

/**
 * Base class for all DUEL-MVC controllers.
 * Uses method injection so other controllers don't need to maintain Inject constructors.
 * Requires Guice.
 */
public abstract class DuelController {

	private ViewResultFactory factory;

	@Inject
	final void init_DuelController(
		ViewResultFactory factory) {

		this.factory = factory;
	}

	/**
	 * Builds a view result for the specified view
	 */
	protected ViewResult view(Class<? extends DuelView> view) {
		return factory.create(view);
	}
}
