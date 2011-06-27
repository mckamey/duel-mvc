package com.example.mvcapp.controllers;

import com.google.inject.*;
import org.duelengine.duel.*;
import org.duelengine.duel.rs.*;

/**
 * Base class for all controllers and provides access to helper methods and ambient request data.
 * Uses method injection so other controllers don't need to maintain constructors.
 */
public abstract class BaseController {

	private Provider<DuelContext> viewContextProvider;

	@Inject
	public void init(Provider<DuelContext> viewContextProvider) {
		this.viewContextProvider = viewContextProvider;
	}

	/**
	 * Builds a view result
	 */
	protected ViewResult view(Class<? extends DuelView> view) {
		return new ViewResult(view, this.viewContextProvider.get());
	}
}
