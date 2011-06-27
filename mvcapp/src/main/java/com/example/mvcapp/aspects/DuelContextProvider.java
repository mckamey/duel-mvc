package com.example.mvcapp.aspects;

import com.google.inject.*;
import com.google.inject.name.Named;
import org.duelengine.duel.DuelContext;

/**
 * Builds the view context for a request
 */
@Singleton
public class DuelContextProvider implements Provider<DuelContext> {

	private final boolean isDebug;

	@Inject
	public DuelContextProvider(
		@Named("DEBUG") boolean isDebug) {

		this.isDebug = isDebug;
	}

	public DuelContext get() {
		DuelContext context = new DuelContext();

		// add ambient client-side data
		if (this.isDebug) {
			context.getFormat().setIndent("\t").setNewline("\n");
			context.putExtra("App.isDebug", true);
		}

		return context;
	}
}