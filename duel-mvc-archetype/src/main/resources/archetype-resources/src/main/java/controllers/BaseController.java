#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.controllers;

import java.util.logging.Logger;
import com.google.inject.*;
import com.google.inject.name.Named;
import org.duelengine.duel.*;
import org.duelengine.duel.rs.*;

import ${package}.aspects.TimedViewResult;

/**
 * Base class for all controllers and provides access to helper methods and ambient request data.
 * Uses method injection so other controllers don't need to maintain constructors.
 */
public abstract class BaseController {

	private final Logger log = Logger.getLogger(BaseController.class.getSimpleName());
	private double timingThreshold;
	private Provider<DuelContext> viewContextProvider;

	@Inject
	public void init(
		@Named("RENDER_THRESHOLD") double timingThreshold,
		Provider<DuelContext> viewContextProvider) {

		this.timingThreshold = timingThreshold;
		this.viewContextProvider = viewContextProvider;
	}

	/**
	 * Builds a view result
	 */
	protected ViewResult view(Class<? extends DuelView> view) {
		return new TimedViewResult(view, viewContextProvider.get(), log, timingThreshold);
	}
}
